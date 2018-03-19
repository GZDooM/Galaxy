package in.dragons.galaxy.task.playstore;

import android.app.DownloadManager;
import android.database.Cursor;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.yeriomin.playstoreapi.GooglePlayAPI;
import com.percolate.caffeine.ViewUtils;

import java.io.IOException;
import java.util.List;

import in.dragons.galaxy.BlackWhiteListManager;
import in.dragons.galaxy.BuildConfig;
import in.dragons.galaxy.R;
import in.dragons.galaxy.UpdatableAppsFragment;
import in.dragons.galaxy.model.App;

import static android.content.Context.DOWNLOAD_SERVICE;

public class ForegroundUpdatableAppsTaskHelper extends UpdatableAppsTask implements CloneableTask {

    private UpdatableAppsFragment activity;
    private Button update, cancel;
    private TextView textView;
    private DownloadManager.Query query;
    private DownloadManager dm;

    public ForegroundUpdatableAppsTaskHelper(UpdatableAppsFragment activity) {
        this.activity = activity;
        setContext(activity.getActivity());
    }

    @Override
    public CloneableTask clone() {
        ForegroundUpdatableAppsTaskHelper task = new ForegroundUpdatableAppsTaskHelper(this.activity);
        task.setErrorView(errorView);
        task.setProgressIndicator(progressIndicator);
        return task;
    }

    @Override
    protected List<App> getResult(GooglePlayAPI api, String... packageNames) throws IOException {
        super.getResult(api, packageNames);
        if (!new BlackWhiteListManager(context).isUpdatable(BuildConfig.APPLICATION_ID)) {
            return updatableApps;
        }
        return updatableApps;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.errorView.setText("");
    }

    @Override
    protected void onPostExecute(List<App> result) {
        super.onPostExecute(result);
        activity.clearApps();
        activity.addApps(updatableApps);

        if (success() && updatableApps.isEmpty())
            activity.getActivity().findViewById(R.id.unicorn).setVisibility(View.VISIBLE);
        else {
            setText(R.id.updates_txt, R.string.list_update_all_txt, updatableApps.size());
            setupButtons();
        }
    }

    private void setupButtons() {
        update = (Button) activity.getActivity().findViewById(R.id.update_all);
        cancel = (Button) activity.getActivity().findViewById(R.id.update_cancel);

        update.setOnClickListener(v -> {
            activity.launchUpdateAll();
            update.setVisibility(View.GONE);
            cancel.setVisibility(View.VISIBLE);
            textView.setText(R.string.list_updating);
        });

        cancel.setOnClickListener(v -> {
            query = new DownloadManager.Query();
            query.setFilterByStatus(DownloadManager.STATUS_PENDING | DownloadManager.STATUS_RUNNING);
            dm = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
            Cursor c = dm.query(query);
            while (c.moveToNext() == true) {
                dm.remove(c.getLong(c.getColumnIndex(DownloadManager.COLUMN_ID)));
            }
            update.setVisibility(View.VISIBLE);
            cancel.setVisibility(View.GONE);
            setText(R.id.updates_txt, R.string.list_update_all_txt, updatableApps.size());
        });
    }

    protected void setText(int viewId, String text) {
        TextView textView = ViewUtils.findViewById(activity.getActivity(), viewId);
        if (null != textView)
            textView.setText(text);
    }

    protected void setText(int viewId, int stringId, Object... text) {
        setText(viewId, activity.getString(stringId, text));
    }
}

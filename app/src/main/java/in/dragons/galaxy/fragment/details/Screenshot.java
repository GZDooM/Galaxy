package in.dragons.galaxy.fragment.details;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import in.dragons.galaxy.R;
import in.dragons.galaxy.activities.DetailsActivity;
import in.dragons.galaxy.adapters.SmallScreenshotsAdapter;
import in.dragons.galaxy.model.App;

public class Screenshot extends Abstract {

    public Screenshot(DetailsActivity activity, App app) {
        super(activity, app);
    }

    @Override
    public void draw() {
        if (app.getScreenshotUrls().size() > 0) {
            drawGallery();
        } else {
            return;
        }
    }

    private void drawGallery() {
        List<SmallScreenshotsAdapter.Holder> SSAdapter = new ArrayList<>();
        RecyclerView gallery = activity.findViewById(R.id.screenshots_gallery);
        gallery.setNestedScrollingEnabled(false);
        SmallScreenshotsAdapter adapter = new SmallScreenshotsAdapter(SSAdapter, activity);
        for (int i = 0; i < app.getScreenshotUrls().size(); i++)
        SSAdapter.add(new SmallScreenshotsAdapter.Holder(app.getScreenshotUrls()));
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
        gallery.setAdapter(adapter);
        gallery.setLayoutManager(layoutManager);
    }
}
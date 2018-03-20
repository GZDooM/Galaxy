package in.dragons.galaxy;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.File;

import in.dragons.galaxy.model.App;
import in.dragons.galaxy.notification.NotificationManagerWrapper;

public class GlobalDownloadReceiver extends DownloadReceiver {

    private NotificationManagerWrapper notificationManager;

    @Override
    protected void process(Context c, Intent i) {
        notificationManager = new NotificationManagerWrapper(c);
        App app = state.getApp();

        DownloadManagerInterface dm = DownloadManagerFactory.get(context);
        if (!dm.finished(downloadId)) {
            return;
        }
        boolean patchSuccess = false;
        if (isDelta(app)) {
            patchSuccess = DeltaPatcherFactory.get(context, app).patch();
            if (!patchSuccess) {
                Log.e(getClass().getSimpleName(), "Delta patching failed for " + app.getPackageName());
                return;
            }
        }
        state.setFinished(downloadId);
        if (actionIs(i, DownloadManagerInterface.ACTION_DOWNLOAD_CANCELLED)) {
            return;
        } else if (dm.success(downloadId)) {
            state.setSuccessful(downloadId);
        } else {
            String error = dm.getError(downloadId);
            ContextUtil.toastLong(context.getApplicationContext(), error);
            notificationManager.show(new Intent(), app.getDisplayName(), error);
        }

        if (!state.isEverythingFinished() || !state.isEverythingSuccessful()) {
            return;
        }
        verifyAndInstall(app, state.getTriggeredBy());
        if (patchSuccess) {
            Intent patchingCompleteIntent = new Intent(ACTION_DELTA_PATCHING_COMPLETE);
            patchingCompleteIntent.putExtra(DownloadManagerInterface.EXTRA_DOWNLOAD_ID, downloadId);
            context.sendBroadcast(patchingCompleteIntent);
        }
    }

    private void verifyAndInstall(App app, DownloadState.TriggeredBy triggeredBy) {
        if (shouldInstall(triggeredBy)) {
            Log.i(getClass().getSimpleName(), "Launching installer for " + app.getPackageName());
            InstallerAbstract installer = InstallerFactory.get(context);
            if (triggeredBy.equals(DownloadState.TriggeredBy.DOWNLOAD_BUTTON)
                    && PreferenceFragment.getBoolean(context, PreferenceFragment.PREFERENCE_AUTO_INSTALL)
                    ) {
                installer.setBackground(false);
            }
            installer.verifyAndInstall(app);
        } else {
            Log.i(getClass().getSimpleName(), "Notifying about download completion of " + app.getPackageName());
            notifyDownloadComplete(app);
            ((GalaxyApplication) context.getApplicationContext()).removePendingUpdate(app.getPackageName());
        }
    }

    private void notifyDownloadComplete(App app) {
        notifyAndToast(
                R.string.notification_download_complete,
                R.string.notification_download_complete_toast,
                app
        );
    }

    private void notifyAndToast(int notificationStringId, int toastStringId, App app) {
        File file = Paths.getApkPath(context, app.getPackageName(), app.getVersionCode());
        Intent openApkIntent = InstallerAbstract.getOpenApkIntent(context, file);
        notificationManager.show(
                openApkIntent,
                app.getDisplayName(),
                context.getString(notificationStringId)
        );
        ContextUtil.toast(context.getApplicationContext(), toastStringId, app.getDisplayName());
    }

    private boolean shouldInstall(DownloadState.TriggeredBy triggeredBy) {
        switch (triggeredBy) {
            case DOWNLOAD_BUTTON:
                return PreferenceFragment.getBoolean(context, PreferenceFragment.PREFERENCE_AUTO_INSTALL);
            case UPDATE_ALL_BUTTON:
                return PreferenceFragment.canInstallInBackground(context);
            case SCHEDULED_UPDATE:
                return PreferenceFragment.canInstallInBackground(context) && PreferenceFragment.getBoolean(context, PreferenceFragment.PREFERENCE_BACKGROUND_UPDATE_INSTALL);
            case MANUAL_DOWNLOAD_BUTTON:
            default:
                return false;
        }
    }
}

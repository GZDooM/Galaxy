package in.dragons.galaxy.task;

import android.os.AsyncTask;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;

import eu.chainfire.libsuperuser.Shell;
import in.dragons.galaxy.ContextUtil;
import in.dragons.galaxy.PreferenceFragment;
import in.dragons.galaxy.R;

public class CheckSuTask extends AsyncTask<Void, Void, Void> {

    protected PreferenceFragment activity;
    protected boolean available;

    public CheckSuTask(PreferenceFragment activity) {
        this.activity = activity;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (!available) {
            ((CheckBoxPreference) activity.findPreference(PreferenceFragment.PREFERENCE_BACKGROUND_UPDATE_INSTALL)).setChecked(false);
            ((ListPreference) activity.findPreference(PreferenceFragment.PREFERENCE_INSTALLATION_METHOD)).setValueIndex(0);
            ContextUtil.toast(activity. getActivity().getApplicationContext(), R.string.pref_no_root);
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        available = Shell.SU.available();
        return null;
    }
}

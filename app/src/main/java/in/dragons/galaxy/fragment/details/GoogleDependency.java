package in.dragons.galaxy.fragment.details;

import android.annotation.SuppressLint;
import android.preference.PreferenceManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import in.dragons.galaxy.DetailsFragment;
import in.dragons.galaxy.SharedPreferencesTranslator;
import in.dragons.galaxy.model.App;
import in.dragons.galaxy.task.playstore.DependencyTranslationTask;

public class GoogleDependency extends AbstractHelper {

    private SharedPreferencesTranslator translator;

    public GoogleDependency(DetailsFragment detailsFragment, App app) {
        super(detailsFragment, app);
        translator = new SharedPreferencesTranslator(PreferenceManager.getDefaultSharedPreferences(detailsFragment.getActivity()));
    }

    @Override
    public void draw() {
        Set<String> untranslated = new HashSet<>();
        Set<String> translated = getTranslatedDeps(app);
        for (String dependency : translated) {
            if (app.getDependencies().contains(dependency)) {
                untranslated.add(dependency);
            }
        }

        if (untranslated.size() > 0) {
            getTranslations(untranslated);
        }
    }

    private Set<String> getTranslatedDeps(App app) {
        Set<String> translated = new HashSet<>();
        for (String dependency : app.getDependencies()) {
            translated.add(translator.getString(dependency));
        }
        return translated;
    }

    private void getTranslations(Set<String> untranslated) {
        @SuppressLint("StaticFieldLeak")
        DependencyTranslationTask task = new DependencyTranslationTask() {

            @Override
            protected void onPostExecute(List<App> apps) {
                super.onPostExecute(apps);
                if (!success()) {
                    return;
                }
                for (String packageName : translated.keySet()) {
                    translator.putString(packageName, translated.get(packageName));
                }
            }
        };
        task.setContext(detailsFragment.getActivity());
        task.execute(untranslated.toArray(new String[untranslated.size()]));
    }
}
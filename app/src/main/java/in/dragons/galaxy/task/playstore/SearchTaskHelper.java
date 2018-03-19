package in.dragons.galaxy.task.playstore;

import android.content.Context;
import android.content.pm.PackageInfo;

import com.github.yeriomin.playstoreapi.SearchIterator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import in.dragons.galaxy.AppListIterator;
import in.dragons.galaxy.CategoryManager;
import in.dragons.galaxy.PlayStoreApiAuthenticator;
import in.dragons.galaxy.model.App;

public class SearchTaskHelper extends EndlessScrollTaskHelper implements CloneableTask {

    static private Set<String> installedPackageNames = new HashSet<>();

    private CategoryManager categoryManager;
    private String query;

    public SearchTaskHelper(AppListIterator iterator) {
        super(iterator);
    }

    public void setQuery(String query) {
        this.query = query;
    }

    @Override
    public CloneableTask clone() {
        SearchTaskHelper task = new SearchTaskHelper(iterator);
        task.setFilter(filter);
        task.setQuery(query);
        task.setErrorView(errorView);
        task.setContext(context);
        task.setProgressIndicator(progressIndicator);
        return task;
    }

    @Override
    protected AppListIterator initIterator() throws IOException {
        return new AppListIterator(new SearchIterator(new PlayStoreApiAuthenticator(context).getApi(), query));
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        categoryManager = new CategoryManager(context);
        if (installedPackageNames.isEmpty()) {
            installedPackageNames = getInstalledPackageNames(context);
        }
    }

    @Override
    protected void onPostExecute(List<App> apps) {
        super.onPostExecute(apps);
        if (success()) {
            BackgroundCategoryTask task = new BackgroundCategoryTask();
            task.setContext(context);
            task.setManager(categoryManager);
            task.execute();
        }
    }

    @Override
    protected List<App> getNextBatch(AppListIterator iterator) {
        List<App> apps = new ArrayList<>();
        for (App app : iterator.next()) {
            app.setInstalled(installedPackageNames.contains(app.getPackageName()));
            if (categoryManager.fits(app.getCategoryId(), filter.getCategory())) {
                apps.add(app);
            }
        }
        return apps;
    }

    static private Set<String> getInstalledPackageNames(Context context) {
        Set<String> newList = new HashSet<>();
        try {
            for (PackageInfo reducedPackageInfo : context.getPackageManager().getInstalledPackages(0)) {
                newList.add(reducedPackageInfo.packageName);
            }
        } catch (RuntimeException e) {
            // TransactionTooLargeException might happen if the user has too many apps
            // Marking apps as installed in search is not very important, so lets ignore this
        }
        return newList;
    }
}

/*
 * Aurora Store
 * Copyright (C) 2018  Rahul Kumar Patel <whyorean@gmail.com>
 *
 * Yalp Store
 * Copyright (C) 2018 Sergey Yeriomin <yeriomin@gmail.com>
 *
 * Aurora Store (a fork of Yalp Store )is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * Aurora Store is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Aurora Store.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.dragons.aurora.task.playstore;

import android.util.Log;

import com.dragons.aurora.AppListIterator;
import com.dragons.aurora.PlayStoreApiAuthenticator;
import com.dragons.aurora.model.App;
import com.dragons.aurora.model.Filter;
import com.dragons.aurora.playstoreapiv2.GooglePlayException;
import com.dragons.aurora.playstoreapiv2.IteratorGooglePlayException;
import com.dragons.aurora.task.AppProvidedCredentialsTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

abstract public class CategoryAppsTask extends ExceptionTask {

    protected Filter filter;
    protected AppListIterator iterator;

    public AppListIterator getIterator() {
        return iterator;
    }

    public void setIterator(AppListIterator iterator) {
        try {
            iterator.setGooglePlayApi(new PlayStoreApiAuthenticator(getContext()).getApi());
        } catch (IOException e) {
            Log.e(getClass().getSimpleName(), "Building an api object from preferences failed");
        }
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    protected List<App> getResult(AppListIterator iterator) throws IOException {

        setIterator(iterator);

        if (!iterator.hasNext()) {
            return new ArrayList<>();
        }

        List<App> apps = new ArrayList<>();
        while (iterator.hasNext() && apps.isEmpty()) {
            try {
                apps.addAll(getNextBatch(iterator));
            } catch (IteratorGooglePlayException e) {
                if (null == e.getCause()) {
                    continue;
                }
                if (noNetwork(e.getCause())) {
                    throw (IOException) e.getCause();
                } else if (e.getCause() instanceof GooglePlayException
                        && ((GooglePlayException) e.getCause()).getCode() == 401 && isDummy()) {
                    new AppProvidedCredentialsTask(getContext()).refreshToken();
                    iterator.setGooglePlayApi(new PlayStoreApiAuthenticator(getContext()).getApi());
                    apps.addAll(getNextBatch(iterator));
                }
            }
        }
        return apps;
    }

    protected List<App> getNextBatch(AppListIterator iterator) {
        return iterator.next();
    }
}

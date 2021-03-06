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

package com.dragons.aurora.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.dragons.aurora.CategoryManager;
import com.dragons.aurora.model.Filter;

public class FilterMenu {

    static private final String FILTER_SYSTEM_APPS = "FILTER_SYSTEM_APPS";
    static private final String FILTER_APPS_WITH_ADS = "FILTER_APPS_WITH_ADS";
    static private final String FILTER_PAID_APPS = "FILTER_PAID_APPS";
    static private final String FILTER_GSF_DEPENDENT_APPS = "FILTER_GSF_DEPENDENT_APPS";
    static private final String FILTER_CATEGORY = "FILTER_CATEGORY";
    static private final String FILTER_RATING = "FILTER_RATING";
    static private final String FILTER_DOWNLOADS = "FILTER_DOWNLOADS";

    private Context context;

    public FilterMenu(Context context) {
        this.context = context;
    }

    public Filter getFilterPreferences() {
        Filter filter = new Filter();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        filter.setSystemApps(prefs.getBoolean(FILTER_SYSTEM_APPS, false));
        filter.setAppsWithAds(prefs.getBoolean(FILTER_APPS_WITH_ADS, true));
        filter.setPaidApps(prefs.getBoolean(FILTER_PAID_APPS, true));
        filter.setGsfDependentApps(prefs.getBoolean(FILTER_GSF_DEPENDENT_APPS, true));
        filter.setCategory(prefs.getString(FILTER_CATEGORY, CategoryManager.TOP));
        filter.setRating(prefs.getFloat(FILTER_RATING, 0.0f));
        filter.setDownloads(prefs.getInt(FILTER_DOWNLOADS, 0));
        return filter;
    }

    public void resetFilterPreferences() {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(FILTER_SYSTEM_APPS, false).apply();
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(FILTER_APPS_WITH_ADS, true).apply();
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(FILTER_PAID_APPS, true).apply();
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(FILTER_GSF_DEPENDENT_APPS, true).apply();
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(FILTER_CATEGORY, CategoryManager.TOP).apply();
        PreferenceManager.getDefaultSharedPreferences(context).edit().putFloat(FILTER_RATING, 0.0f).apply();
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(FILTER_DOWNLOADS, 0).apply();
    }
}

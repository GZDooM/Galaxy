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

package com.dragons.aurora.fragment.details;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import com.dragons.aurora.R;
import com.dragons.aurora.fragment.DetailsFragment;
import com.dragons.aurora.model.App;
import com.dragons.aurora.task.playstore.PurchaseTask;
import com.percolate.caffeine.ViewUtils;

public class BackToPlayStore extends AbstractHelper {

    static private final String PLAY_STORE_PACKAGE_NAME = "com.android.vending";

    public BackToPlayStore(DetailsFragment fragment, App app) {
        super(fragment, app);
    }

    @Override
    public void draw() {
        if (!isPlayStoreInstalled() || !app.isInPlayStore()) {
            return;
        }
        ViewUtils.findViewById(fragment.getActivity(), R.id.to_play_store_cnt).setVisibility(View.VISIBLE);
        ImageView toPlayStore = (ImageView) fragment.getActivity().findViewById(R.id.to_play_store);
        toPlayStore.setVisibility(View.VISIBLE);
        toPlayStore.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(PurchaseTask.URL_PURCHASE + app.getPackageName()));
            fragment.getActivity().startActivity(i);
        });
    }

    private boolean isPlayStoreInstalled() {
        try {
            return null != fragment.getActivity().getPackageManager().getPackageInfo(PLAY_STORE_PACKAGE_NAME, 0);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
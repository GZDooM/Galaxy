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

package com.dragons.aurora;

import android.content.Context;
import android.util.Log;

import com.dragons.aurora.model.App;

public class InstallerDefault extends InstallerAbstract {

    public InstallerDefault(Context context) {
        super(context);
    }

    @Override
    public boolean verify(App app) {
        if (background) {
            Log.i(getClass().getSimpleName(), "Background installation is not supported by default installer");
            return false;
        }
        return super.verify(app);
    }

    @Override
    protected void install(App app) {
        InstallationState.setSuccess(app.getPackageName());
        context.startActivity(
                InstallerAbstract.getOpenApkIntent(
                        context,
                        Paths.getApkPath(context, app.getPackageName(), app.getVersionCode())
                )
        );
    }
}

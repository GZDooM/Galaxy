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

package com.dragons.aurora.task;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import com.dragons.aurora.PlayStoreApiAuthenticator;
import com.dragons.aurora.R;
import com.dragons.aurora.activities.AccountsActivity;
import com.dragons.aurora.activities.AuroraActivity;
import com.dragons.aurora.activities.DetailsActivity;
import com.dragons.aurora.fragment.DetailsFragment;
import com.dragons.aurora.fragment.InstalledAppsFragment;
import com.dragons.aurora.fragment.UpdatableAppsFragment;

import java.io.IOException;

public class AppProvidedCredentialsTask extends CheckCredentialsTask {

    private Context context;

    public AppProvidedCredentialsTask(Context context) {
        this.context = context;
    }

    protected void payload() throws IOException {
    }

    public void logInWithPredefinedAccount() {
        LoginTask task = new LoginTask(context);
        task.setContext(context);
        task.prepareDialog(R.string.dialog_message_logging_in_predefined, R.string.dialog_title_logging_in);
        task.execute();
    }

    public void refreshToken() {
        RefreshTokenTask task = new RefreshTokenTask(context);
        task.setContext(context);
        task.execute();
    }

    @Override
    protected Void doInBackground(String[] params) {
        try {
            payload();
        } catch (IOException e) {
            exception = e;
        }
        return null;
    }

    public static class RefreshTokenTask extends AppProvidedCredentialsTask {

        private Context context;

        public RefreshTokenTask(Context context) {
            super(context);
            this.context = context;
        }

        @Override
        protected void payload() throws IOException {
            new PlayStoreApiAuthenticator(context).refreshToken();
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (success()) {
                if (context instanceof AccountsActivity)
                    ((AccountsActivity) context).notifyTokenRefreshed();
                else if (context instanceof DetailsActivity)
                    DetailsFragment.newInstance();
                else if (context instanceof AuroraActivity) {
                    UpdatableAppsFragment.newInstance();
                    InstalledAppsFragment.newInstance();
                } else
                    Log.i(getClass().getSimpleName(), "Token Refreshed");
            }
        }
    }

    public static class LoginTask extends AppProvidedCredentialsTask {

        private Context context;

        public LoginTask(Context context) {
            super(context);
            this.context = context;
        }

        @Override
        protected void payload() throws IOException {
            new PlayStoreApiAuthenticator(context).login();
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (success()) {
                PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("LOGGED_IN", true).apply();
                PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("DUMMY_ACC", true).apply();
                PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("GOOGLE_ACC", false).apply();
                if (context instanceof AccountsActivity)
                    ((AccountsActivity) context).userChanged();
            }
        }
    }
}

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

package com.dragons.aurora.fragment.preference;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.util.Log;

import com.dragons.aurora.AuroraPermissionManager;
import com.dragons.aurora.ContextUtil;
import com.dragons.aurora.Paths;
import com.dragons.aurora.R;
import com.dragons.aurora.fragment.PreferenceFragment;

import java.io.File;
import java.io.IOException;

public class DownloadDirectory extends Abstract {

    private EditTextPreference preference;

    public DownloadDirectory(PreferenceFragment activity) {
        super(activity);
    }

    public DownloadDirectory setPreference(EditTextPreference preference) {
        this.preference = preference;
        return this;
    }

    @Override
    public void draw() {
        preference.setSummary(Paths.getDownloadPath(activity.getActivity()).getAbsolutePath());
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AuroraPermissionManager permissionManager = new AuroraPermissionManager(activity.getActivity());
                if (!permissionManager.checkPermission()) {
                    permissionManager.requestPermission();
                }
                return true;
            }
        });
        preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                String newValue = (String) o;
                boolean result = checkNewValue(newValue);
                if (!result) {
                    if (ContextUtil.isAlive(activity.getActivity()) && !((EditTextPreference) preference).getText().equals(Paths.FALLBACK_DIRECTORY)) {
                        getFallbackDialog().show();
                    } else {
                        ContextUtil.toast(activity.getActivity(), R.string.error_downloads_directory_not_writable);
                    }
                } else {
                    try {
                        preference.setSummary(new File(Paths.getStorageRoot(activity.getActivity()), newValue).getCanonicalPath());
                    } catch (IOException e) {
                        Log.i(getClass().getName(), "checkNewValue returned true, but drawing the path \"" + newValue + "\" in the summary failed... strange");
                        return false;
                    }
                }
                return result;
            }

            private boolean checkNewValue(String newValue) {
                try {
                    File storageRoot = Paths.getStorageRoot(activity.getActivity());
                    File newDir = new File(storageRoot, newValue).getCanonicalFile();
                    if (!newDir.getCanonicalPath().startsWith(storageRoot.getCanonicalPath())) {
                        return false;
                    }
                    if (newDir.exists()) {
                        return newDir.canWrite();
                    }
                    if (activity.getActivity().checkCallingOrSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        return newDir.mkdirs();
                    }
                    return true;
                } catch (IOException e) {
                    return false;
                }
            }

            private AlertDialog getFallbackDialog() {
                return new AlertDialog.Builder(activity.getActivity())
                        .setMessage(
                                activity.getString(R.string.error_downloads_directory_not_writable)
                                        + "\n\n"
                                        + activity.getString(R.string.pref_message_fallback, Paths.FALLBACK_DIRECTORY)
                        )
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                preference.setText(Paths.FALLBACK_DIRECTORY);
                                preference.getOnPreferenceChangeListener().onPreferenceChange(preference, Paths.FALLBACK_DIRECTORY);
                                dialog.dismiss();
                            }
                        })
                        .create()
                        ;
            }
        });
    }
}

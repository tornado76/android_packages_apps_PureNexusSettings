/*
 * Copyright (C) 2015 The Pure Nexus Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.purenexussettings;

import android.content.ContentResolver;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.provider.Settings;

import android.preference.PreferenceScreen;
import android.support.annotation.NonNull;

public class BatteryFragment extends PreferenceFragment
        implements OnPreferenceChangeListener {
    public BatteryFragment(){}

    private static final String STATUS_BAR_SHOW_BATTERY_PERCENT = "status_bar_show_battery_percent";

    private ListPreference mStatusBarBattery;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.battery_fragment);

        ContentResolver resolver = getActivity().getContentResolver();

        mStatusBarBattery = (ListPreference) findPreference(STATUS_BAR_SHOW_BATTERY_PERCENT);

        int batteryStyle = Settings.System.getInt(
                resolver, Settings.System.STATUS_BAR_SHOW_BATTERY_PERCENT, 0);
        mStatusBarBattery.setValue(String.valueOf(batteryStyle));
        mStatusBarBattery.setSummary(mStatusBarBattery.getEntry());
        mStatusBarBattery.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mStatusBarBattery) {
            int batteryStyle = Integer.valueOf((String) newValue);
            int index = mStatusBarBattery.findIndexOfValue((String) newValue);
            Settings.System.putInt(
                    resolver, Settings.System.STATUS_BAR_SHOW_BATTERY_PERCENT, batteryStyle);
            mStatusBarBattery.setSummary(mStatusBarBattery.getEntries()[index]);
            return true;
        }
        return false;
    }
}

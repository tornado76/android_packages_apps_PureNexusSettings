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
import android.preference.PreferenceScreen;
import android.provider.Settings;

import com.android.purenexussettings.preferences.ColorPickerPreference;

public class BatteryFragment extends PreferenceFragment
        implements OnPreferenceChangeListener {
    public BatteryFragment(){}

    private static final String STATUS_BAR_BATTERY_STYLE = "status_bar_battery_style";
    private static final String STATUS_BAR_SHOW_BATTERY_PERCENT = "status_bar_show_battery_percent";
    private static final String PREF_BATT_ICON_COLOR = "battery_icon_color";

    private static final int STATUS_BAR_BATTERY_STYLE_HIDDEN = 4;
    private static final int STATUS_BAR_BATTERY_STYLE_TEXT = 6;

    private ListPreference mStatusBarBattery;
    private ListPreference mStatusBarBatteryShowPercent;
    private ColorPickerPreference mBatteryIconColor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.battery_fragment);

        ContentResolver resolver = getActivity().getContentResolver();
        PreferenceScreen prefSet = getPreferenceScreen();

        // Status bar battery
        mStatusBarBattery = (ListPreference) findPreference(STATUS_BAR_BATTERY_STYLE);
        mStatusBarBatteryShowPercent =
                (ListPreference) findPreference(STATUS_BAR_SHOW_BATTERY_PERCENT);

        int batteryStyle = Settings.System.getInt(resolver,
                Settings.System.STATUS_BAR_BATTERY_STYLE, 0);
        mStatusBarBattery.setValue(String.valueOf(batteryStyle));
        mStatusBarBattery.setSummary(mStatusBarBattery.getEntry());
        mStatusBarBattery.setOnPreferenceChangeListener(this);

        int batteryShowPercent = Settings.System.getInt(resolver,
                Settings.System.STATUS_BAR_SHOW_BATTERY_PERCENT, 0);
        mStatusBarBatteryShowPercent.setValue(String.valueOf(batteryShowPercent));
        mStatusBarBatteryShowPercent.setSummary(mStatusBarBatteryShowPercent.getEntry());
        mStatusBarBatteryShowPercent.setOnPreferenceChangeListener(this);

        mBatteryIconColor = (ColorPickerPreference) findPreference(PREF_BATT_ICON_COLOR);
        mBatteryIconColor.setOnPreferenceChangeListener(this);
        mBatteryIconColor.setSummary(mBatteryIconColor.getSummaryText() + ColorPickerPreference.convertToARGB(Settings.System.getInt(resolver,
                     Settings.System.STATUS_BAR_BATTERY_COLOR, mBatteryIconColor.getPrefDefault())));
        mBatteryIconColor.setNewPreviewColor(Settings.System.getInt(resolver, Settings.System.STATUS_BAR_BATTERY_COLOR, mBatteryIconColor.getPrefDefault()));

        enableStatusBarBatteryDependents(batteryStyle);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mStatusBarBattery) {
            int batteryStyle = Integer.valueOf((String) objValue);
            int index = mStatusBarBattery.findIndexOfValue((String) objValue);
            Settings.System.putInt(
                    resolver, Settings.System.STATUS_BAR_BATTERY_STYLE, batteryStyle);
            mStatusBarBattery.setSummary(mStatusBarBattery.getEntries()[index]);
            enableStatusBarBatteryDependents(batteryStyle);
            return true;
        } else if (preference == mStatusBarBatteryShowPercent) {
            int batteryShowPercent = Integer.valueOf((String) objValue);
            int index = mStatusBarBatteryShowPercent.findIndexOfValue((String) objValue);
            Settings.System.putInt(
                    resolver, Settings.System.STATUS_BAR_SHOW_BATTERY_PERCENT, batteryShowPercent);
            mStatusBarBatteryShowPercent.setSummary(
                    mStatusBarBatteryShowPercent.getEntries()[index]);
            return true;
        } else if (preference == mBatteryIconColor) {
            Settings.System.putInt(getActivity().getContentResolver(), Settings.System.STATUS_BAR_BATTERY_COLOR, (Integer) objValue);
            preference.setSummary(((ColorPickerPreference) preference).getSummaryText() + ColorPickerPreference.convertToARGB((Integer) objValue));
            return true;
        }
        return false;
    }

    private void enableStatusBarBatteryDependents(int batteryIconStyle) {
        if (batteryIconStyle == STATUS_BAR_BATTERY_STYLE_HIDDEN) {
            mStatusBarBatteryShowPercent.setEnabled(false);
            mBatteryIconColor.setEnabled(false);
            mBatteryIconColor.setPreviewDim(false);
        } else if (batteryIconStyle == STATUS_BAR_BATTERY_STYLE_TEXT) {
            mStatusBarBatteryShowPercent.setEnabled(false);
            mBatteryIconColor.setEnabled(true);
            mBatteryIconColor.setPreviewDim(true);
        } else {
            mStatusBarBatteryShowPercent.setEnabled(true);
            mBatteryIconColor.setEnabled(true);
            mBatteryIconColor.setPreviewDim(true);
        }
    }
}

/*
 * Copyright (C) 2016 The Pure Nexus Project
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
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.provider.Settings;

public class LockscreenItemsFragment extends PreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    public LockscreenItemsFragment(){}

    private static final String LOCK_CLOCK_FONTS = "lock_clock_fonts";
    private static final String PREF_CONDITION_ICON = "weather_condition_icon";

    private ListPreference mLockClockFonts;
    private ListPreference mConditionIcon;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.lockscreen_items_fragment);

        PreferenceScreen prefScreen = getPreferenceScreen();
        ContentResolver resolver = getActivity().getContentResolver();

        mLockClockFonts = (ListPreference) findPreference(LOCK_CLOCK_FONTS);
        mLockClockFonts.setValue(String.valueOf(Settings.System.getInt(
                resolver, Settings.System.LOCK_CLOCK_FONTS, 4)));
        mLockClockFonts.setSummary(mLockClockFonts.getEntry());
        mLockClockFonts.setOnPreferenceChangeListener(this);

        mConditionIcon = (ListPreference) findPreference(PREF_CONDITION_ICON);
        int conditionIcon = Settings.System.getInt(resolver,
               Settings.System.LOCK_SCREEN_WEATHER_CONDITION_ICON, 0);
        mConditionIcon.setValue(String.valueOf(conditionIcon));
        mConditionIcon.setSummary(mConditionIcon.getEntry());
        mConditionIcon.setOnPreferenceChangeListener(this);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mLockClockFonts) {
            Settings.System.putInt(resolver, Settings.System.LOCK_CLOCK_FONTS,
                    Integer.valueOf((String) newValue));
            mLockClockFonts.setValue(String.valueOf(newValue));
            mLockClockFonts.setSummary(mLockClockFonts.getEntry());
            return true;
        }
        if (preference == mConditionIcon) {
            int intValue = Integer.valueOf((String) newValue);
            int index = mConditionIcon.findIndexOfValue((String) newValue);
            Settings.System.putInt(resolver,
                    Settings.System.LOCK_SCREEN_WEATHER_CONDITION_ICON, intValue);
            mConditionIcon.setSummary(mConditionIcon.getEntries()[index]);
            return true;
        }
        return false;
    }
}

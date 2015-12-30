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

import android.os.Bundle;
import android.os.UserHandle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

public class DisplayFragment extends PreferenceFragment {
    private static final String TAG = "DisplayFragment";

    public DisplayFragment(){}

    private static final String KEY_NOTIFICATION_LIGHT = "notification_light";
    private static final String KEY_BATTERY_LIGHT = "battery_light";

    private static final String CATEGORY_LEDS = "leds";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.display_fragment);

        final PreferenceCategory leds = (PreferenceCategory)
                findPreference(CATEGORY_LEDS);
		initPulse(leds);
    }

    private void initPulse(PreferenceCategory parent) {
        if (!getResources().getBoolean(
                com.android.internal.R.bool.config_intrusiveNotificationLed)) {
            parent.removePreference(parent.findPreference(KEY_NOTIFICATION_LIGHT));
        }
        if (!getResources().getBoolean(
                com.android.internal.R.bool.config_intrusiveBatteryLed)
                || UserHandle.myUserId() != UserHandle.USER_OWNER) {
            parent.removePreference(parent.findPreference(KEY_BATTERY_LIGHT));
        }
    }
}

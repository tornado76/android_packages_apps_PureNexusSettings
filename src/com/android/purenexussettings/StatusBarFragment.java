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
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.annotation.NonNull;

public class StatusBarFragment extends PreferenceFragment {
    private static final String BATTERYFRAG = "batteryfrag";
    private static final String CARRIERLABELFRAG = "carrierlabelfrag";
    private static final String CLOCKDATEFRAG = "clockdatefrag";
    private static final String NETWORKTRAFFRAG = "nettraffrag";

    private Preference mBattery;
    private Preference mCarrierLabel;
    private Preference mClockDate;
    private Preference mNetTraf;

    public StatusBarFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.statusbar_fragment);

        mBattery = (Preference)findPreference(BATTERYFRAG);
        mCarrierLabel = (Preference)findPreference(CARRIERLABELFRAG);
        mClockDate = (Preference)findPreference(CLOCKDATEFRAG);
        mNetTraf = (Preference)findPreference(NETWORKTRAFFRAG);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen prefScreen, @NonNull Preference pref) {
        if (pref == mBattery) {
            ((TinkerActivity)getActivity()).displaySubFrag(getString(R.string.battery_frag_title));

            return true;
        }

        if (pref == mCarrierLabel) {
            ((TinkerActivity)getActivity()).displaySubFrag(getString(R.string.carrierlabelfrag_title));

            return true;
        }

        if (pref == mClockDate) {
            ((TinkerActivity)getActivity()).displaySubFrag(getString(R.string.clockdate_frag_title));

            return true;
        }

        if (pref == mNetTraf) {
            ((TinkerActivity)getActivity()).displaySubFrag(getString(R.string.nettraffic_frag_title));

            return true;
        }

        return false;
    }
}

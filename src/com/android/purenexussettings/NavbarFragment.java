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

import android.content.res.Resources;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.provider.Settings;

public class NavbarFragment extends PreferenceFragment {

    private static final String CATEGORY_NAVBAR = "navigation_bar";

    private static final String NAVDIMEN = "navbar_dimen_frag";

    private Preference mNavDimen;
	
    public NavbarFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.navbar_fragment);

        final PreferenceScreen prefScreen = getPreferenceScreen();

        final PreferenceCategory navbarCategory =
                (PreferenceCategory) prefScreen.findPreference(CATEGORY_NAVBAR);

        mNavDimen = (Preference)findPreference(NAVDIMEN);

        // Enable or disable NavbarImeSwitcher based on boolean: config_show_cmIMESwitcher
        boolean showCmImeSwitcher = getResources().getBoolean(
                com.android.internal.R.bool.config_show_cmIMESwitcher);
        if (!showCmImeSwitcher) {
            Preference pref = findPreference(Settings.System.STATUS_BAR_IME_SWITCHER);
            if (pref != null) {
                navbarCategory.removePreference(pref);
            }
        }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen prefScreen, Preference pref) {
        if (pref == mNavDimen) {
            ((TinkerActivity)getActivity()).displaySubFrag(getString(R.string.navbardimenfrag_title));

            return true;
        }
        return false;
    }
}

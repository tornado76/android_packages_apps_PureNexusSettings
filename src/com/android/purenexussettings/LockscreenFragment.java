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

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.hardware.fingerprint.FingerprintManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.UserHandle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.support.design.widget.Snackbar;

import com.android.purenexussettings.Utils;
import com.android.purenexussettings.preferences.SeekBarPreference;
import com.android.purenexussettings.preferences.SystemSettingSwitchPreference;

import com.android.internal.widget.LockPatternUtils;

public class LockscreenFragment extends PreferenceFragment
            implements OnPreferenceChangeListener  {

    public LockscreenFragment(){}

    public static final int IMAGE_PICK = 1;
    private static final int MY_USER_ID = UserHandle.myUserId();

    private static final String LS_OPTIONS_CAT = "lockscreen_options";
    private static final String LS_SECURE_CAT = "lockscreen_secure_options";
    private static final String LS_WALLPAPER_CAT = "lockscreen_wallpaper";

    private static final String FINGERPRINT_VIB = "fingerprint_success_vib";
    private static final String KEY_WALLPAPER_SET = "lockscreen_wallpaper_set";
    private static final String KEY_WALLPAPER_CLEAR = "lockscreen_wallpaper_clear";
    private static final String KEYGUARD_TORCH = "keyguard_toggle_torch";
    private static final String WALLPAPER_PACKAGE_NAME = "com.slim.wallpaperpicker";
    private static final String WALLPAPER_CLASS_NAME = "com.slim.wallpaperpicker.WallpaperCropActivity";
    private static final String LSITEMS = "ls_items";
    private static final String LOCKSCREEN_MAX_NOTIF_CONFIG = "lockscreen_max_notif_cofig";
    private static final String LOCKSCREEN_ALPHA = "lockscreen_alpha";
    private static final String LOCKSCREEN_SECURITY_ALPHA = "lockscreen_security_alpha";
    private static final String PREF_LS_BOUNCER = "lockscreen_bouncer";

    private FingerprintManager mFingerprintManager;
    private Preference mSetWallpaper;
    private Preference mClearWallpaper;
    private Preference mLsItems;
    private SeekBarPreference mMaxKeyguardNotifConfig;
    private SystemSettingSwitchPreference mLsTorch;
    private SystemSettingSwitchPreference mFingerprintVib;
    private SeekBarPreference mLsAlpha;
    private SeekBarPreference mLsSecurityAlpha;
    private ListPreference mLsBouncer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean slimWallInstalled;

        addPreferencesFromResource(R.xml.lockscreen_fragment);

        PreferenceScreen prefScreen = getPreferenceScreen();
        ContentResolver resolver = getActivity().getContentResolver();
        final LockPatternUtils lockPatternUtils = new LockPatternUtils(getActivity());

        PreferenceCategory optionsCategory = (PreferenceCategory) findPreference(LS_OPTIONS_CAT);
        PreferenceCategory secureCategory = (PreferenceCategory) findPreference(LS_SECURE_CAT);
        PreferenceCategory wallpaperCategory = (PreferenceCategory) findPreference(LS_WALLPAPER_CAT);

        mSetWallpaper = (Preference) findPreference(KEY_WALLPAPER_SET);
        mClearWallpaper = (Preference) findPreference(KEY_WALLPAPER_CLEAR);
        mLsItems = (Preference) findPreference(LSITEMS);

        mMaxKeyguardNotifConfig = (SeekBarPreference) findPreference(LOCKSCREEN_MAX_NOTIF_CONFIG);
        int kgconf = Settings.System.getInt(resolver,
                Settings.System.LOCKSCREEN_MAX_NOTIF_CONFIG, 5);
        mMaxKeyguardNotifConfig.setValue(kgconf);
        mMaxKeyguardNotifConfig.setOnPreferenceChangeListener(this);

        mLsTorch = (SystemSettingSwitchPreference) findPreference(KEYGUARD_TORCH);
        if (!Utils.deviceSupportsFlashLight(getActivity())) {
            optionsCategory.removePreference(mLsTorch);
        }

        mFingerprintManager = (FingerprintManager) getActivity().getSystemService(Context.FINGERPRINT_SERVICE);
        mFingerprintVib = (SystemSettingSwitchPreference) findPreference(FINGERPRINT_VIB);
        if (!mFingerprintManager.isHardwareDetected()){
            secureCategory.removePreference(mFingerprintVib);
        }

        mLsBouncer = (ListPreference) findPreference(PREF_LS_BOUNCER);
        mLsBouncer.setOnPreferenceChangeListener(this);
        int lockbouncer = Settings.Secure.getInt(resolver,
                Settings.Secure.LOCKSCREEN_BOUNCER, 0);
        mLsBouncer.setValue(String.valueOf(lockbouncer));
        updateBouncerSummary(lockbouncer);

        mLsAlpha = (SeekBarPreference) findPreference(LOCKSCREEN_ALPHA);
        float alpha = Settings.System.getFloat(resolver,
                Settings.System.LOCKSCREEN_ALPHA, 0.45f);
        mLsAlpha.setValue((int)(100 * alpha));
        mLsAlpha.setOnPreferenceChangeListener(this);

        mLsSecurityAlpha = (SeekBarPreference) findPreference(LOCKSCREEN_SECURITY_ALPHA);
        float alpha2 = Settings.System.getFloat(resolver,
                Settings.System.LOCKSCREEN_SECURITY_ALPHA, 0.75f);
        mLsSecurityAlpha.setValue((int)(100 * alpha2));
        mLsSecurityAlpha.setOnPreferenceChangeListener(this);

        if (!lockPatternUtils.isSecure(MY_USER_ID)) {
            prefScreen.removePreference(secureCategory);
        }

        // check if wallpaper app installed
        try {
            PackageInfo pi = getActivity().getPackageManager().getPackageInfo(WALLPAPER_PACKAGE_NAME, 0);
            slimWallInstalled = pi.applicationInfo.enabled;
        } catch (PackageManager.NameNotFoundException e) {
            slimWallInstalled = false;
        }

        // if not remove wallpaper options
        if (!slimWallInstalled) {
            prefScreen.removePreference(wallpaperCategory);
        }

    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mLsItems) {
            ((TinkerActivity)getActivity()).displaySubFrag(getString(R.string.lockscreen_items_fragment_title));
            return true;
        }
        if (preference == mSetWallpaper) {
            setKeyguardWallpaper();
            return true;
        } else if (preference == mClearWallpaper) {
            clearKeyguardWallpaper();
            Snackbar.make(getActivity().findViewById(R.id.frame_container), getString(R.string.reset_lockscreen_wallpaper),
            Snackbar.LENGTH_LONG).show();
            return true;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mMaxKeyguardNotifConfig) {
            int kgconf = (Integer) newValue;
            Settings.System.putInt(resolver,
                    Settings.System.LOCKSCREEN_MAX_NOTIF_CONFIG, kgconf);
            return true;
        } else if (preference == mLsBouncer) {
            int lockbouncer = Integer.valueOf((String) newValue);
            Settings.Secure.putInt(resolver, Settings.Secure.LOCKSCREEN_BOUNCER, lockbouncer);
            updateBouncerSummary(lockbouncer);
            return true;
        } else if (preference == mLsAlpha) {
            int alpha = (Integer) newValue;
            Settings.System.putFloat(resolver,
                    Settings.System.LOCKSCREEN_ALPHA, alpha / 100.0f);
            return true;
        } else if (preference == mLsSecurityAlpha) {
            int alpha2 = (Integer) newValue;
            Settings.System.putFloat(resolver,
                    Settings.System.LOCKSCREEN_SECURITY_ALPHA, alpha2 / 100.0f);
            return true;
        }
        return false;
    }

    private void updateBouncerSummary(int value) {
        Resources res = getResources();
 
        if (value == 0) {
            // stock bouncer
            mLsBouncer.setSummary(res.getString(R.string.ls_bouncer_on_summary));
        } else if (value == 1) {
            // bypass bouncer
            mLsBouncer.setSummary(res.getString(R.string.ls_bouncer_off_summary));
        } else {
            String type = null;
            switch (value) {
                case 2:
                    type = res.getString(R.string.ls_bouncer_dismissable);
                    break;
                case 3:
                    type = res.getString(R.string.ls_bouncer_persistent);
                    break;
                case 4:
                    type = res.getString(R.string.ls_bouncer_all);
                    break;
            }
            // Remove title capitalized formatting
            type = type.toLowerCase();
            mLsBouncer.setSummary(res.getString(R.string.ls_bouncer_summary, type));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri uri = data.getData();
                Intent intent = new Intent();
                intent.setClassName(WALLPAPER_PACKAGE_NAME, WALLPAPER_CLASS_NAME);
                intent.putExtra("keyguardMode", "1");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setData(uri);
                startActivity(intent);
            }
        }
    }

    private void setKeyguardWallpaper() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK);
    }

    private void clearKeyguardWallpaper() {
        WallpaperManager wallpaperManager = null;
        wallpaperManager = WallpaperManager.getInstance(getActivity());
        wallpaperManager.clearKeyguardWallpaper();
    }
}

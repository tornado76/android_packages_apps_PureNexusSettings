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

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


public class AboutFragment extends Fragment {

    Context context;
    private AlertDialog popUpInfo;
    private int clickCount;

    private void getStartDialog() {
        MyDialogFragment myDiag = new MyDialogFragment();
        myDiag.setVals(this, true);
        myDiag.show(getFragmentManager(), "Diag1");
    }

    public void getThanksDialog() {
        MyDialogFragment myDiag = new MyDialogFragment();
        myDiag.setVals(this, false);
        myDiag.show(getFragmentManager(), "Diag2");
    }

    public AboutFragment() {
    }

    public static class MyDialogFragment extends DialogFragment
    {
        private boolean showStart;
        private Fragment fragBase;

        public MyDialogFragment() {}

        public void setVals(Fragment orig, boolean isStart) {
            showStart = isStart;
            fragBase = orig;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            View view = getActivity().getLayoutInflater().inflate(R.layout.changelog, null);
            TextView mChangeText = (TextView)view.findViewById(R.id.changetext);

            mChangeText.setText(showStart ? Html.fromHtml(getString(R.string.changelog)) : Html.fromHtml(getString(R.string.credits)));

            builder.setView(view);

            builder.setTitle(showStart ? getString(R.string.alertdiagtitle) : getString(R.string.setnegative));

            builder.setPositiveButton(getString(R.string.setpositive), null);

            if (showStart) {
                builder.setNegativeButton(getString(R.string.setnegative), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ((AboutFragment)fragBase).getThanksDialog();
                    }
                });
            }

            return  builder.create();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.context = getActivity().getApplicationContext();
        this.popUpInfo = null;
        clickCount = 0;
    }

    private void noBrowserSnack(View v) {
        Snackbar.make(v, getString(R.string.no_browser_error), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.about_frag_card, container, false);

        final LinearLayout logo = (LinearLayout)v.findViewById(R.id.logo_card);
        LinearLayout thanks = (LinearLayout)v.findViewById(R.id.credits_card);

        thanks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popUpInfo == null || !popUpInfo.isShowing()) {
                    getStartDialog();
                    logo.setClickable(true);
                }
            }
        });

        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCount++;
                if (clickCount == 5) {
                    Snackbar.make(v, getString(R.string.click1), Snackbar.LENGTH_SHORT).show();
                }
                if (clickCount > 5 && clickCount < 10) {
                    Snackbar.make(v, String.format(getString(R.string.click2), clickCount), Snackbar.LENGTH_SHORT).show();
                }
                if (clickCount == 10) {
                    Snackbar.make(v, String.format(getString(R.string.click3), clickCount), Snackbar.LENGTH_LONG).show();
                    clickCount = 0;
                }
            }
        });

        logo.setClickable(false);
        return v;
    }
}

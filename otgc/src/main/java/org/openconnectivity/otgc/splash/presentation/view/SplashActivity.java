/*
 * *****************************************************************
 *
 *  Copyright 2018 DEKRA Testing and Certification, S.A.U. All Rights Reserved.
 *
 *  ******************************************************************
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  ******************************************************************
 */

package org.openconnectivity.otgc.splash.presentation.view;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.view.ContextThemeWrapper;
import android.view.View;
import android.widget.TextView;

import org.openconnectivity.otgc.R;
import org.openconnectivity.otgc.common.presentation.viewmodel.ViewModelError;
import org.openconnectivity.otgc.devicelist.presentation.view.DeviceListActivity;
import org.openconnectivity.otgc.di.Injectable;
import org.openconnectivity.otgc.login.presentation.view.LoginActivity;
import org.openconnectivity.otgc.splash.presentation.viewmodel.SplashViewModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import javax.inject.Inject;

public class SplashActivity extends AppCompatActivity implements Injectable {

    private static final String PREFERENCE_EULA_ACCEPTED = "eula.accepted";
    private static final String PREFERENCES_EULA = "eula";
    private static final int PERMISSIONS_REQUEST_CODE = 1;

    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    private SplashViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isTaskRoot()) {
            finish();
            return;
        }

        initViewModel();

        showEulaDialog();
        //mViewModel.checkIfPermissionsAreGranted();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            mViewModel.checkIfIsAuthenticated();
        }
    }

    private void initViewModel() {
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(SplashViewModel.class);

        mViewModel.isProcessing().observe(this, this::handleProcessing);
        mViewModel.getError().observe(this, this::handleError);

        mViewModel.isAuthenticated().observe(this, this::handleAuthenticated);
        mViewModel.getMissedPermissions().observe(this, this::handleMissedPermissions);
    }

    private void handleProcessing(Boolean isProcessing) {
        // TODO: fill method
    }

    private void handleError(ViewModelError error) {
        // TODO: fill method
    }

    private void handleAuthenticated(@NonNull Boolean isAuthenticated) {
        Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(
                this,
                android.R.anim.fade_in, android.R.anim.fade_out
        ).toBundle();

//        if (isAuthenticated) {
//            startActivity(
//                    new Intent(SplashActivity.this, DeviceListActivity.class),
//                    bundle);
//        } else {
//            startActivity(
//                    new Intent(SplashActivity.this, LoginActivity.class),
//                    bundle);
//        }
        startActivity(
                    new Intent(SplashActivity.this, DeviceListActivity.class),
                    bundle);

        finish();
    }

    private void handleMissedPermissions(@NonNull List<String> missedPermissions) {
        if (missedPermissions.isEmpty()) {
            mViewModel.checkIfIsAuthenticated();
        } else {
            askForPermissions(missedPermissions);
        }
    }

    private void showEulaDialog() {
        final SharedPreferences preferences = this.getSharedPreferences(PREFERENCES_EULA, Activity.MODE_PRIVATE);

        if (!preferences.getBoolean(PREFERENCE_EULA_ACCEPTED, false)) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.eula_title);
            builder.setCancelable(false);
            builder.setPositiveButton(R.string.eula_accept_button,
                    (DialogInterface dialog, int which) -> {
                        preferences.edit().putBoolean(PREFERENCE_EULA_ACCEPTED, true).commit();
                        mViewModel.checkIfPermissionsAreGranted();
                    });
            builder.setNegativeButton(R.string.eula_dismiss_button,
                    (DialogInterface dialog, int which) -> this.finish());
            builder.setMessage(R.string.eula_body);
            builder.create().show();
        } else {
            mViewModel.checkIfPermissionsAreGranted();
        }
    }

    private void askForPermissions(@NonNull List<String> permissions) {
        View messageView = View.inflate(this, R.layout.fragment_dialog, null);
        TextView alertMessage = messageView.findViewById(R.id.text_dialog_message);
        TextView alertTitle = messageView.findViewById(R.id.text_dialog_title);
        alertTitle.setText(getString(R.string.permissions_dialog_title));
        alertMessage.setText(getString(R.string.permissions_dialog_explain_message));
        alertMessage.append("\n\n");

        AlertDialog alertDialog =
                new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AppTheme))
                        .setView(messageView)
                        .create();
        for (int i = 0; i < permissions.size(); i++) {
            if (permissions.get(i).equals(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                alertMessage.append(getText(R.string.permissions_dialog_location_explain));
            } else if (permissions.get(i).equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                alertMessage.append(getText(R.string.permissions_dialog_storage_explain));
            }

            if (i < permissions.size() - 1) {
                alertMessage.append("\n");
            }
        }

        alertDialog.setButton(Dialog.BUTTON_POSITIVE, getText(R.string.permissions_dialog_yes_message), (dialog, which) -> {
            dialog.dismiss();
            ActivityCompat.requestPermissions(this, permissions.toArray(new String[permissions.size()]), PERMISSIONS_REQUEST_CODE);
        });

        alertDialog.setButton(Dialog.BUTTON_NEGATIVE, getText(R.string.permissions_dialog_no_message), (dialog, which) -> {
            dialog.dismiss();
            openSettings();
        });
        alertDialog.setCancelable(false);
        alertDialog.show(); // This should be called before looking up for elements
    }

    private void openSettings() {
        View messageView = View.inflate(this, R.layout.fragment_dialog, null);
        TextView alertMessage = messageView.findViewById(R.id.text_dialog_message);
        TextView alertTitle = messageView.findViewById(R.id.text_dialog_title);
        alertTitle.setText(getString(R.string.permissions_denied_dialog_title));
        alertMessage.setText(getString(R.string.permissions_denied_dialog_go_to_settings_message));

        final AlertDialog alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AppTheme)).setView(messageView).create();
        alertDialog.setButton(Dialog.BUTTON_POSITIVE,
                getText(R.string.permissions_denied_dialog_yes_message),
                (dialog, which) -> {
                    dialog.dismiss();
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    startActivity(intent);
                });

        alertDialog.setButton(Dialog.BUTTON_NEGATIVE,
                getText(R.string.permissions_denied_dialog_no_message),
                (dialog, which) -> dialog.dismiss());
        alertDialog.setCancelable(false);
        alertDialog.show(); // This should be called before looking up for elements
    }
}

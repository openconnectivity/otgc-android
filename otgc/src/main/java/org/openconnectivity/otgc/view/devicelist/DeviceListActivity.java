/*
 *  *****************************************************************
 *
 *  Copyright 2018 DEKRA Testing and Certification, S.A.U. All Rights Reserved.
 *
 *  *****************************************************************
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *           http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  *****************************************************************
 */
package org.openconnectivity.otgc.view.devicelist;

import android.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.iotivity.OCRandomPinHandler;
import org.openconnectivity.otgc.utils.constant.OtgcMode;
import org.openconnectivity.otgc.utils.handler.OCSetRandomPinHandler;
import org.openconnectivity.otgc.utils.view.RecyclerWithSwipeFragment;
import org.openconnectivity.otgc.utils.viewmodel.CommonError;
import org.openconnectivity.otgc.utils.viewmodel.Response;
import org.openconnectivity.otgc.utils.viewmodel.Status;
import org.openconnectivity.otgc.utils.viewmodel.ViewModelError;
import org.openconnectivity.otgc.view.trustanchor.TrustAnchorActivity;
import org.openconnectivity.otgc.viewmodel.DeviceListViewModel;
import org.openconnectivity.otgc.viewmodel.SharedViewModel;
import org.openconnectivity.otgc.view.login.LoginActivity;
import org.openconnectivity.otgc.R;
import org.openconnectivity.otgc.view.settings.SettingsActivity;
import org.openconnectivity.otgc.view.logviewer.LogViewerActivity;
import org.openconnectivity.otgc.view.wlanscan.WlanScanActivity;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import timber.log.Timber;

public class DeviceListActivity extends AppCompatActivity implements HasSupportFragmentInjector {

    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;

    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    @BindView(R.id.progress_bar) ProgressBar mProgressBar;
    @BindView(R.id.toolbar) Toolbar mToolbar;

    private MenuItem obtMenuItem;
    private MenuItem clientMenuItem;

    private DeviceListViewModel mViewModel;

    // TODO: Refactor to avoid AlertDialog object
    private AlertDialog mConnectToWifiDialog = null;

    String verifyPin = "";

    OCSetRandomPinHandler randomPinCallbackListener = (String uuid) -> {
        Timber.d("Inside randomPinListener");
        final Object lock = new Object();
        runOnUiThread(() -> {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(DeviceListActivity.this, R.style.AppTheme));
            alertDialog.setTitle(DeviceListActivity.this.getString(R.string.devices_dialog_insert_randompin_title));
            alertDialog.setMessage(uuid + ": ");
            final EditText input = new EditText(DeviceListActivity.this);
            alertDialog.setView(input);
            alertDialog.setCancelable(false);
            alertDialog.setPositiveButton(DeviceListActivity.this.getString(R.string.devices_dialog_insert_randompin_yes_option), (dialog, which) -> {
                dialog.dismiss();
                try {
                    synchronized (lock) {
                        verifyPin = input.getText().toString();
                        lock.notifyAll();
                    }
                } catch (Exception e) {
                    Timber.e(e);
                }
            }).show();
        });
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                Timber.e(e);
            }
        }
        Timber.d("Verify after submit = %s", verifyPin);
        return verifyPin;
    };

    OCRandomPinHandler displayPinListener = pin -> {
        Timber.d("Inside displayPinListener");
        runOnUiThread(() -> {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(DeviceListActivity.this, R.style.AppTheme));
            alertDialog.setTitle(DeviceListActivity.this.getString(R.string.devices_dialog_show_randompin_title));
            alertDialog.setMessage(pin);
            alertDialog.setCancelable(false);
            alertDialog.setPositiveButton(
                    DeviceListActivity.this.getString(R.string.devices_dialog_show_randompin_yes_option),
                    (dialog, which) -> dialog.dismiss()).show();
        });
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices);

        ButterKnife.bind(this);
        initViews();
        initViewModel();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mViewModel.checkIfIsConnectedToWifi();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mViewModel.closeIotivityStack();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_devices, menu);

        for (int i=0; i<menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            String title = item.getTitle().toString().toLowerCase();
            if (title.contains(OtgcMode.CLIENT.toLowerCase())) {
                clientMenuItem = item;
            } else if (title.contains(OtgcMode.OBT.toLowerCase())) {
                obtMenuItem = item;
            }
        }

        mViewModel.initializeIotivityStack();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_reset:
                if (!obtMenuItem.isVisible()) {
                    showConfirmSetMode(OtgcMode.OBT, true);
                } else if (!clientMenuItem.isVisible()) {
                    showConfirmSetMode(OtgcMode.CLIENT, true);
                }
                break;
            case R.id.menu_item_obt_mode:
                showConfirmSetMode(OtgcMode.OBT, false);
                break;
            case R.id.menu_item_client_mode:
                showConfirmSetMode(OtgcMode.CLIENT, false);
                break;
            case R.id.menu_item_trust_anchor:
                onTrustAnchorManagement();
                break;
            case R.id.menu_item_log:
                onLogPressed();
                break;
            case R.id.menu_item_settings:
                onSettingsPressed();
                break;
            case R.id.buttonDeactivate:
                onLogoutPressed();
                break;
            default:
                break;
        }

        return true;
    }

    @Override
    public DispatchingAndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingAndroidInjector;
    }

    @OnClick(R.id.floating_button_device_scan)
    protected void onScanPressed() {
        mViewModel.retrieveDeviceId();

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.devices_fragment);
        if (fragment instanceof RecyclerWithSwipeFragment) {
            ((RecyclerWithSwipeFragment) fragment).onSwipeRefresh();
        } else if (fragment instanceof DoxsFragment) {
            ((DoxsFragment) fragment).onSwipeRefresh();
        }
    }

    private void initViews() {
        setSupportActionBar(mToolbar);
    }

    private void initViewModel() {
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(DeviceListViewModel.class);
        mViewModel.getError().observe(this, this::handleError);

        mViewModel.getInit().observe(this, success -> {
            if (success != null && success) {
                mViewModel.setRandomPinListener(randomPinCallbackListener);
                mViewModel.setDisplayPinListener(displayPinListener);
                retrieveId();
            }
        });
        mViewModel.getMode().observe(this, this::processModeResponse);
        mViewModel.getClientModeResponse().observe(this, this::processClientModeResponse);
        mViewModel.getObtModeResponse().observe(this, this::processObtModeResponse);
        mViewModel.getLogoutResponse().observe(this, this::processLogoutResponse);
        mViewModel.getConnectedResponse().observe(this, this::processConnectedResponse);
        mViewModel.getDeviceId().observe(this, mToolbar::setSubtitle);

        SharedViewModel sharedViewModel = ViewModelProviders.of(this, mViewModelFactory).get(SharedViewModel.class);
        sharedViewModel.getLoading().observe(this, this::processing);
        sharedViewModel.getDisconnected().observe(this, isDisconnected -> {
            processing(false);
            goToWlanConnectSSID();
        });
    }

    public void retrieveId() {
        mViewModel.retrieveDeviceId();
    }

    private void handleError(ViewModelError error) {
        if (error.getType().equals(CommonError.NETWORK_DISCONNECTED)) {
            processing(false);
            goToWlanConnectSSID();
        }
    }

    private void processModeResponse(String mode) {
        if (mode.equals(OtgcMode.OBT)) {
            obtMenuItem.setVisible(false);
            clientMenuItem.setVisible(true);
        } else if (mode.equals(OtgcMode.CLIENT)) {
            obtMenuItem.setVisible(true);
            clientMenuItem.setVisible(false);
        }
    }

    private void processClientModeResponse(Response<Void> response) {
        switch (response.status) {
            case LOADING:
                mProgressBar.setVisibility(View.VISIBLE);
                break;
            case SUCCESS:
                mProgressBar.setVisibility(View.GONE);
                retrieveId();
                onScanPressed();
                break;
            default:
                mProgressBar.setVisibility(View.GONE);
                Toast.makeText(this, R.string.devices_error_client_mode_failed, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void processObtModeResponse(Response<Void> response) {
        switch (response.status) {
            case LOADING:
                mProgressBar.setVisibility(View.VISIBLE);
                break;
            case SUCCESS:
                mProgressBar.setVisibility(View.GONE);
                retrieveId();
                onScanPressed();
                break;
            default:
                mProgressBar.setVisibility(View.GONE);
                Toast.makeText(this, R.string.devices_error_obt_mode_failed, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void processing(boolean isProcessing) {
        mProgressBar.setVisibility(isProcessing ? View.VISIBLE : View.GONE);
    }

    private void processLogoutResponse(Response<Void> response) {
        if (response.status.equals(Status.SUCCESS)) {
            startActivity(new Intent(DeviceListActivity.this, LoginActivity.class));
            finish();
        }
    }

    private void processConnectedResponse(Response<Boolean> response) {
        if (response.status.equals(Status.SUCCESS)
                && response.data != null && !response.data) {
            goToWlanConnectSSID();
        }
    }

    private void onSettingsPressed() {
        Intent settingsIntent = new Intent().setClass(DeviceListActivity.this, SettingsActivity.class);
        startActivity(settingsIntent);
    }

    private void onLogoutPressed() {
        Timber.d("Deactivate option selected...");
        mViewModel.logout();
    }

    private void onTrustAnchorManagement() {
        Intent trustAnchorIntent = new Intent().setClass(DeviceListActivity.this, TrustAnchorActivity.class);
        startActivity(trustAnchorIntent);
    }

    private void onLogPressed() {
        Intent intent = new Intent(this, LogViewerActivity.class);
        startActivity(intent);
    }

    private void goToWlanConnectSSID() {
        if (mConnectToWifiDialog == null) {
            mConnectToWifiDialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.devices_dialog_wifi_title)
                    .setMessage(R.string.devices_dialog_wifi_message)
                    .setPositiveButton(R.string.devices_dialog_wifi_positive_button_text, (dialog, which) -> {
                        dialog.dismiss();
                        mConnectToWifiDialog = null;
                        startActivity(new Intent(DeviceListActivity.this, WlanScanActivity.class));
                    }).setNegativeButton(R.string.devices_dialog_wifi_negative_button_text,
                            (dialog, which) -> /*dialog.dismiss()*/mConnectToWifiDialog = null
                    ).create();
            mConnectToWifiDialog.show();
        }
    }

    private void showConfirmSetMode(String mode, boolean reset) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(DeviceListActivity.this, R.style.AppTheme));
        alertDialog.setTitle(this.getString(R.string.devices_dialog_confirm_reset_device_title));

        alertDialog.setMessage(R.string.devices_dialog_confirm_reset_device_message);
        alertDialog.setPositiveButton(this.getString(R.string.devices_dialog_confirm_reset_device_yes_option), (dialog, which) -> {
            dialog.dismiss();
            if (reset) {
                if (mode.equals(OtgcMode.OBT)) {
                    mViewModel.resetObtMode();
                } else if (mode.equals(OtgcMode.CLIENT)) {
                    mViewModel.resetClientMode();
                }
            } else {
                if (mode.equals(OtgcMode.OBT)) {
                    mViewModel.setObtMode();
                } else if (mode.equals(OtgcMode.CLIENT)) {
                    mViewModel.setClientMode();
                }
            }

        });
        alertDialog.setNegativeButton(this.getString(R.string.devices_dialog_confirm_reset_device_no_option), (dialog, which) -> dialog.dismiss()).show();
    }
}

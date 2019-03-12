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
package org.openconnectivity.otgc.wlanscan.presentation.view;

import android.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
//import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.textfield.TextInputEditText;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.openconnectivity.otgc.common.presentation.UiUtils;
import org.openconnectivity.otgc.common.presentation.viewmodel.Response;
import org.openconnectivity.otgc.R;
import org.openconnectivity.otgc.common.presentation.viewmodel.ViewModelError;
import org.openconnectivity.otgc.di.Injectable;
import org.openconnectivity.otgc.wlanscan.domain.model.WifiNetwork;
import org.openconnectivity.otgc.wlanscan.presentation.viewmodel.WlanScanViewModel;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import timber.log.Timber;

public class WlanScanActivity extends AppCompatActivity implements Injectable {

    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    @BindView(R.id.swipe_refresh_wlan)
    SwipeRefreshLayout mWlanSwipeRefresh;

    private WlanScanViewModel mViewModel;
    private WlanScanAdapter mAdapter;

    private AlertDialog personalAlertDialog;

    private boolean associating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wlan);

        ButterKnife.bind(this);
        initViews();
        initViewModel();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mViewModel.enableWiFi();
        mViewModel.checkIfWifiIsConnected();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Timber.d("On Destroy");
        closeProgressDialog();

        personalAlertDialog = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.floating_button_wlan_scan)
    public void onScanPressed() {
        mViewModel.scanWifiNetworks();
    }

    private void initViews() {
        RecyclerView recyclerView = findViewById(R.id.recycler_wlan_networks);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new WlanScanAdapter(this);
        WlanScanAdapter.setOnItemClickListener((position, v) -> {
            if (!mAdapter.getItem(position).isSecured()) {
                mViewModel.connectToWifi(mAdapter.getItem(position));
            } else {
                buildWlanPasswordDialog(mAdapter.getItem(position));
            }
        });
        recyclerView.setAdapter(mAdapter);

        mWlanSwipeRefresh.setColorSchemeColors(this.getResources().getColor(R.color.ocf_dark_blue));
        mWlanSwipeRefresh.setOnRefreshListener(() -> {
            mWlanSwipeRefresh.setRefreshing(false);
            mProgressBar.setVisibility(View.VISIBLE);
            mViewModel.scanWifiNetworks();
        });

        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    private void initViewModel() {
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(WlanScanViewModel.class);
        mViewModel.isProcessing().observe(this, this::handleProcessing);
        mViewModel.getError().observe(this, this::handleError);

        mViewModel.getWifiStateResponse().observe(this, this::processWifiState);
        mViewModel.getScanResponse().observe(this, this::processScan);
        mViewModel.stateResponse().observe(this, this::processStateResponse);
        mViewModel.getWifiConnectedResponse().observe(this, this::processWifiConnectedResponse);
    }

    private void handleProcessing(@NonNull Boolean isProcessing) {
        // TODO:
    }

    private void handleError(@NonNull ViewModelError error) {
        // TODO:
    }

    private void processWifiState(Response<Integer> response) {
        switch (response.status) {
            case LOADING:
                Toast.makeText(this, R.string.wlan_scan_dialog_enable_wifi_message, Toast.LENGTH_SHORT).show();
                mProgressBar.setVisibility(View.VISIBLE);
                break;
            case SUCCESS:
                if (response.data != null) {
                    switch (response.data) {
                        case WifiManager.WIFI_STATE_DISABLING:
                            break;
                        case WifiManager.WIFI_STATE_DISABLED:
                            break;
                        case WifiManager.WIFI_STATE_ENABLING:
                            break;
                        case WifiManager.WIFI_STATE_ENABLED:
                            mProgressBar.setVisibility(View.GONE);
                            onScanPressed();
                            break;
                        case WifiManager.WIFI_STATE_UNKNOWN:
                        default:
                            break;
                    }
                }
                break;
            default:
                mProgressBar.setVisibility(View.GONE);
                Timber.e(response.message);
                break;
        }
    }

    private void processScan(Response<List<WifiNetwork>> response) {
        switch (response.status) {
            case LOADING:
                mProgressBar.setVisibility(View.VISIBLE);
                Toast.makeText(this, R.string.wlan_scan_network_scan_message, Toast.LENGTH_SHORT).show();
                break;
            case SUCCESS:
                mProgressBar.setVisibility(View.GONE);
                if (response.data != null) {
                    mAdapter.updateDataset(response.data);
                }
                break;
            default:
                mProgressBar.setVisibility(View.GONE);
                break;
        }
    }

    private void processStateResponse(Response<SupplicantState> response) {
        switch (response.status) {
            case SUCCESS:
                if (response.data != null) {
                    switch (response.data) {
                        case ASSOCIATED:
                            break;
                        case ASSOCIATING:
                            buildConnectProgressDialog();
                            associating = true;
                            break;
                        case AUTHENTICATING:
                            break;
                        case COMPLETED:
                            if (associating) {
                                onBackPressed();
                            }
                            break;
                        case DISCONNECTED:
                            break;
                        case DORMANT:
                            break;
                        case FOUR_WAY_HANDSHAKE:
                            break;
                        case GROUP_HANDSHAKE:
                            break;
                        case INACTIVE:
                            break;
                        case INTERFACE_DISABLED:
                            break;
                        case INVALID:
                            break;
                        case SCANNING:
                            break;
                        case UNINITIALIZED:
                            break;
                        default:
                            break;
                    }
                }
                break;
            default:
                break;
        }
    }

    private void processWifiConnectedResponse(Response<Boolean> response) {
        switch (response.status) {
            case LOADING:
                break;
            case SUCCESS:
                if (response.data != null && response.data) {
                    onBackPressed();
                }
                break;
            default:
                break;
        }
    }

    private void closeProgressDialog() {
        if (personalAlertDialog != null && personalAlertDialog.isShowing()) {
            personalAlertDialog.dismiss();
        }
    }

    private void buildWlanPasswordDialog(WifiNetwork scanResult) {
        View passwordView = View.inflate(this, R.layout.dialog_wlan_input_password, null);
        TextInputEditText etPassword = passwordView.findViewById(R.id.edt_inflater_wlan_connect_bssid_password);
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(passwordView)
                .setTitle(scanResult.getName())
                .setPositiveButton(R.string.wlan_scan_dialog_password_positive_button_text,
                        (dialog, which) -> {
                            mViewModel.connectToWifi(scanResult, etPassword.getText().toString());
                            dialog.dismiss();
                }).setNegativeButton(R.string.wlan_scan_dialog_password_negative_button_text,
                        (dialog, which) -> dialog.dismiss()
                ).create();
        alertDialog.show();
    }

    private void buildConnectProgressDialog() {
        personalAlertDialog = UiUtils.createProgressDialog(this,
                getString(R.string.wlan_scan_dialog_connect_to_wifi_message));
    }
}

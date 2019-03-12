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

package org.openconnectivity.otgc.devicelist.presentation.view;

import android.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import androidx.annotation.NonNull;
import com.google.android.material.textfield.TextInputEditText;

import androidx.recyclerview.selection.OnDragInitiatedListener;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.iotivity.base.OxmType;
import org.openconnectivity.otgc.R;
import org.openconnectivity.otgc.accesscontrol.presentation.view.AccessControlActivity;
import org.openconnectivity.otgc.client.presentation.view.GenericClientActivity;
import org.openconnectivity.otgc.common.presentation.view.EmptyRecyclerView;
import org.openconnectivity.otgc.common.presentation.viewmodel.CommonError;
import org.openconnectivity.otgc.common.presentation.viewmodel.ViewModelError;
import org.openconnectivity.otgc.credential.presentation.view.CredentialsActivity;
import org.openconnectivity.otgc.devicelist.domain.model.Device;
import org.openconnectivity.otgc.common.presentation.UiUtils;
import org.openconnectivity.otgc.common.presentation.viewmodel.Response;
import org.openconnectivity.otgc.devicelist.presentation.viewmodel.SharedViewModel;
import org.openconnectivity.otgc.devicelist.presentation.viewmodel.DoxsViewModel;
import org.openconnectivity.otgc.di.Injectable;
import org.openconnectivity.otgc.linkedroles.presentation.view.LinkedRolesActivity;
import org.openconnectivity.otgc.wlanscan.domain.model.WifiNetwork;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class DoxsFragment extends Fragment implements DoxsViewModel.SelectOxMListener, Injectable {

    @BindView(R.id.swipe_refresh_devices) SwipeRefreshLayout mSwipeToRefreshView;
    @BindView(R.id.recycler_ocf_devices) EmptyRecyclerView mRecyclerView;

    @Inject ViewModelProvider.Factory mViewModelFactory;

    private DoxsListAdapter mAdapter;

    private DoxsViewModel mViewModel;
    private SharedViewModel mSharedViewModel;

    private AlertDialog adPersonal;

    private OxmType selectedOxm;

    private int positionBeingUpdated = 0;
    private int positionWifiEasySetupItem = 0;

    private AlertDialog mConnectWifiDialog = null;

    private SelectionTracker mSelectionTracker;
    private ActionMode mActionMode;

    public DoxsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recycler_with_swipe, container, false);
        ButterKnife.bind(this, rootView);

        initViews();

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViewModel();
    }

    private void initViews() {
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(getAdapter());
        mRecyclerView.setEmptyView(null);

        if (getContext() != null) {
            mSwipeToRefreshView.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        }

        mSwipeToRefreshView.setOnRefreshListener(() -> {
            mSwipeToRefreshView.setRefreshing(false);
            onSwipeRefresh();
        });

        mSelectionTracker = new SelectionTracker.Builder<>(
                "my-selection-id",
                mRecyclerView,
                new MyItemKeyProvider(1, mAdapter.mDataset),
                new MyItemLookup(mRecyclerView),
                StorageStrategy.createLongStorage()
        ).withOnDragInitiatedListener(e -> {
            Timber.d("onDragInitiated");
            return true;
        }).build();

        mAdapter.setSelectionTracker(mSelectionTracker);

        mSelectionTracker.addObserver(new SelectionTracker.SelectionObserver() {
            @Override
            public void onSelectionChanged() {
                super.onSelectionChanged();
                if (mSelectionTracker.hasSelection()) {
                    if (mActionMode == null) {
                        ActionModeController actionController = new ActionModeController(getActivity(), mSelectionTracker);
                        ActionModeController.setOnMenuItemClickListener((menuItem, serverId, clientId) -> {
                            if (menuItem.getItemId() == R.id.action_pairwise) {
                                mViewModel.pairwiseDevices(serverId, clientId);
                            } else if (menuItem.getItemId() == R.id.action_unlink) {
                                mViewModel.unlinkDevices(serverId, clientId);
                            }

                            return true;
                        });
                        mActionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(actionController);
                    } else {
                        mActionMode.invalidate();
                    }
                    mActionMode.setTitle(Integer.toString(mSelectionTracker.getSelection().size()));
                } else if (mActionMode != null) {
                    mActionMode.finish();
                    mActionMode = null;
                }
            }
        });
    }

    private RecyclerView.Adapter getAdapter() {
        mAdapter = new DoxsListAdapter(getContext());
        DoxsListAdapter.setOnItemClickListener((position, v) -> {
            /*switch (mAdapter.mDataset.get(position).getType()) {
                case UNOWNED:
                    positionBeingUpdated = position;
                    mViewModel.doOwnershipTransfer(mAdapter.mDataset.get(position).getOcSecureResource());
                    break;
                case OWNED_BY_SELF:
                case OWNED_BY_OTHER:
                    launchGenericClientView(
                            mAdapter.mDataset.get(position).getOcSecureResource().getIpAddr(),
                            mAdapter.mDataset.get(position).getDeviceId());
                    break;
                default:
                    break;
            }*/
            switch (v.getId()) {
                case R.id.img_btn_add_device:
                    positionBeingUpdated = position;
                    mViewModel.doOwnershipTransfer(mAdapter.mDataset.get(position).getOcSecureResource());
                    break;
                case R.id.img_btn_generic_client:
                    launchGenericClientView(mAdapter.mDataset.get(position).getDeviceId());
                    break;
            }
        });
        DoxsListAdapter.setOnMenuItemClickListener((position, item) -> {
            switch (item.getItemId()) {
                case R.id.menu_item_set_device_name:
                    showSetDeviceNameDialog(
                            position,
                            mAdapter.mDataset.get(position).getDeviceId(),
                            mAdapter.mDataset.get(position).getDeviceInfo().getName()
                    );
                    break;
                case R.id.button_access_control:
                    Intent intent = new Intent(getActivity(), AccessControlActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("deviceId", mAdapter.mDataset.get(position).getDeviceId());
                    startActivity(intent);
                    break;
                case R.id.button_credentials:
                    Intent credIntent = new Intent(getActivity(), CredentialsActivity.class);
                    credIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    credIntent.putExtra("deviceId", mAdapter.mDataset.get(position).getDeviceId());
                    startActivity(credIntent);
                    break;
                case R.id.button_roles:
                    Intent rolesIntent = new Intent(getActivity(), LinkedRolesActivity.class);
                    rolesIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    rolesIntent.putExtra("deviceId", mAdapter.mDataset.get(position).getDeviceId());
                    rolesIntent.putExtra("deviceRole", mAdapter.mDataset.get(position).getRole().toString());
                    startActivity(rolesIntent);
                    break;
                case R.id.menu_item_wifi_easy_setup:
                    mViewModel.getScanResponse().observe(this, this::processScan);
                    positionWifiEasySetupItem = position;
                    mViewModel.scanWifiNetworks();
                    break;
                case R.id.button_offboard:
                    positionBeingUpdated = position;
                    mViewModel.offboard(mAdapter.mDataset.get(position).getOcSecureResource());
                    break;
                default:
                    break;
            }
            return false;
        });
        return mAdapter;
    }

    private void initViewModel() {
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(DoxsViewModel.class);

        mViewModel.setOxmListener(this);

        mViewModel.isProcessing().observe(this, this::handleProcessing);
        mViewModel.getError().observe(this, this::handleError);

        mViewModel.getDeviceFound().observe(this, this::processDeviceFound);
        mViewModel.getOtmResponse().observe(this, this::processOtmResponse);
        mViewModel.getOffboardResponse().observe(this, this::processOffboardResponse);

        mViewModel.getConnectWifiEasySetupResponse().observe(this, this::processConnectWifiEasySetupResponse);

        if (getActivity() != null) {
            mSharedViewModel = ViewModelProviders.of(getActivity(), mViewModelFactory).get(SharedViewModel.class);
        }
    }

    public void onSwipeRefresh() {
        mAdapter.clearItems();
        mViewModel.onScanRequested();
    }

    @Override
    public OxmType onGetOxM(List<OxmType> supportedOxm) {
        selectedOxm = null;
        List<CharSequence> options = new ArrayList<>();
        if (supportedOxm.contains(OxmType.OIC_JUST_WORKS)) {
            options.add(getString(R.string.devices_oxm_just_works));
        }
        if (supportedOxm.contains(OxmType.OIC_RANDOM_DEVICE_PIN)) {
            options.add(getString(R.string.devices_oxm_random_pin));
        }
        if (supportedOxm.contains(OxmType.OIC_MANUFACTURER_CERTIFICATE)) {
            options.add(getString(R.string.devices_oxm_man_cert));
        }

        final Object lock = new Object();
        getActivity().runOnUiThread(() -> {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
            alertDialog.setTitle(R.string.devices_select_oxm_title);
            alertDialog.setItems(options.toArray(new CharSequence[0]), (dialog, which) -> {
                dialog.dismiss();
                try {
                    synchronized (lock) {
                        selectedOxm = supportedOxm.get(which);
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

        return selectedOxm;
    }

    private void handleProcessing(Boolean isProcessing) {
        mSharedViewModel.setLoading(isProcessing);
    }

    private void handleError(ViewModelError error) {
        if (error.getType() instanceof CommonError
                && error.getType().equals(CommonError.NETWORK_DISCONNECTED)) {
            mSharedViewModel.setDisconnected(true);
        } else if (error.getType() instanceof DoxsViewModel.Error) {
            switch ((DoxsViewModel.Error) error.getType()) {
                case SCAN_DEVICES:
                    Toast.makeText(getActivity(), R.string.devices_error_scanning_devices, Toast.LENGTH_SHORT).show();
                    break;
                case PAIRWISE_DEVICES:
                    Toast.makeText(getActivity(), R.string.devices_error_pairing_devices, Toast.LENGTH_SHORT).show();
                default:
                    break;
            }
        }
    }

    private void processDeviceFound(@NonNull Device deviceFound) {
        mAdapter.addItem(deviceFound);
    }

    private void processOtmResponse(Response<Device> response) {
        switch (response.status) {
            case LOADING:
                renderOtmLoadingState();
                break;
            case SUCCESS:
                renderOtmDataState(response.data);
                break;
            case ERROR:
                renderOtmErrorState(response.message);
                break;
        }
    }

    private void renderOtmLoadingState() {
        adPersonal = UiUtils.createProgressDialog(getActivity(),
                getString(R.string.devices_dialog_onboarding_otm_message));
    }

    private void renderOtmDataState(Device data) {
        if (adPersonal != null && adPersonal.isShowing()) {
            adPersonal.dismiss();
        }

        if (data != null) {
            positionBeingUpdated = mAdapter.updateItem(positionBeingUpdated, data);
            showSetDeviceNameDialog(
                    positionBeingUpdated,
                    mAdapter.mDataset.get(positionBeingUpdated).getDeviceId(),
                    mAdapter.mDataset.get(positionBeingUpdated).getDeviceInfo().getName()
            );
            positionBeingUpdated = 0;
        }
    }

    private void renderOtmErrorState(String message) {
        if (adPersonal != null && adPersonal.isShowing()) {
            adPersonal.dismiss();
        }
        Toast.makeText(getActivity(), R.string.devices_error_transferring_ownership, Toast.LENGTH_SHORT).show();
    }

    private void processOffboardResponse(Response<Device> response) {
        switch (response.status) {
            case LOADING:
                adPersonal = UiUtils.createProgressDialog(getActivity(),
                        getString(R.string.devices_dialog_offboarding_reset_message));
                break;
            case SUCCESS:
                if (adPersonal != null && adPersonal.isShowing()) {
                    adPersonal.dismiss();
                }
                if (response.data != null) {
                    mAdapter.updateItem(positionBeingUpdated, response.data);
                    positionBeingUpdated = 0;
                }
                break;
            default:
                if (adPersonal != null && adPersonal.isShowing()) {
                    adPersonal.dismiss();
                }
                Toast.makeText(getActivity(), R.string.devices_error_offboard_failed, Toast.LENGTH_SHORT)
                        .show();
                break;
        }
    }

    private void processScan(Response<List<WifiNetwork>> response) {
        switch (response.status) {
            case LOADING:
                adPersonal = UiUtils.createProgressDialog(getActivity(), getString(R.string.wlan_scan_network_scan_message));
                break;
            case SUCCESS:
                if (adPersonal != null && adPersonal.isShowing()) {
                    adPersonal.dismiss();
                }
                if (response.data != null) {
                    buildConnectWifiDialog(response.data);
                    mViewModel.getScanResponse().removeObservers(this);
                }
                break;
            default:
                if (adPersonal != null && adPersonal.isShowing()) {
                    adPersonal.dismiss();
                }
                Toast.makeText(getActivity(), getString(R.string.devices_dialog_wifi_scan_error), Toast.LENGTH_SHORT)
                        .show();
                break;
        }
    }

    private void buildConnectWifiDialog(List<WifiNetwork> wifiNetworks) {
        if (mConnectWifiDialog == null) {
            ArrayList<WifiNetwork> wifiArrayList = new ArrayList<>();
            WifiAdapter adapter = new WifiAdapter(getContext(), wifiArrayList);
            View convertView = View.inflate(getContext(), R.layout.dialog_wlan_scan, null);
            Spinner spinnerView = (Spinner) convertView.findViewById(R.id.dialog_spinner_wifi);
            EditText etPwd = (EditText) convertView.findViewById(R.id.edit_text_password);
            spinnerView.setAdapter(adapter);
            for (WifiNetwork wifiNetwork : wifiNetworks) {
                adapter.add(wifiNetwork);
            }

            mConnectWifiDialog = new AlertDialog.Builder(getContext())
                    .setTitle(R.string.devices_dialog_wifi_title)
                    .setView(convertView)
                    .setPositiveButton(R.string.devices_dialog_wifi_positive_button_text, (dialog, which) -> {
                        WifiNetwork selectedWifi = (WifiNetwork) spinnerView.getSelectedItem();
                        String pwd = etPwd.getText().toString();
                        mViewModel.connectWifiEasySetup(mAdapter.mDataset.get(positionWifiEasySetupItem).getDeviceId(),
                                selectedWifi.getName(), pwd,
                                selectedWifi.getAuthenticationType(),
                                selectedWifi.getEncryptionType());
                        mConnectWifiDialog = null;
                        adapter.clear();
                    }).setNegativeButton(R.string.devices_dialog_wifi_negative_button_text,
                            (dialog, which) -> {
                                /*dialog.dismiss()*/
                                mConnectWifiDialog = null;
                                adapter.clear();
                            }
                    ).create();
            mConnectWifiDialog.show();
        } else {
            mConnectWifiDialog.show();
        }
    }

    private void processConnectWifiEasySetupResponse(Response<Void> response) {
        switch (response.status) {
            case LOADING:
                adPersonal = UiUtils.createProgressDialog(getActivity(), getString(R.string.devices_dialog_wifi_easy_setup_connecting));
                break;
            case SUCCESS:
                if (adPersonal != null && adPersonal.isShowing()) {
                    adPersonal.dismiss();
                }
                Toast.makeText(getContext(), getString(R.string.devices_dialog_wifi_easy_setup_connect), Toast.LENGTH_SHORT).show();
                break;
            default:
                if (adPersonal != null && adPersonal.isShowing()) {
                    adPersonal.dismiss();
                }
                Toast.makeText(getContext(), getString(R.string.devices_dialog_wifi_easy_setup_error), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void launchGenericClientView(String deviceId) {
        Intent intent = new Intent(getActivity(), GenericClientActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("DeviceId", deviceId);
        startActivity(intent);
    }

    private void showSetDeviceNameDialog(int position, String deviceId, String currentDeviceName) {
        float dpi = getContext().getResources().getDisplayMetrics().density;
        final TextInputEditText input = new TextInputEditText(getActivity());
        input.setText(currentDeviceName);
        input.setSelectAllOnFocus(true);
        input.requestFocus();
        AlertDialog dialog = (new AlertDialog.Builder(getActivity()))
                .setTitle(getString(R.string.devices_dialog_set_device_name_title))
                .setPositiveButton(getString(R.string.devices_dialog_set_device_name_yes_option), (dialogInterface, which) -> {
                    mViewModel.setDeviceName(deviceId, input.getText().toString());
                    Device d = mAdapter.mDataset.get(position);
                    d.getDeviceInfo().setName(input.getText().toString());
                    mAdapter.mDataset.updateItemAt(position, d);
                })
                .setNegativeButton(
                        getString(R.string.devices_dialog_set_device_name_no_option),
                        null)
                .create();
        dialog.setView(input, (int)(16*dpi), (int)(16*dpi), (int)(16*dpi), (int)(16*dpi));
        dialog.show();
    }
}

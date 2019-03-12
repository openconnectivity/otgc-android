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

package org.openconnectivity.otgc.devicelist.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.iotivity.base.OcSecureResource;
import org.iotivity.base.OxmType;
import org.openconnectivity.otgc.common.domain.usecase.CheckConnectionUseCase;
import org.openconnectivity.otgc.common.domain.usecase.GetDeviceInfoUseCase;
import org.openconnectivity.otgc.common.domain.model.NetworkDisconnectedException;
import org.openconnectivity.otgc.common.presentation.viewmodel.BaseViewModel;
import org.openconnectivity.otgc.common.presentation.viewmodel.CommonError;
import org.openconnectivity.otgc.common.presentation.viewmodel.ViewModelError;
import org.openconnectivity.otgc.common.presentation.viewmodel.ViewModelErrorType;
import org.openconnectivity.otgc.devicelist.domain.model.DeviceType;
import org.openconnectivity.otgc.common.domain.usecase.GetDeviceNameUseCase;
import org.openconnectivity.otgc.devicelist.domain.usecase.GetDeviceRoleUseCase;
import org.openconnectivity.otgc.devicelist.domain.usecase.GetOTMethodsUseCase;
import org.openconnectivity.otgc.devicelist.domain.usecase.OnboardUseCase;
import org.openconnectivity.otgc.devicelist.domain.model.Device;
import org.openconnectivity.otgc.common.presentation.viewmodel.Response;
import org.openconnectivity.otgc.devicelist.domain.usecase.OffboardUseCase;
import org.openconnectivity.otgc.devicelist.domain.usecase.PairwiseDevicesUseCase;
import org.openconnectivity.otgc.devicelist.domain.usecase.ScanDevicesUseCase;
import org.openconnectivity.otgc.devicelist.domain.usecase.SetDeviceNameUseCase;
import org.openconnectivity.otgc.devicelist.domain.usecase.SetOTMethodUseCase;
import org.openconnectivity.otgc.common.domain.rx.SchedulersFacade;
import org.openconnectivity.otgc.devicelist.domain.usecase.UnlinkDevicesUseCase;
import org.openconnectivity.otgc.devicelist.domain.usecase.WiFiEasySetupUseCase;
import org.openconnectivity.otgc.wlanscan.domain.model.WifiNetwork;
import org.openconnectivity.otgc.wlanscan.domain.usecase.RegisterScanResultsReceiverUseCase;
import org.openconnectivity.otgc.wlanscan.domain.usecase.ScanWiFiNetworksUseCase;

import java.util.List;

import javax.inject.Inject;

public class DoxsViewModel extends BaseViewModel {

    private final CheckConnectionUseCase mCheckConnectionUseCase;
    private final ScanDevicesUseCase mScanDevicesUseCase;
    private final GetOTMethodsUseCase mGetOTMethodsUseCase;
    private final SetOTMethodUseCase mSetOTMethodUseCase;
    private final OnboardUseCase mOnboardUseCase;
    private final OffboardUseCase mOffboardUseCase;
    private final GetDeviceInfoUseCase mGetDeviceInfoUseCase;
    private final SetDeviceNameUseCase mSetDeviceNameUseCase;
    private final GetDeviceNameUseCase mGetDeviceNameUseCase;
    private final ScanWiFiNetworksUseCase mScanWiFiNetworksUseCase;
    private final WiFiEasySetupUseCase mWiFiEasySetupUseCase;
    private final GetDeviceRoleUseCase mGetDeviceRoleUseCase;
    private final PairwiseDevicesUseCase mPairwiseDevicesUseCase;
    private final UnlinkDevicesUseCase mUnlinkDevicesUseCase;

    private final SchedulersFacade mSchedulersFacade;

    private final MutableLiveData<Device> mDeviceFound = new MutableLiveData<>();

    private final MutableLiveData<Response<Device>> otmResponse = new MutableLiveData<>();
    private final MutableLiveData<Response<Device>> offboardResponse = new MutableLiveData<>();
    private final MutableLiveData<Response<List<WifiNetwork>>> scanResponse = new MutableLiveData<>();
    private final MutableLiveData<Response<Void>> connectWifiEasySetupResponse = new MutableLiveData<>();

    private SelectOxMListener mOxmListener;

    @Inject
    DoxsViewModel(RegisterScanResultsReceiverUseCase registerScanResultsReceiverUseCase,
            CheckConnectionUseCase checkConnectionUseCase,
            ScanDevicesUseCase scanDevicesUseCase,
            GetOTMethodsUseCase getOTMethodsUseCase,
            SetOTMethodUseCase setOTMethodUseCase,
            OnboardUseCase onboardUseCase,
            OffboardUseCase offboardUseCase,
            GetDeviceInfoUseCase getDeviceInfoUseCase,
            SetDeviceNameUseCase setDeviceNameUseCase,
            GetDeviceNameUseCase getDeviceNameUseCase,
            ScanWiFiNetworksUseCase scanWiFiNetworksUseCase,
            WiFiEasySetupUseCase wiFiEasySetupUseCase,
            GetDeviceRoleUseCase getDeviceRoleUseCase,
            PairwiseDevicesUseCase pairwiseDevicesUseCase,
            UnlinkDevicesUseCase unlinkDevicesUseCase,
            SchedulersFacade schedulersFacade) {
        this.mCheckConnectionUseCase = checkConnectionUseCase;
        this.mScanDevicesUseCase = scanDevicesUseCase;
        this.mGetOTMethodsUseCase = getOTMethodsUseCase;
        this.mSetOTMethodUseCase = setOTMethodUseCase;
        this.mOnboardUseCase = onboardUseCase;
        this.mOffboardUseCase = offboardUseCase;
        this.mGetDeviceInfoUseCase = getDeviceInfoUseCase;
        this.mSetDeviceNameUseCase = setDeviceNameUseCase;
        this.mGetDeviceNameUseCase = getDeviceNameUseCase;
        this.mScanWiFiNetworksUseCase = scanWiFiNetworksUseCase;
        this.mWiFiEasySetupUseCase = wiFiEasySetupUseCase;
        this.mGetDeviceRoleUseCase = getDeviceRoleUseCase;
        this.mPairwiseDevicesUseCase = pairwiseDevicesUseCase;
        this.mUnlinkDevicesUseCase = unlinkDevicesUseCase;

        this.mSchedulersFacade = schedulersFacade;

        mDisposables.add(registerScanResultsReceiverUseCase.execute()
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .subscribe(
                        scanResults -> scanResponse.setValue(Response.success(scanResults)),
                        throwable -> scanResponse.setValue(Response.error(throwable))
                ));
    }

    public LiveData<Device> getDeviceFound() {
        return mDeviceFound;
    }

    public void setOxmListener(SelectOxMListener listener) {
        mOxmListener = listener;
    }

    public LiveData<Response<Device>> getOtmResponse() {
        return otmResponse;
    }

    public LiveData<Response<Device>> getOffboardResponse() {
        return offboardResponse;
    }

    public MutableLiveData<Response<List<WifiNetwork>>> getScanResponse() {
        return scanResponse;
    }

    public LiveData<Response<Void>> getConnectWifiEasySetupResponse() {
        return connectWifiEasySetupResponse;
    }

    public void onScanRequested() {
        mDisposables.add(mCheckConnectionUseCase.executeCompletable()
                .andThen(mScanDevicesUseCase.execute()
                    .map(device -> {
                        device.setDeviceInfo(mGetDeviceInfoUseCase.execute(device.getDeviceId()).blockingGet());
                        return device;
                    }).map(device -> {
                        device.setRole(
                                mGetDeviceRoleUseCase.execute(device.getDeviceId()).blockingGet());

                        return device;
                    }).map(device -> {
                        if (device.getType().equals(DeviceType.OWNED_BY_SELF)) {
                            String storedDeviceName = mGetDeviceNameUseCase.execute(device.getDeviceId()).blockingGet();
                            if (storedDeviceName != null && !storedDeviceName.isEmpty()) {
                                device.getDeviceInfo().setName(storedDeviceName);
                            }
                        }

                        return device;
                    })
                )
                .subscribeOn(mSchedulersFacade.io())
                .observeOn(mSchedulersFacade.ui())
                .doOnSubscribe(__ -> mProcessing.setValue(true))
                .doFinally(() -> mProcessing.setValue(false))
                .subscribe(
                        mDeviceFound::setValue,
                        throwable -> {
                            if (throwable instanceof NetworkDisconnectedException) {
                                mError.setValue(new ViewModelError(CommonError.NETWORK_DISCONNECTED, null));
                            } else {
                                mError.setValue(new ViewModelError(Error.SCAN_DEVICES, null));
                            }
                        }
                ));
    }

    public void doOwnershipTransfer(OcSecureResource ocSecureResource) {
        mDisposables.add(mCheckConnectionUseCase.executeCompletable()
                .andThen(mGetOTMethodsUseCase.execute(ocSecureResource)
                        .map(oxms -> {
                            if (oxms.size() > 1) {
                                return mOxmListener.onGetOxM(oxms);
                            } else {
                                return oxms.get(0);
                            }
                        }).filter(oxm -> oxm != null)
                        .flatMapCompletable(oxm -> mSetOTMethodUseCase.execute(ocSecureResource, oxm)))
                //.flatMap(updatedOcSecureResource -> mOnboardUseCase.execute(updatedOcSecureResource)
                .andThen(mOnboardUseCase.execute(ocSecureResource)
                        .map(device -> {
                            device.setDeviceInfo(mGetDeviceInfoUseCase.execute(device.getDeviceId()).blockingGet());
                            return device;
                        }))
                        .map(device -> {
                            device.setRole(
                                    mGetDeviceRoleUseCase.execute(device.getDeviceId()).blockingGet());

                            return device;
                        })
                .subscribeOn(mSchedulersFacade.io())
                .observeOn(mSchedulersFacade.ui())
                .doOnSubscribe(__ -> otmResponse.setValue(Response.loading()))
                .subscribe(
                        ownedDevice -> otmResponse.setValue(Response.success(ownedDevice)),
                        throwable -> {
                            if (throwable instanceof NetworkDisconnectedException) {
                                mError.setValue(new ViewModelError(CommonError.NETWORK_DISCONNECTED, null));
                            } else {
                                otmResponse.setValue(Response.error(throwable));
                            }
                        }
                ));
    }

    public void offboard(OcSecureResource deviceToOffboard) {
        mDisposables.add(mCheckConnectionUseCase.executeCompletable()
                .andThen(mOffboardUseCase.execute(deviceToOffboard)
                        .map(device -> {
                            device.setDeviceInfo(mGetDeviceInfoUseCase.execute(device.getDeviceId()).blockingGet());
                            return device;
                        }))
                .subscribeOn(mSchedulersFacade.io())
                .observeOn(mSchedulersFacade.ui())
                .doOnSubscribe(__ -> offboardResponse.setValue(Response.loading()))
                .subscribe(
                        unownedDevice -> offboardResponse.setValue(Response.success(unownedDevice)),
                        throwable -> {
                            if (throwable instanceof NetworkDisconnectedException) {
                                mError.setValue(new ViewModelError(CommonError.NETWORK_DISCONNECTED, null));
                            } else {
                                otmResponse.setValue(Response.error(throwable));
                            }
                        }
                ));
    }

    public void setDeviceName(String deviceId, String deviceName) {
        mDisposables.add(mSetDeviceNameUseCase.execute(deviceId, deviceName)
                .andThen(mGetDeviceNameUseCase.execute(deviceId))
                .subscribeOn(mSchedulersFacade.io())
                .observeOn(mSchedulersFacade.ui())
                .subscribe(
                        name -> {},
                        throwable -> {}
                ));
    }

    public void scanWifiNetworks() {
        mDisposables.add(mScanWiFiNetworksUseCase.execute()
                .subscribeOn(mSchedulersFacade.io())
                .observeOn(mSchedulersFacade.ui())
                .doOnSubscribe(__ -> scanResponse.setValue(Response.loading()))
                .subscribe(
                        () -> {},
                        throwable -> {}
                ));
    }

    public void connectWifiEasySetup(String deviceId, String ssid, String pwd,
                                     int authenticationType, int encodingType) {
        mDisposables.add(mWiFiEasySetupUseCase.execute(deviceId, ssid, pwd, authenticationType, encodingType)
                .subscribeOn(mSchedulersFacade.io())
                .observeOn(mSchedulersFacade.ui())
                .doOnSubscribe(__ -> connectWifiEasySetupResponse.setValue(Response.loading()))
                .subscribe(
                        () -> connectWifiEasySetupResponse.setValue(Response.success(null)),
                        throwable -> connectWifiEasySetupResponse.setValue(Response.error(throwable))
                )
        );
    }

    public void pairwiseDevices(String serverId, String clientId) {
        mDisposables.add(mPairwiseDevicesUseCase.execute(serverId, clientId)
                .subscribeOn(mSchedulersFacade.io())
                .observeOn(mSchedulersFacade.ui())
                .doOnSubscribe(__ -> mProcessing.setValue(true))
                .doFinally(() -> mProcessing.setValue(false))
                .subscribe(
                        () -> {},
                        throwable -> mError.setValue(new ViewModelError(Error.PAIRWISE_DEVICES, null))
                ));
    }

    public void unlinkDevices(String serverId, String clientId) {
        mDisposables.add(mUnlinkDevicesUseCase.execute(serverId, clientId)
                .subscribeOn(mSchedulersFacade.io())
                .observeOn(mSchedulersFacade.ui())
                .doOnSubscribe(__ -> mProcessing.setValue(true))
                .doFinally(() -> mProcessing.setValue(false))
                .subscribe(
                        () -> {},
                        throwable -> mError.setValue(new ViewModelError(Error.UNLINK_DEVICES, null))
                ));
    }

    public interface SelectOxMListener {
        OxmType onGetOxM(List<OxmType> supportedOxm);
    }

    public enum Error implements ViewModelErrorType {
        SCAN_DEVICES,
        PAIRWISE_DEVICES,
        UNLINK_DEVICES
    }
}

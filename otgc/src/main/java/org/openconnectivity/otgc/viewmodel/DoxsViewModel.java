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

package org.openconnectivity.otgc.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.openconnectivity.otgc.domain.usecase.GetDeviceDatabaseUseCase;
import org.openconnectivity.otgc.domain.usecase.wifi.CheckConnectionUseCase;
import org.openconnectivity.otgc.domain.usecase.GetDeviceInfoUseCase;
import org.openconnectivity.otgc.domain.model.exception.NetworkDisconnectedException;
import org.openconnectivity.otgc.utils.constant.OcfOxmType;
import org.openconnectivity.otgc.utils.viewmodel.BaseViewModel;
import org.openconnectivity.otgc.utils.viewmodel.CommonError;
import org.openconnectivity.otgc.utils.viewmodel.ViewModelError;
import org.openconnectivity.otgc.utils.viewmodel.ViewModelErrorType;
import org.openconnectivity.otgc.domain.model.devicelist.DeviceType;
import org.openconnectivity.otgc.domain.usecase.GetDeviceNameUseCase;
import org.openconnectivity.otgc.domain.usecase.GetDeviceRoleUseCase;
import org.openconnectivity.otgc.domain.usecase.GetOTMethodsUseCase;
import org.openconnectivity.otgc.domain.usecase.OnboardUseCase;
import org.openconnectivity.otgc.domain.model.devicelist.Device;
import org.openconnectivity.otgc.utils.viewmodel.Response;
import org.openconnectivity.otgc.domain.usecase.OffboardUseCase;
import org.openconnectivity.otgc.domain.usecase.link.PairwiseDevicesUseCase;
import org.openconnectivity.otgc.domain.usecase.ScanDevicesUseCase;
import org.openconnectivity.otgc.domain.usecase.SetDeviceNameUseCase;
import org.openconnectivity.otgc.utils.rx.SchedulersFacade;
import org.openconnectivity.otgc.domain.usecase.link.UnlinkDevicesUseCase;
import org.openconnectivity.otgc.domain.usecase.easysetup.WiFiEasySetupUseCase;
import org.openconnectivity.otgc.domain.model.WifiNetwork;
import org.openconnectivity.otgc.domain.usecase.wifi.RegisterScanResultsReceiverUseCase;
import org.openconnectivity.otgc.domain.usecase.wifi.ScanWiFiNetworksUseCase;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

public class DoxsViewModel extends BaseViewModel {

    private final CheckConnectionUseCase mCheckConnectionUseCase;
    private final ScanDevicesUseCase mScanDevicesUseCase;
    private final GetOTMethodsUseCase mGetOTMethodsUseCase;
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
    private final GetDeviceDatabaseUseCase mGetDeviceDatabaseUseCase;

    private final SchedulersFacade mSchedulersFacade;

    private final MutableLiveData<Device> mDeviceFound = new MutableLiveData<>();
    private final MutableLiveData<Device> mUpdatedDevice = new MutableLiveData<>();

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
            GetDeviceDatabaseUseCase getDeviceDatabaseUseCase,
            SchedulersFacade schedulersFacade) {
        this.mCheckConnectionUseCase = checkConnectionUseCase;
        this.mScanDevicesUseCase = scanDevicesUseCase;
        this.mGetOTMethodsUseCase = getOTMethodsUseCase;
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
        this.mGetDeviceDatabaseUseCase = getDeviceDatabaseUseCase;

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

    public LiveData<Device> getUpdatedDevice() {
        return mUpdatedDevice;
    }

    public LiveData<Response<Void>> getConnectWifiEasySetupResponse() {
        return connectWifiEasySetupResponse;
    }

    public void onScanRequested() {
        mDisposables.add(mCheckConnectionUseCase.executeCompletable()
                .andThen(mScanDevicesUseCase.execute()
                    .delay(1, TimeUnit.SECONDS)
                    .map(device -> {
                        device.setDeviceInfo(mGetDeviceInfoUseCase.execute(device).blockingGet());
                        return device;
                    })
                    .delay(1, TimeUnit.SECONDS)
                    .map(device -> {
                        device.setDeviceRole(
                                mGetDeviceRoleUseCase.execute(device).blockingGet());
                        return device;
                    })
                    .delay(1, TimeUnit.SECONDS)
                    .map(device -> {
                        if (device.getDeviceType().equals(DeviceType.OWNED_BY_SELF)) {
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
                )
        );
    }

    public void doOwnershipTransfer(Device deviceToOnboard) {
        mDisposables.add(mCheckConnectionUseCase.executeCompletable()
                .andThen(mGetOTMethodsUseCase.execute(deviceToOnboard)
                        .map(oxms -> {
                            if (oxms.size() > 1) {
                                return mOxmListener.onGetOxM(oxms);
                            } else {
                                return oxms.get(0);
                            }
                        }).filter(oxm -> oxm != null))
                .subscribeOn(mSchedulersFacade.io())
                .observeOn(mSchedulersFacade.ui())
                .doOnSubscribe(__ -> otmResponse.setValue(Response.loading()))
                .subscribe(
                        oxm -> mOnboardUseCase.execute(deviceToOnboard, oxm)
                                .delay(1, TimeUnit.SECONDS)
                                .map(device -> {
                                    device.setDeviceInfo(mGetDeviceInfoUseCase.execute(device).blockingGet());
                                    return device;
                                })
                                .delay(1, TimeUnit.SECONDS)
                                .map(device -> {
                                    device.setDeviceRole( mGetDeviceRoleUseCase.execute(device).blockingGet());
                                    return device;
                                })
                            .subscribeOn(mSchedulersFacade.io())
                            .observeOn(mSchedulersFacade.ui())
                            .subscribe(
                                    ownedDevice -> otmResponse.setValue(Response.success(ownedDevice)),
                                    throwable -> {
                                        if (throwable instanceof NetworkDisconnectedException) {
                                            mError.setValue(new ViewModelError(CommonError.NETWORK_DISCONNECTED, null));
                                        } else {
                                            otmResponse.setValue(Response.error(throwable));
                                        }
                                    }
                            )
                ));
    }

    public void offboard(Device deviceToOffboard) {
        mDisposables.add(mCheckConnectionUseCase.executeCompletable()
                .andThen(mOffboardUseCase.execute(deviceToOffboard)
                        .delay(1, TimeUnit.SECONDS)
                        .map(device -> {
                            device.setDeviceInfo(mGetDeviceInfoUseCase.execute(device).blockingGet());
                            return device;
                        })
                        .delay(1, TimeUnit.SECONDS)
                        .map(device -> {
                            device.setDeviceRole(mGetDeviceRoleUseCase.execute(device).blockingGet());
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

    public void pairwiseDevices(Device server, Device client) {
        mDisposables.add(mPairwiseDevicesUseCase.execute(client, server)
                .subscribeOn(mSchedulersFacade.io())
                .observeOn(mSchedulersFacade.ui())
                .doOnSubscribe(__ -> mProcessing.setValue(true))
                .doFinally(() -> mProcessing.setValue(false))
                .subscribe(
                        () -> {},
                        throwable -> mError.setValue(new ViewModelError(Error.PAIRWISE_DEVICES, null))
                ));
    }

    public void unlinkDevices(Device server, Device client) {
        mDisposables.add(mUnlinkDevicesUseCase.execute(client, server)
                .subscribeOn(mSchedulersFacade.io())
                .observeOn(mSchedulersFacade.ui())
                .doOnSubscribe(__ -> mProcessing.setValue(true))
                .doFinally(() -> mProcessing.setValue(false))
                .subscribe(
                        () -> {},
                        throwable -> mError.setValue(new ViewModelError(Error.UNLINK_DEVICES, null))
                ));
    }

    public void updateDevice(Device device) {
        mDisposables.add(mGetDeviceDatabaseUseCase.execute(device)
                .subscribeOn(mSchedulersFacade.io())
                .observeOn(mSchedulersFacade.ui())
                .doOnSubscribe( __ -> mProcessing.setValue(true))
                .doFinally(() -> mProcessing.setValue(false))
                .subscribe(
                        mUpdatedDevice::setValue,
                        throwable -> mError.setValue(new ViewModelError(Error.DB_ERROR, null))
                ));
    }

    public interface SelectOxMListener {
        OcfOxmType onGetOxM(List<OcfOxmType> supportedOxm);
    }

    public enum Error implements ViewModelErrorType {
        SCAN_DEVICES,
        PAIRWISE_DEVICES,
        UNLINK_DEVICES,
        DB_ERROR
    }
}

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

package org.openconnectivity.otgc.wlanscan.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import android.net.wifi.SupplicantState;

import org.openconnectivity.otgc.common.domain.usecase.CheckConnectionUseCase;
import org.openconnectivity.otgc.common.domain.usecase.RegisterSupplicantStateReceiverUseCase;
import org.openconnectivity.otgc.common.domain.usecase.EnableWiFiUseCase;
import org.openconnectivity.otgc.common.domain.usecase.RegisterWifiStateReceiverUseCase;
import org.openconnectivity.otgc.common.presentation.viewmodel.Response;
import org.openconnectivity.otgc.common.domain.rx.SchedulersFacade;
import org.openconnectivity.otgc.common.presentation.viewmodel.ViewModelError;
import org.openconnectivity.otgc.wlanscan.domain.model.WifiNetwork;
import org.openconnectivity.otgc.wlanscan.domain.usecase.ConnectToWiFiUseCase;
import org.openconnectivity.otgc.wlanscan.domain.usecase.RegisterScanResultsReceiverUseCase;
import org.openconnectivity.otgc.wlanscan.domain.usecase.ScanWiFiNetworksUseCase;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

public class WlanScanViewModel extends ViewModel {

    private final EnableWiFiUseCase enableWiFiUseCase;
    private final ScanWiFiNetworksUseCase scanWiFiNetworksUseCase;
    private final ConnectToWiFiUseCase connectToWiFiUseCase;
    private final CheckConnectionUseCase mCheckConnectionUseCase;

    private final SchedulersFacade schedulersFacade;

    private final CompositeDisposable disposables = new CompositeDisposable();

    private final MutableLiveData<Boolean> mProcessing = new MutableLiveData<>();
    private final MutableLiveData<ViewModelError> mError = new MutableLiveData<>();

    private final MutableLiveData<Response<Integer>> wifiStateResponse = new MutableLiveData<>();
    private final MutableLiveData<Response<List<WifiNetwork>>> scanResponse = new MutableLiveData<>();
    private final MutableLiveData<Response<SupplicantState>> stateResponse = new MutableLiveData<>();
    private final MutableLiveData<Response<Boolean>> connectResponse = new MutableLiveData<>();
    private final MutableLiveData<Response<Boolean>> wifiConnectedResponse = new MutableLiveData<>();

    @Inject
    WlanScanViewModel(
            RegisterWifiStateReceiverUseCase registerWifiStateReceiverUseCase,
            RegisterScanResultsReceiverUseCase registerScanResultsReceiverUseCase,
            RegisterSupplicantStateReceiverUseCase registerSupplicantStateReceiverUseCase,
            EnableWiFiUseCase enableWiFiUseCase,
            ScanWiFiNetworksUseCase scanWiFiNetworksUseCase,
            ConnectToWiFiUseCase connectToWiFiUseCase,
            CheckConnectionUseCase checkConnectionUseCase,
            SchedulersFacade schedulersFacade) {
        this.enableWiFiUseCase = enableWiFiUseCase;
        this.scanWiFiNetworksUseCase = scanWiFiNetworksUseCase;
        this.connectToWiFiUseCase = connectToWiFiUseCase;
        this.mCheckConnectionUseCase = checkConnectionUseCase;

        this.schedulersFacade = schedulersFacade;

        disposables.add(registerWifiStateReceiverUseCase.execute()
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .subscribe(
                        wifiState -> wifiStateResponse.setValue(Response.success(wifiState)),
                        throwable -> wifiStateResponse.setValue(Response.error(throwable))
                ));

        disposables.add(registerScanResultsReceiverUseCase.execute()
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .subscribe(
                        scanResults -> scanResponse.setValue(Response.success(scanResults)),
                        throwable -> scanResponse.setValue(Response.error(throwable))
                ));

        disposables.add(registerSupplicantStateReceiverUseCase.execute()
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .subscribe(
                        status -> stateResponse.setValue(Response.success(status)),
                        throwable -> stateResponse.setValue(Response.error(throwable))
                ));
    }

    @Override
    protected void onCleared() {
        disposables.clear();
    }

    public LiveData<Boolean> isProcessing() {
        return mProcessing;
    }

    public LiveData<ViewModelError> getError() {
        return mError;
    }

    public MutableLiveData<Response<Integer>> getWifiStateResponse() {
        return wifiStateResponse;
    }

    public MutableLiveData<Response<List<WifiNetwork>>> getScanResponse() {
        return scanResponse;
    }

    public MutableLiveData<Response<SupplicantState>> stateResponse() {
        return stateResponse;
    }

    public MutableLiveData<Response<Boolean>> getWifiConnectedResponse() { return wifiConnectedResponse; }

    public void scanWifiNetworks() {
        disposables.add(scanWiFiNetworksUseCase.execute()
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .doOnSubscribe(__ -> scanResponse.setValue(Response.loading()))
                .subscribe(
                        () -> {},
                        throwable -> {}
                ));
    }

    public void enableWiFi() {
        disposables.add(enableWiFiUseCase.execute()
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .doOnSubscribe(__ -> wifiStateResponse.setValue(Response.loading()))
                .subscribe(
                        () -> {},
                        throwable -> {}
                ));
    }

    public void connectToWifi(WifiNetwork network) {
        connectToWifi(network, null);
    }

    public void connectToWifi(WifiNetwork network, String password) {
        disposables.add(connectToWiFiUseCase.execute(network, password)
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .doOnSubscribe(__ -> connectResponse.setValue(Response.loading()))
                .doOnComplete(() -> connectResponse.setValue(Response.success(true)))
                .subscribe(
                        () -> {},
                        throwable -> connectResponse.setValue(Response.error(throwable))
                ));
    }

    public void checkIfWifiIsConnected() {
        disposables.add(mCheckConnectionUseCase.execute()
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .subscribe(
                        isConnected -> wifiConnectedResponse.setValue(Response.success(isConnected)),
                        throwable -> connectResponse.setValue(Response.error(throwable))
                ));
    }
}

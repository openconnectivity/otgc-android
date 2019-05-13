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

package org.openconnectivity.otgc.domain.usecase.wifi;

import android.net.wifi.WifiConfiguration;

import org.openconnectivity.otgc.data.repository.WlanRepository;
import org.openconnectivity.otgc.domain.model.WifiNetwork;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Single;

public class ConnectToWiFiUseCase {
    private final WlanRepository wlanRepository;

    @Inject
    public ConnectToWiFiUseCase(WlanRepository wlanRepository) {
        this.wlanRepository = wlanRepository;
    }

    public Completable execute(WifiNetwork network, String password) {
        Single<WifiConfiguration> configureWifiNetwork;
        if (network.isSecured()) {
            if (network.isWep()) {
                configureWifiNetwork = wlanRepository.configureWepWifi(network.getName(), password);
            } else if (network.isWpa()) {
                configureWifiNetwork = wlanRepository.configureWpaWifi(network.getName(), password);
            } else {
                configureWifiNetwork = wlanRepository.configureWpa2Wifi(network.getName(), password);
            }
        } else {
            configureWifiNetwork = wlanRepository.configureOpenWifi(network.getName());
        }

        return wlanRepository.isWifiEnabled()
                .andThen(wlanRepository.getWifiConfiguration(network.getName()))
                .onErrorResumeNext(configureWifiNetwork)
                .flatMapCompletable(wlanRepository::connectToWifi);
    }
}

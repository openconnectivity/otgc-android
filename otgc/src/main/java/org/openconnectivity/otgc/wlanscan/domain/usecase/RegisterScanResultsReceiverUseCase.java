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

package org.openconnectivity.otgc.wlanscan.domain.usecase;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import org.openconnectivity.otgc.common.data.repository.WlanRepository;
import org.openconnectivity.otgc.wlanscan.domain.model.WifiNetwork;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

public class RegisterScanResultsReceiverUseCase {

    private final WlanRepository wlanRepository;

    @Inject
    public RegisterScanResultsReceiverUseCase(WlanRepository wlanRepository) {
        this.wlanRepository = wlanRepository;
    }

    public Observable<List<WifiNetwork>> execute() {
        return wlanRepository.registerScanResultsReceiver()
                .map(scanResults -> {
                    // Create Temporary HashMap
                    HashMap<String, ScanResult> map = new HashMap<>();

                    // Add ScanResults to Map to remove duplicates
                    for (ScanResult scanResult : scanResults) {
                        if (scanResult.SSID != null &&
                                !scanResult.SSID.isEmpty()) {
                            if (map.get(scanResult.SSID) == null
                                || map.get(scanResult.SSID).level < scanResult.level) {
                                map.put(scanResult.SSID, scanResult);
                            }
                        }
                    }

                    // Add to new list
                    List<ScanResult> sortedWifiList = new ArrayList<>(map.values());

                    // Create Comparator to sort by level
                    Comparator<ScanResult> comparator = (lhs, rhs) ->
                        Integer.compare(rhs.level, lhs.level);

                    // Apply Comparator and sort
                    Collections.sort(sortedWifiList, comparator);

                    return sortedWifiList;
                })
                .map(scanResults -> {
                    List<WifiNetwork> uiResults = new ArrayList<>();

                    for (ScanResult scanResult : scanResults) {
                        WifiNetwork wifiNetwork = new WifiNetwork(
                                scanResult.SSID,
                                scanResult.capabilities,
                                WifiManager.calculateSignalLevel(scanResult.level, 4)
                        );
                        uiResults.add(wifiNetwork);
                    }

                    return uiResults;
                });
    }
}

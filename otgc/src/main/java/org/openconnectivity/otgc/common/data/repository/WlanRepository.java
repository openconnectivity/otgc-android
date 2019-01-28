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

package org.openconnectivity.otgc.common.data.repository;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.GroupCipher;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiConfiguration.PairwiseCipher;
import android.net.wifi.WifiConfiguration.Protocol;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;

import org.openconnectivity.otgc.common.data.model.WifiAdapterNotEnabledException;
import org.openconnectivity.otgc.common.data.model.WifiNetworkNotEnabledException;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import timber.log.Timber;

@Singleton
public class WlanRepository {

    private Context mContext;
    private WifiManager mWifiManager;
    private ConnectivityManager mConnManager;

    @Inject
    public WlanRepository(Context context) {
        this.mContext = context;

        this.mWifiManager = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        this.mConnManager = (ConnectivityManager) mContext.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public Observable<Integer> registerWifiStateReceiver() {
        return Observable.create(emitter ->
                mContext.registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    emitter.onNext(intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN));
                }
            }, new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION))
        );
    }

    public Observable<List<ScanResult>> registerScanResultsReceiver() {
        return Observable.create(emitter ->
                mContext.registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    emitter.onNext(mWifiManager.getScanResults());
                }
            }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
        );
    }

    public Observable<SupplicantState> registerSupplicantStateReceiver() {
        return Observable.create(emitter ->
                mContext.registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    int suplError = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, -1);
                    if (suplError == -1) {
                        emitter.onNext(intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE));
                    }
                }
            }, new IntentFilter(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION))
        );
    }

    public Completable enableWiFi() {
        return Completable.create(emitter -> mWifiManager.setWifiEnabled(true));
    }

    public Completable scanWiFiNetworks() {
        return Completable.create(emitter -> mWifiManager.startScan());
    }

    public Completable isWifiEnabled() {
        return Completable.create(emitter -> {
            if (mWifiManager.isWifiEnabled()) {
                emitter.onComplete();
            } else {
                emitter.onError(new WifiAdapterNotEnabledException());
            }
        });
    }

    public Single<WifiConfiguration> getWifiConfiguration(@NonNull String ssid) {
        return Single.create(emitter -> {
            for (WifiConfiguration wifiConfiguration : mWifiManager.getConfiguredNetworks()) {
                if (wifiConfiguration.SSID != null &&
                        wifiConfiguration.SSID.equals("\"" + ssid + "\"")) {
                    emitter.onSuccess(wifiConfiguration);
                }
            }

            emitter.onSuccess(null);
        });
    }

    public Single<WifiConfiguration> configureOpenWifi(@NonNull String ssid) {
        return Single.create(emitter -> {
            WifiConfiguration config = new WifiConfiguration();
            config.SSID = "\"" + ssid + "\"";

            config.allowedKeyManagement.set(KeyMgmt.NONE);

            emitter.onSuccess(config);
        });
    }

    public Single<WifiConfiguration> configureWepWifi(@NonNull String ssid,
                                                          @NonNull String password) {
        return Single.create(emitter -> {
            WifiConfiguration config = new WifiConfiguration();
            config.SSID = "\"" + ssid + "\"";

            config.wepKeys[0] = "\"" + password + "\"";
            config.wepTxKeyIndex = 0;

            config.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
            config.allowedAuthAlgorithms.set(AuthAlgorithm.SHARED);

            config.allowedKeyManagement.set(KeyMgmt.NONE);

            config.allowedGroupCiphers.set(GroupCipher.WEP40);
            config.allowedGroupCiphers.set(GroupCipher.WEP104);

            emitter.onSuccess(config);
        });
    }

    public Single<WifiConfiguration> configureWpaWifi(@NonNull String ssid,
                                                      @NonNull String password) {
        return configureWpaWpa2Wifi(ssid, password, false);
    }

    public Single<WifiConfiguration> configureWpa2Wifi(@NonNull String ssid,
                                                      @NonNull String password) {
        return configureWpaWpa2Wifi(ssid, password, true);
    }

    private Single<WifiConfiguration> configureWpaWpa2Wifi(@NonNull String ssid,
                                                          @NonNull String password,
                                                          boolean isWpa2) {
        return Single.create(emitter -> {
            WifiConfiguration config = new WifiConfiguration();
            config.SSID = "\"" + ssid + "\"";

            config.preSharedKey = "\"" + password + "\"";

            config.allowedGroupCiphers.set(GroupCipher.TKIP);
            config.allowedGroupCiphers.set(GroupCipher.CCMP);

            config.allowedKeyManagement.set(KeyMgmt.WPA_PSK);

            config.allowedPairwiseCiphers.set(PairwiseCipher.CCMP);
            config.allowedPairwiseCiphers.set(PairwiseCipher.TKIP);

            config.allowedProtocols.set(isWpa2 ? Protocol.RSN : Protocol.WPA);

            emitter.onSuccess(config);
        });
    }

    public Completable connectToWifi(WifiConfiguration configuration) {
        return Completable.create(emitter -> {
            int networkId = configuration.networkId;
            if (networkId == -1) {
                networkId = mWifiManager.addNetwork(configuration);
            }

            if (networkId != -1
                    && mWifiManager.enableNetwork(networkId, true)) {
                mWifiManager.reconnect();
            } else {
                emitter.onError(new WifiNetworkNotEnabledException());
            }

            emitter.onComplete();
        });
    }

    public Single<Boolean> isConnectedToWiFi() {
        return Single.create(emitter -> {
            boolean isConnected = false;
            Network[] networks = mConnManager.getAllNetworks();
            int i = 0;
            while (i < networks.length && !isConnected) {
                NetworkInfo networkInfo = mConnManager.getNetworkInfo(networks[i]);
                if (networkInfo != null) {
                    if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                        if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                            isConnected = true;
                        } else if (networkInfo.getState().equals(NetworkInfo.State.CONNECTING)) {
                            isConnected = retryIfConnecting(networks[i]);
                        }
                    }
                }
                i++;
            }

            emitter.onSuccess(isConnected);
        });
    }

    private boolean retryIfConnecting(Network network) {
        boolean isConnected = false;
        int retry = 0;
        while (retry < 3 && !isConnected) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Timber.e(e);
                Thread.currentThread().interrupt();
            }

            NetworkInfo networkInfo = mConnManager.getNetworkInfo(network);
            isConnected =
                    networkInfo.getState().equals(NetworkInfo.State.CONNECTED);
            retry++;
        }

        return isConnected;
    }
}

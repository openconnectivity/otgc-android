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

package org.openconnectivity.otgc.devicelist.data.repository;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;

import org.iotivity.base.OcResource;
import org.iotivity.service.easysetup.mediator.DeviceProp;
import org.iotivity.service.easysetup.mediator.DevicePropProvisioningCallback;
import org.iotivity.service.easysetup.mediator.DevicePropProvisioningStatus;
import org.iotivity.service.easysetup.mediator.ESException;
import org.iotivity.service.easysetup.mediator.EasySetup;
import org.iotivity.service.easysetup.mediator.RemoteEnrollee;
import org.iotivity.service.easysetup.mediator.enums.ESResult;
import org.iotivity.service.easysetup.mediator.enums.WIFI_AUTHTYPE;
import org.iotivity.service.easysetup.mediator.enums.WIFI_ENCTYPE;
import org.openconnectivity.otgc.devicelist.data.model.EasySetupException;
import org.openconnectivity.otgc.devicelist.data.model.NoRemoteEnrolleeException;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Completable;
import timber.log.Timber;

@Singleton
public class EasySetupRepository {

    private Context mContext;

    @Inject
    EasySetupRepository(Context context) {
        mContext = context;
    }

    public Completable configureAndConnect(OcResource ocResource,
                                           String enrollerSsid,
                                           String enrollerPwd,
                                           int authType,
                                           int encType) {
        return Completable.create(emitter -> {
            String securedHost = getCoapsIpv6Host(ocResource.getAllHosts());
            if (securedHost != null) {
                ocResource.setHost(securedHost);
            }

            RemoteEnrollee remoteEnrollee = EasySetup.getInstance(mContext).createRemoteEnrollee(ocResource);

            if (remoteEnrollee != null) {
                try {
                    remoteEnrollee.provisionDeviceProperties(
                            buildDeviceProperties(enrollerSsid, enrollerPwd, authType, encType),
                            new DevicePropProvisioningCallback() {
                        @Override
                        public void onProgress(DevicePropProvisioningStatus devicePropProvisioningStatus) {
                            final ESResult result = devicePropProvisioningStatus.getESResult();
                            if (result.equals(ESResult.ES_OK)) {
                                emitter.onComplete();
                            } else {
                                emitter.onError(new EasySetupException(result));
                            }
                        }
                    });
                } catch (ESException e) {
                    Timber.e(e.getLocalizedMessage());
                    emitter.onError(e);
                }
            } else {
                Timber.e("createRemoteEnrollee returned null");
                emitter.onError(new NoRemoteEnrolleeException());
            }
        });
    }

    private String getCoapsIpv6Host(@NonNull List<String> hosts) {
        String coapsIpv6Host = null;
        for (String host : hosts) {
            if (host.startsWith("coaps") && !host.contains(".")) {
                coapsIpv6Host = host;
                break;
            }
        }

        return coapsIpv6Host;
    }

    private DeviceProp buildDeviceProperties(String ssid,
                                             String pwd,
                                             int authType,
                                             int encType) {
        DeviceProp deviceProp = new DeviceProp();
        deviceProp.setWiFiProp(ssid, pwd, WIFI_AUTHTYPE.fromInt(authType),
                WIFI_ENCTYPE.fromInt(encType));

        String language;
        String country;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            language = mContext.getResources().getConfiguration().getLocales().get(0).getLanguage();
            country = mContext.getResources().getConfiguration().getLocales().get(0).getCountry();
        } else {
            language = mContext.getResources().getConfiguration().locale.getLanguage();
            country = mContext.getResources().getConfiguration().locale.getCountry();
        }
        deviceProp.setDevConfProp(language, country, "");

        return deviceProp;
    }
}

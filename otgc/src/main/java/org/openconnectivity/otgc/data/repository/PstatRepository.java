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

package org.openconnectivity.otgc.data.repository;

import org.iotivity.CborEncoder;
import org.iotivity.OCClientResponse;
import org.iotivity.OCEndpoint;
import org.iotivity.OCEndpointUtil;
import org.iotivity.OCMain;
import org.iotivity.OCQos;
import org.iotivity.OCResponseHandler;
import org.iotivity.OCStatus;
import org.iotivity.OCUuid;
import org.iotivity.OCUuidUtil;
import org.openconnectivity.otgc.domain.model.resource.secure.pstat.OcPstat;
import org.openconnectivity.otgc.domain.model.resource.secure.pstat.OcPstatDeviceState;
import org.openconnectivity.otgc.utils.constant.OcfDosType;
import org.openconnectivity.otgc.utils.constant.OcfResourceUri;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Completable;
import io.reactivex.Single;

@Singleton
public class PstatRepository {

    @Inject
    public PstatRepository() {

    }

    public Completable changeDeviceStatus(String endpoint, String deviceId, OcfDosType dosType) {
        return Completable.create(emitter -> {
            OCEndpoint ep = OCEndpointUtil.stringToEndpoint(endpoint, new String[1]);
            OCUuid uuid = OCUuidUtil.stringToUuid(deviceId);
            OCEndpointUtil.setDi(ep, uuid);

            OCResponseHandler handler = (OCClientResponse response) -> {
                OCStatus code = response.getCode();
                if (code == OCStatus.OC_STATUS_OK
                        || code == OCStatus.OC_STATUS_CHANGED) {
                    emitter.onComplete();
                } else {
                    emitter.onError(new Exception("Send POST /oic/sec/pstat error"));
                }
            };

            // Initialize POST
            if (OCMain.initPost(OcfResourceUri.PSTAT_URI, ep, null, handler, OCQos.HIGH_QOS)) {
                // Create pstat resource
                OcPstatDeviceState dos = new OcPstatDeviceState();
                dos.setDeviceOnboardingState((long)dosType.getValue());
                OcPstat pstat = new OcPstat();
                pstat.setDeviceState(dos);

                CborEncoder root = pstat.parseToCbor();

                if (!OCMain.doPost()) {
                    emitter.onError(new Exception("Do POST /oic/sec/pstat error"));
                }

            } else {
                emitter.onError(new Exception("Init POST /oic/sec/pstat error"));
            }

            OCEndpointUtil.freeEndpoint(ep);
        });
    }

    public Single<OcPstat> get(String endpoint, String deviceId) {
        return Single.create(emitter -> {
            OCEndpoint ep = OCEndpointUtil.stringToEndpoint(endpoint, new String[1]);
            OCUuid uuid = OCUuidUtil.stringToUuid(deviceId);
            OCEndpointUtil.setDi(ep, uuid);

            OCResponseHandler handler = (OCClientResponse response) -> {
                OCStatus code = response.getCode();
                if (code == OCStatus.OC_STATUS_OK) {
                    OcPstat pstat = new OcPstat();
                    pstat.parseOCRepresentation(response.getPayload());
                    emitter.onSuccess(pstat);
                } else {
                    emitter.onError(new Exception("Get /oic/sec/pstat error"));
                }
            };

            if (!OCMain.doGet(OcfResourceUri.PSTAT_URI, ep, null, handler, OCQos.HIGH_QOS)) {
                emitter.onError(new Exception("Do GET /oic/sec/pstat error"));
            }

            OCEndpointUtil.freeEndpoint(ep);
        });
    }

    public Completable post(String endpoint, String deviceId, OcPstat pstat) {
        return Completable.create(emitter -> {
            OCEndpoint ep = OCEndpointUtil.stringToEndpoint(endpoint, new String[1]);
            OCUuid uuid = OCUuidUtil.stringToUuid(deviceId);
            OCEndpointUtil.setDi(ep, uuid);

            OCResponseHandler handler = (OCClientResponse response) -> {
                OCStatus code = response.getCode();
                if (code == OCStatus.OC_STATUS_OK
                        || code == OCStatus.OC_STATUS_CHANGED) {
                    emitter.onComplete();
                } else {
                    emitter.onError(new Exception("POST /oic/sec/pstat error - code: " + code));
                }
            };

            if (OCMain.initPost(OcfResourceUri.PSTAT_URI, ep, null, handler, OCQos.HIGH_QOS)) {
                CborEncoder root = pstat.parseToCbor();

                if (!OCMain.doPost()) {
                    emitter.onError(new Exception("Do POST /oic/sec/pstat error"));
                }
            } else {
                emitter.onError(new Exception("Init POST /oic/sec/pstat error"));
            }

            OCEndpointUtil.freeEndpoint(ep);
        });
    }
}
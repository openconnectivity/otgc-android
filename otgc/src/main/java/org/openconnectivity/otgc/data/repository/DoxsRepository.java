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
import org.iotivity.OCObt;
import org.iotivity.OCObtDeviceStatusHandler;
import org.iotivity.OCQos;
import org.iotivity.OCRandomPinHandler;
import org.iotivity.OCResponseHandler;
import org.iotivity.OCStatus;
import org.iotivity.OCUuid;
import org.iotivity.OCUuidUtil;
import org.openconnectivity.otgc.domain.model.resource.secure.doxm.OcDoxm;
import org.openconnectivity.otgc.utils.constant.OcfOxmType;
import org.openconnectivity.otgc.utils.constant.OcfResourceUri;
import org.openconnectivity.otgc.utils.handler.OCSetRandomPinHandler;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Completable;
import io.reactivex.Single;
import timber.log.Timber;

@Singleton
public class DoxsRepository {

    private OCSetRandomPinHandler randomPinHandler;

    @Inject
    public DoxsRepository(){}

    public Completable doOwnershipTransfer(String deviceId, OcfOxmType oxm) {
        return Completable.create(emitter -> {
            OCUuid uuid = OCUuidUtil.stringToUuid(deviceId);

            OCObtDeviceStatusHandler handler = (OCUuid ocUuid, int status) -> {
                if (status >= 0) {
                    Timber.d("Successfully performed OTM on device " + OCUuidUtil.uuidToString(ocUuid));
                    emitter.onComplete();
                } else {
                    String error = "ERROR performing ownership transfer on device " + OCUuidUtil.uuidToString(ocUuid);
                    Timber.e(error);
                    emitter.onError(new Exception(error));
                }
            };

            int ret = -1;
            if (oxm == OcfOxmType.OC_OXMTYPE_JW) {
                ret = OCObt.performJustWorksOtm(uuid, handler);
            } else if (oxm == OcfOxmType.OC_OXMTYPE_RDP) {
                ret = OCObt.requestRandomPin(uuid, (OCUuid ocUuid, int status) -> {
                    if (status >= 0) {
                        Timber.d("Successfully request Random PIN " + OCUuidUtil.uuidToString(ocUuid));
                        String pin = randomPinHandler.handler();
                        if (OCObt.performRandomPinOtm(uuid, pin, pin.length(), handler) != -1){
                            emitter.onComplete();
                        } else {
                            String error = "ERROR send random PIN on device " + OCUuidUtil.uuidToString(ocUuid);
                            Timber.e(error);
                            emitter.onError(new Exception(error));
                        }
                    } else {
                        String error = "ERROR requesting random PIN on device " + OCUuidUtil.uuidToString(ocUuid);
                        Timber.e(error);
                        emitter.onError(new Exception(error));
                    }
                });
			} else if (oxm == OcfOxmType.OC_OXMTYPE_MFG_CERT) {
                ret = OCObt.performCertOtm(uuid, handler);
            }

            if (ret >= 0) {
                Timber.d("Successfully issued request to perform ownership transfer");
            } else {
                String error = "ERROR issuing request to perform ownership transfer";
                Timber.e(error);
                emitter.onError(new Exception(error));
            }
        });
    }

    public Completable resetDevice(String deviceId) {
        return Completable.create(emitter -> {
            OCUuid uuid = OCUuidUtil.stringToUuid(deviceId);

            OCObtDeviceStatusHandler handler = (OCUuid ocUuid, int status) -> {
                if (status >= 0) {
                    Timber.d("Successfully performed hard RESET to device " + OCUuidUtil.uuidToString(ocUuid));
                    emitter.onComplete();
                } else {
                    String error = "ERROR performing hard RESET to device " + OCUuidUtil.uuidToString(ocUuid);
                    Timber.e(error);
                    emitter.onError(new Exception(error));
                }
            };

            int ret = OCObt.deviceHardReset(uuid, handler);
            if (ret >= 0) {
                Timber.d("Successfully issued request to perform hard RESET");
            } else {
                String error = "ERROR issuing request to perform hard RESET";
                Timber.e(error);
                emitter.onError(new Exception(error));
            }
        });
    }

    public Single<OcDoxm> retrieveOTMethods(String endpoint) {
        return Single.create(emitter -> {
            OCEndpoint ep = OCEndpointUtil.newEndpoint();
            OCEndpointUtil.stringToEndpoint(endpoint, ep, new String[1]);

            OCResponseHandler handler = (OCClientResponse response) -> {
                OCStatus code = response.getCode();
                if (code == OCStatus.OC_STATUS_OK) {
                    OcDoxm doxm = new OcDoxm();
                    doxm.parseOCRepresentation(response.getPayload());
                    emitter.onSuccess(doxm);
                } else {
                    String error = "GET /oic/sec/doxm error";
                    Timber.e(error);
                    emitter.onError(new Exception(error));
                }
            };

            if (!OCMain.doGet(OcfResourceUri.DOXM_URI, ep, null, handler, OCQos.HIGH_QOS)) {
                String error = "Send GET /oic/sec/doxm error";
                Timber.e(error);
                emitter.onError(new Exception(error));
            }

            OCEndpointUtil.freeEndpoint(ep);
        });
    }

    public void setDisplayPinListener(OCRandomPinHandler displayPinListener) {
        OCMain.setRandomPinHandler(displayPinListener);
    }

    public void setRandomPinCallbackListener(OCSetRandomPinHandler randomPinCallbackListener) {
        this.randomPinHandler = randomPinCallbackListener;
    }

    public Single<OcDoxm> get(String endpoint, String deviceId) {
        return Single.create(emitter -> {
            OCEndpoint ep = OCEndpointUtil.newEndpoint();
            OCEndpointUtil.stringToEndpoint(endpoint, ep, new String[1]);
            OCUuid di = OCUuidUtil.stringToUuid(deviceId);
            OCEndpointUtil.setDi(ep, di);

            OCResponseHandler handler = (OCClientResponse response) -> {
                OCStatus code = response.getCode();
                if (code == OCStatus.OC_STATUS_OK) {
                    OcDoxm doxm = new OcDoxm();
                    doxm.parseOCRepresentation(response.getPayload());
                    emitter.onSuccess(doxm);
                } else {
                    emitter.onError(new Exception("GET /oic/sec/doxm error - code: " + code));
                }
            };

            if (!OCMain.doGet(OcfResourceUri.DOXM_URI, ep, null, handler, OCQos.HIGH_QOS)) {
                emitter.onError(new Exception("Do GET /oic/sec/doxm error"));
            }

            OCEndpointUtil.freeEndpoint(ep);
        });
    }

    public Completable post(String endpoint, String deviceId, OcDoxm doxm) {
        return Completable.create(emitter -> {
            OCEndpoint ep = OCEndpointUtil.newEndpoint();
            OCEndpointUtil.stringToEndpoint(endpoint, ep, new String[1]);
            OCUuid di = OCUuidUtil.stringToUuid(deviceId);
            OCEndpointUtil.setDi(ep, di);

            OCResponseHandler handler = (OCClientResponse response) -> {
                OCStatus code = response.getCode();
                if (code == OCStatus.OC_STATUS_OK
                        || code == OCStatus.OC_STATUS_CHANGED) {
                    emitter.onComplete();
                } else {
                    emitter.onError(new Exception("POST /oic/sec/doxm error - code: " + code));
                }
            };

            if (OCMain.initPost(OcfResourceUri.DOXM_URI, ep, null, handler, OCQos.HIGH_QOS)) {
                CborEncoder root = doxm.parseToCbor();

                if (!OCMain.doPost()) {
                    emitter.onError(new Exception("Do POST /oic/sec/doxm error"));
                }
            } else {
                emitter.onError(new Exception("Init POST /oic/sec/doxm error"));
            }

            OCEndpointUtil.freeEndpoint(ep);
        });
    }
}

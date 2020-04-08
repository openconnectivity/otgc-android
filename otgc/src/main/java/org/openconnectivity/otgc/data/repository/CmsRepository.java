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

import org.iotivity.*;
import org.openconnectivity.otgc.domain.model.resource.secure.cred.OcCredentials;
import org.openconnectivity.otgc.domain.model.resource.secure.csr.OcCsr;
import org.openconnectivity.otgc.utils.constant.OcfResourceUri;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Completable;
import io.reactivex.Single;
import timber.log.Timber;

@Singleton
public class CmsRepository {

    @Inject
    CmsRepository() {

    }

    public Single<OcCredentials> getCredentials(String deviceId) {
        return Single.create(emitter -> {
            OCUuid uuid = OCUuidUtil.stringToUuid(deviceId);

            OCObtCredsHandler handler = (OCCreds credentials) -> {
                if (credentials != null) {
                    OcCredentials creds = new OcCredentials(credentials, true);
                    emitter.onSuccess(creds);
                } else {
                    String error = "GET credentials error";
                    Timber.e(error);
                    emitter.onError(new Exception(error));
                }
            };

            int ret = OCObt.retrieveCreds(uuid, handler);
            if (ret >= 0) {
                System.out.println("\nSuccessfully issued request to RETRIEVE /oic/sec/cred");
                Timber.d("Successfully issued request to retrieve the credentials");
            } else {
                String error = "GET credentials error";
                Timber.e(error);
                emitter.onError(new Exception(error));
            }
        });
    }

    public Single<OcCsr> retrieveCsr(String endpoint, String deviceId) {
        return Single.create(emitter -> {
            OCEndpoint ep = OCEndpointUtil.stringToEndpoint(endpoint, new String[1]);
            OCUuid uuid = OCUuidUtil.stringToUuid(deviceId);
            OCEndpointUtil.setDi(ep, uuid);

            OCResponseHandler handler = (OCClientResponse response)-> {
                OCStatus code = response.getCode();
                if (code == OCStatus.OC_STATUS_OK) {
                    OcCsr csr = new OcCsr();
                    csr.parseOCRepresentation(response.getPayload());
                    emitter.onSuccess(csr);
                } else {
                    emitter.onError(new Exception("Send GET to /oic/sec/csr error"));
                }
            };

            if (!OCMain.doGet(OcfResourceUri.CSR_URI, ep, null, handler, OCQos.HIGH_QOS)) {
                emitter.onError(new Exception("Send GET to /oic/sec/csr error"));
            }

            OCEndpointUtil.freeEndpoint(ep);
        });
    }

    public Completable provisionIdentityCertificate(String deviceId) {
        return Completable.create(emitter -> {
            OCUuid di = OCUuidUtil.stringToUuid(deviceId);

            OCObtStatusHandler handler = (int status) -> {
                if (status >= 0) {
                    Timber.d("Provision identity certificate succeeded");
                    emitter.onComplete();
                } else {
                    emitter.onError(new IOException("Provision identity certificate error"));
                }
            };

            int ret = OCObt.provisionIdentityCertificate(di, handler);
            if (ret < 0) {
                emitter.onError(new IOException("Provision identity certificate error"));
            }
        });
    }

    public Completable provisionRoleCertificate(String deviceId, String roleId, String roleAuthority) {
        return Completable.create(emitter -> {
            OCUuid di = OCUuidUtil.stringToUuid(deviceId);

            OCRole roles = OCObt.addRoleId(null, roleId, roleAuthority);

            OCObtStatusHandler handler = (int status) -> {
                if (status >= 0) {
                    Timber.d("Provision role certificate succeeded");
                    emitter.onComplete();
                } else {
                    emitter.onError(new IOException("Provision role certificate error"));
                }
            };

            int ret = OCObt.provisionRoleCertificate(roles, di, handler);
            if (ret < 0) {
                emitter.onError(new IOException("Provision role certificate error"));
                OCObt.freeRoleId(roles);
            }
        });
    }

    public Completable provisionPairwiseCredential(String clientId, String serverId) {
        return Completable.create(emitter -> {
            OCUuid cliendDi = OCUuidUtil.stringToUuid(clientId);
            OCUuid serverDi = OCUuidUtil.stringToUuid(serverId);

            OCObtStatusHandler handler = (int status) -> {
                if (status >= 0) {
                    Timber.d("Successfully provisioned pair-wise credentials");
                    emitter.onComplete();
                } else {
                    String errorMsg = "ERROR provisioning pair-wise credentials";
                    Timber.e(errorMsg);
                    emitter.onError(new Exception(errorMsg));
                }
            };

            int ret = OCObt.provisionPairwiseCredentials(cliendDi, serverDi, handler);
            if (ret >= 0) {
                Timber.d("Successfully issued request to provision credentials");
            } else {
                String errorMsg = "ERROR issuing request to provision credentials";
                Timber.e(errorMsg);
                emitter.onError(new Exception(errorMsg));
            }
        });
    }

    public Completable deleteCredential(String deviceId, long credId) {
        return Completable.create(emitter -> {
            OCUuid uuid = OCUuidUtil.stringToUuid(deviceId);

            OCObtStatusHandler handler = (int status) -> {
                if (status >= 0) {
                    Timber.d("Delete credential success");
                    emitter.onComplete();
                } else {
                    String error = "Delete credential error";
                    Timber.e(error);
                    emitter.onError(new Exception(error));
                }
            };

            int ret = OCObt.deleteCredByCredId(uuid, (int)credId, handler);
            if (ret >= 0) {
                Timber.d("Successfully issued request to DELETE /oic/sec/cred");
            } else {
                String error = "DELETE request to /oic/sec/cred error";
                Timber.d(error);
                emitter.onError(new Exception(error));
            }
        });
    }

    public Single<OcCredentials> retrieveOwnCredentials() {
        return Single.create(emitter -> {
            OcCredentials creds = new OcCredentials(OCObt.retrieveOwnCreds(), false);
            emitter.onSuccess(creds);
        });
    }

    public Completable addTrustAnchor(String pemCert) {
        return Completable.create(emitter -> {
            if (OCPki.addTrustAnchor(0 /* First device */, pemCert.getBytes()) == -1) {
                emitter.onError(new Exception("Add trust anchor error"));
            }

            if (OCPki.addMfgTrustAnchor(0 /* First device */, pemCert.getBytes()) == -1) {
                emitter.onError(new Exception("Add manufacturer trust anchor error"));
            }

            emitter.onComplete();
        });
    }

    public Completable removeTrustAnchor(long credid) {
        return Completable.create(emitter -> {
            int ret = OCObt.deleteOwnCredByCredId((int)credid);
            if (ret >= 0) {
                Timber.d("Successfully DELETED cred");
                emitter.onComplete();
            } else {
                String error = "ERROR DELETING cred";
                Timber.e(error);
                emitter.onError(new Exception(error));
            }
        });
    }
}

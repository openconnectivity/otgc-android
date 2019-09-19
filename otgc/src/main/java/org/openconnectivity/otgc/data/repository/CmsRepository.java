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
import org.openconnectivity.otgc.domain.model.resource.secure.cred.OcCredPrivateData;
import org.openconnectivity.otgc.domain.model.resource.secure.cred.OcCredPublicData;
import org.openconnectivity.otgc.domain.model.resource.secure.cred.OcCredRole;
import org.openconnectivity.otgc.domain.model.resource.secure.cred.OcCredential;
import org.openconnectivity.otgc.domain.model.resource.secure.cred.OcCredentials;
import org.openconnectivity.otgc.domain.model.resource.secure.csr.OcCsr;
import org.openconnectivity.otgc.utils.constant.OcfCredType;
import org.openconnectivity.otgc.utils.constant.OcfCredUsage;
import org.openconnectivity.otgc.utils.constant.OcfEncoding;
import org.openconnectivity.otgc.utils.constant.OcfResourceUri;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.Single;
import timber.log.Timber;

@Singleton
public class CmsRepository {

    @Inject
    CmsRepository() {

    }

    public Single<OcCredentials> getCredentials(String endpoint, String deviceId) {
        return Single.create(emitter -> {
            OCEndpoint ep = OCEndpointUtil.stringToEndpoint(endpoint, new String[1]);
            OCUuid uuid = OCUuidUtil.stringToUuid(deviceId);
            OCEndpointUtil.setDi(ep, uuid);

            OCResponseHandler handler = (OCClientResponse response) -> {
                OCStatus code = response.getCode();
                if (code.equals(OCStatus.OC_STATUS_OK)) {
                    OcCredentials creds = new OcCredentials();
                    creds.parseOCRepresentation(response.getPayload());
                    emitter.onSuccess(creds);
                } else {
                    String error = "GET credentials error";
                    emitter.onError(new Exception(error));
                }
            };

            if (!OCMain.doGet(OcfResourceUri.CRED_URI, ep, null, handler, OCQos.HIGH_QOS)) {
                String error = "GET credentials error";
                emitter.onError(new Exception(error));
            }

            OCEndpointUtil.freeEndpoint(ep);
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

    public Completable provisionTrustAnchor(String endpoint, String deviceId, String rootCert) {
        return Completable.create(emitter -> {
            OCEndpoint ep = OCEndpointUtil.stringToEndpoint(endpoint, new String[1]);
            OCUuid di = OCUuidUtil.stringToUuid(deviceId);
            OCEndpointUtil.setDi(ep, di);

            OCResponseHandler handler = (OCClientResponse response) -> {
                OCStatus code = response.getCode();
                if (code.equals(OCStatus.OC_STATUS_OK) || code.equals(OCStatus.OC_STATUS_CHANGED)) {
                    Timber.d("Provision root certificate succeeded");
                    emitter.onComplete();
                } else {
                    emitter.onError(new IOException("Provision root certificate error"));
                }
            };

            if (OCMain.initPost(OcfResourceUri.CRED_URI, ep, null, handler, OCQos.HIGH_QOS)) {
                OcCredPublicData publicData = new OcCredPublicData();
                publicData.setPemData(rootCert);
                publicData.setEncoding(OcfEncoding.OC_ENCODING_PEM);

                OcCredential cred = new OcCredential();
                cred.setSubjectuuid("*");
                cred.setCredtype(OcfCredType.OC_CREDTYPE_CERT);
                cred.setCredusage(OcfCredUsage.OC_CREDUSAGE_TRUSTCA);
                cred.setPublicData(publicData);
                List<OcCredential> credList = new ArrayList<>();
                credList.add(cred);

                OcCredentials creds = new OcCredentials();
                creds.setCredList(credList);

                CborEncoder root = creds.parseToCbor();
                if (OCMain.doPost()) {
                    Timber.d("Sent POST request to /oic/sec/cred");
                } else {
                    String error = "Could not send POST request to /oic/sec/cred";
                    Timber.e(error);
                    emitter.onError(new Exception(error));
                }
            } else {
                String error = "Could not init POST request to /oic/sec/cred";
                Timber.e(error);
                emitter.onError(new Exception(error));
            }

            OCEndpointUtil.freeEndpoint(ep);
        });
    }

    public Completable provisionIdentityCertificate(String endpoint, String deviceId, String rootCert, String identityCert) {
        return provisionTrustAnchor(endpoint, deviceId, rootCert)
                .andThen(
                    Completable.create(emitter -> {
                        OCEndpoint ep = OCEndpointUtil.stringToEndpoint(endpoint, new String[1]);
                        OCUuid di = OCUuidUtil.stringToUuid(deviceId);
                        OCEndpointUtil.setDi(ep, di);

                        OCResponseHandler handler = (OCClientResponse response) -> {
                            OCStatus code = response.getCode();
                            if (code.equals(OCStatus.OC_STATUS_OK) || code.equals(OCStatus.OC_STATUS_CHANGED)) {
                                Timber.d("Provision identity certificate succeeded");
                                emitter.onComplete();
                            } else {
                                emitter.onError(new IOException("Provision identity certificate error"));
                            }
                        };

                        if (OCMain.initPost(OcfResourceUri.CRED_URI, ep, null, handler, OCQos.HIGH_QOS)) {
                            OcCredPublicData publicData = new OcCredPublicData();
                            publicData.setPemData(identityCert);
                            publicData.setEncoding(OcfEncoding.OC_ENCODING_PEM);

                            OcCredential cred = new OcCredential();
                            cred.setSubjectuuid(deviceId);
                            cred.setCredtype(OcfCredType.OC_CREDTYPE_CERT);
                            cred.setCredusage(OcfCredUsage.OC_CREDUSAGE_CERT);
                            cred.setPublicData(publicData);
                            List<OcCredential> credList = new ArrayList<>();
                            credList.add(cred);

                            OcCredentials creds = new OcCredentials();
                            creds.setCredList(credList);

                            CborEncoder root = creds.parseToCbor();
                            if (OCMain.doPost()) {
                                Timber.d("Sent POST request to /oic/sec/cred");
                            } else {
                                String error = "Could not send POST request to /oic/sec/cred";
                                Timber.e(error);
                                emitter.onError(new Exception(error));
                            }
                        } else {
                            String error = "Could not init POST request to /oic/sec/cred";
                            Timber.e(error);
                            emitter.onError(new Exception(error));
                        }

                        OCEndpointUtil.freeEndpoint(ep);
                    }));
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

    public Completable provisionRoleCertificate(String endpoint, String deviceId, String roleCert, String roleId, String roleAuthority) {
        return Completable.create(emitter -> {
            OCEndpoint ep = OCEndpointUtil.stringToEndpoint(endpoint, new String[1]);
            OCUuid di = OCUuidUtil.stringToUuid(deviceId);
            OCEndpointUtil.setDi(ep, di);

            OCResponseHandler handler = (OCClientResponse response) -> {
                OCStatus code = response.getCode();
                if (code.equals(OCStatus.OC_STATUS_OK) || code.equals(OCStatus.OC_STATUS_CHANGED)) {
                    Timber.d("Provision role certificate succeeded");
                    emitter.onComplete();
                } else {
                    emitter.onError(new IOException("Provision role certificate error"));
                }
            };

            if (OCMain.initPost(OcfResourceUri.CRED_URI, ep, null, handler, OCQos.HIGH_QOS)) {
                OcCredPublicData publicData = new OcCredPublicData();
                publicData.setPemData(roleCert);
                publicData.setEncoding(OcfEncoding.OC_ENCODING_PEM);

                OcCredRole role = new OcCredRole();
                role.setRole(roleId);
                role.setAuthority(roleAuthority);

                OcCredential cred = new OcCredential();
                cred.setSubjectuuid(deviceId);
                cred.setCredtype(OcfCredType.OC_CREDTYPE_CERT);
                cred.setCredusage(OcfCredUsage.OC_CREDUSAGE_ROLECERT);
                cred.setPublicData(publicData);
                cred.setRoleid(role);
                List<OcCredential> credList = new ArrayList<>();
                credList.add(cred);

                OcCredentials creds = new OcCredentials();
                creds.setCredList(credList);

                CborEncoder root = creds.parseToCbor();
                if (OCMain.doPost()) {
                    Timber.d("Sent POST request to /oic/sec/cred");
                } else {
                    String error = "Could not send POST request to /oic/sec/cred";
                    Timber.e(error);
                    emitter.onError(new Exception(error));
                }
            } else {
                String error = "Could not init POST request to /oic/sec/cred";
                Timber.e(error);
                emitter.onError(new Exception(error));
            }

            OCEndpointUtil.freeEndpoint(ep);
        });
    }

    public Completable provisionRoleCertificate(String deviceId, String roleId, String roleAuthority) {
        return Completable.create(emitter -> {
            OCUuid di = OCUuidUtil.stringToUuid(deviceId);

            OCRole role = new OCRole();
            role.setRole(roleId);
            role.setAuthority(roleAuthority);

            OCObtStatusHandler handler = (int status) -> {
                if (status >= 0) {
                    Timber.d("Provision role certificate succeeded");
                    emitter.onComplete();
                } else {
                    emitter.onError(new IOException("Provision role certificate error"));
                }
            };

            int ret = OCObt.provisionRoleCertificate(role, di, handler);
            if (ret < 0) {
                emitter.onError(new IOException("Provision role certificate error"));
            }
        });
    }

    public Completable createPskCredential(String endpoint, String deviceId, String targetUuid, byte[] symmetricKey) {
        return Completable.create(emitter -> {
            OCEndpoint ep = OCEndpointUtil.stringToEndpoint(endpoint, new String[1]);
            OCUuid di = OCUuidUtil.stringToUuid(deviceId);
            OCEndpointUtil.setDi(ep, di);

            OCResponseHandler handler = (OCClientResponse response) -> {
                OCStatus code = response.getCode();
                if (code.equals(OCStatus.OC_STATUS_OK) || code.equals(OCStatus.OC_STATUS_CHANGED)) {
                    Timber.d("Provision identity certificate succeeded");
                    emitter.onComplete();
                } else {
                    emitter.onError(new IOException("Provision identity certificate error"));
                }
            };

            if (OCMain.initPost(OcfResourceUri.CRED_URI, ep, null, handler, OCQos.HIGH_QOS)) {
                OcCredPrivateData privateData = new OcCredPrivateData();
                privateData.setDataDer(symmetricKey);
                privateData.setEncoding(OcfEncoding.OC_ENCODING_RAW);

                OcCredential cred = new OcCredential();
                cred.setSubjectuuid(targetUuid);
                cred.setCredtype(OcfCredType.OC_CREDTYPE_PSK);
                cred.setPrivateData(privateData);
                List<OcCredential> credList = new ArrayList<>();
                credList.add(cred);

                OcCredentials creds = new OcCredentials();
                creds.setCredList(credList);

                CborEncoder root = creds.parseToCbor();
                if (OCMain.doPost()) {
                    Timber.d("Sent POST request to /oic/sec/cred");
                } else {
                    String error = "Could not send POST request to /oic/sec/cred";
                    Timber.e(error);
                    emitter.onError(new Exception(error));
                }
            } else {
                String error = "Could not init POST request to /oic/sec/cred";
                Timber.e(error);
                emitter.onError(new Exception(error));
            }

            OCEndpointUtil.freeEndpoint(ep);
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

    public Completable deleteCredential(String endpoint, String deviceId, long credId) {
        return Completable.create(emitter -> {
            OCEndpoint ep = OCEndpointUtil.stringToEndpoint(endpoint, new String[1]);
            OCUuid di = OCUuidUtil.stringToUuid(deviceId);
            OCEndpointUtil.setDi(ep, di);

            OCResponseHandler handler = (OCClientResponse response) -> {
                OCStatus code = response.getCode();
                if (code == OCStatus.OC_STATUS_OK
                        || code == OCStatus.OC_STATUS_DELETED) {
                    Timber.d("Delete credential success");
                    emitter.onComplete();
                } else {
                    String error = "Delete credential error";
                    Timber.e(error);
                    emitter.onError(new Exception(error));
                }
            };

            if (!OCMain.doDelete(OcfResourceUri.CRED_URI, ep, OcfResourceUri.DELETE_CRED_QUERY + credId, handler, OCQos.HIGH_QOS)) {
                String error = "DELETE request to /oic/sec/cred error";
                Timber.d(error);
                emitter.onError(new Exception(error));
            }

            OCEndpointUtil.freeEndpoint(ep);
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

    public Completable removeTrustAnchor(long device, long credid) {
        return Completable.create(emitter -> {
            OCPki.removeCredentialByCredid(device, (int)credid);
            emitter.onComplete();
        });
    }
}

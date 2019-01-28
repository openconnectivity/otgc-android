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

package org.openconnectivity.otgc.credential.data.repository;

import org.iotivity.base.CredType;
import org.iotivity.base.DeviceStatus;
import org.iotivity.base.OcException;
import org.iotivity.base.OcSecureResource;
import org.iotivity.base.OicSecCreds;

import java.io.IOException;
import java.util.EnumSet;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Completable;
import io.reactivex.Single;
import timber.log.Timber;

@Singleton
public class CmsRepository {

    private int mCredId = 0;

    @Inject
    CmsRepository() {

    }

    public Single<OicSecCreds> getCredentials(OcSecureResource ocSecureResource) {
        return Single.create(emitter -> {
            try {
                if (ocSecureResource.getDeviceStatus().equals(DeviceStatus.ON)) {
                    ocSecureResource.getCredentials((creds, hasErrors) -> {
                        if (hasErrors == 0) {
                            emitter.onSuccess(creds);
                        } else {
                            // TODO: Create GetCredsException
                            emitter.onError(new IOException("Get Credendials Exception"));
                        }
                    });
                } else {
                    emitter.onError(new IOException("Device is off"));
                }
            } catch (OcException e) {
                Timber.e(e);
                emitter.onError(e);
            }
        });
    }

    public Completable provisionIdentityCertificate(OcSecureResource ocSecureResource) {
        return Completable.create(emitter -> {
            try {
                ocSecureResource.provisionIdentityCertificate((provisionResults, hasError) -> {
                    if (hasError == 0) {
                        emitter.onComplete();
                    } else {
                        // TODO: Create ProvisionIdentityCertificateException
                        emitter.onError(new IOException("Error provisioning identity certificate"));
                    }
                });
            } catch (OcException e) {
                Timber.e(e.getLocalizedMessage());
                emitter.onError(e);
            }
        });
    }

    public Completable provisionRoleCertificate(OcSecureResource ocSecureResource,
                                                String roleId,
                                                String roleAuthority) {
        return Completable.create(emitter -> {
            try {
                ocSecureResource.provisionRoleCertificate(roleId, roleAuthority, (provisionResults, hasError) -> {
                    if (hasError == 0) {
                        emitter.onComplete();
                    } else {
                        // TODO: Create ProvisionRoleCertificateException
                        emitter.onError(new IOException("Error provisioning role certificate"));
                    }
                });
            } catch (OcException e) {
                Timber.e(e.getLocalizedMessage());
                emitter.onError(e);
            }
        });
    }

    public Single<Boolean> provisionCertChain(OcSecureResource ocSecureResource) {
        return Single.create(emitter -> {
            try {
                ocSecureResource.provisionTrustCertChain(
                        EnumSet.of(CredType.SIGNED_ASYMMETRIC_KEY),
                        mCredId,
                        (provisionResults, hasError) -> {
                            if (hasError == 0) {
                                Timber.d("Provision Certificate Chain succeeded");
                            } else {

                            }

                            emitter.onSuccess(hasError == 0);
                        });
            } catch (OcException e) {
                Timber.e(e);
            }
        });
    }

    public Completable deleteCredential(OcSecureResource ocSecureResource, int credId) {
        return Completable.create(emitter -> {
            try {
                ocSecureResource.deleteCredential(credId, (provisionResults, hasError) -> {
                    if (hasError == 0) {
                        Timber.d("Delete Credential succeeded");
                        emitter.onComplete();
                    } else {
                        // TODO: Create DeleteCredentialException
                        emitter.onError(new IOException("Delete Credential error"));
                    }
                });
            } catch (OcException e) {
                Timber.e(e);
                emitter.onError(e);
            }
        });
    }
}

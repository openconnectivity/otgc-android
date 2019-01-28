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

package org.openconnectivity.otgc.accesscontrol.data.repository;

import org.iotivity.base.OcException;
import org.iotivity.base.OcSecureResource;
import org.iotivity.base.OicSecAce;
import org.iotivity.base.OicSecAcl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Completable;
import io.reactivex.Single;
import timber.log.Timber;

@Singleton
public class AmsRepository {

    @Inject
    AmsRepository() {

    }

    public Single<OicSecAcl> getAcl(OcSecureResource ocSecureResource) {
        return Single.create(emitter -> {
            try {
                ocSecureResource.getACL((acl, hasErrors) -> {
                    if (hasErrors == 0) {
                        emitter.onSuccess(acl);
                    } else {
                        // TODO: Create GetAclException
                        emitter.onError(new IOException("Get ACL error"));
                    }
                });

            } catch (OcException e) {
                Timber.e(e);
                emitter.onError(e);
            }
        });
    }

    public Completable provisionAcl(OcSecureResource ocSecureResource, OicSecAce ace) {
        return Completable.create(emitter -> {
            try {
                List<OicSecAce> aces = new ArrayList<>();
                aces.add(ace);
                OicSecAcl acl = new OicSecAcl(null, aces);
                ocSecureResource.provisionACL(acl, (provisionResults, hasError) -> {
                    if (hasError == 0) {
                        Timber.d("Provision ACL succeeded");
                        emitter.onComplete();
                    } else {
                        // TODO: Create ProvisionAclException
                        emitter.onError(new IOException("Provision ACL error"));
                    }
                });
            } catch (OcException e) {
                Timber.e(e);
                emitter.onError(e);
            }
        });
    }

    public Completable deleteAcl(OcSecureResource ocSecureResource, int aceId) {
        return Completable.create(emitter -> {
            try {
                ocSecureResource.deleteACE(aceId, (provisionResults, hasError) -> {
                    if (hasError == 0) {
                        Timber.d("Delete ACE succeeded");
                        emitter.onComplete();
                    } else {
                        // TODO: Create DeleteACEException
                        emitter.onError(new IOException("Delete ACE error"));
                    }
                });
            } catch (OcException e) {
                Timber.e(e);
                emitter.onError(e);
            }
        });
    }
}

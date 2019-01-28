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

import org.iotivity.base.OcException;
import org.iotivity.base.OcSecureResource;
import org.iotivity.base.OxmType;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Completable;
import io.reactivex.Single;
import timber.log.Timber;

@Singleton
public class DoxsRepository {

    @Inject
    DoxsRepository() {

    }

    public Completable doOwnershipTransfer(OcSecureResource ocSecureResource) {
        return Completable.create(emitter -> {
            try {
                ocSecureResource.doOwnershipTransfer((results, hasError) -> {
                    if (hasError == 0) {
                        Timber.d("Ownership Transfer succeeded");
                        emitter.onComplete();
                    } else {
                        if (!results.isEmpty()) {
                            Timber.d("Ownership Transfer failed: %d", results.get(0).getResult());
                        }
                        // TODO: Create OwnershipTransferException and emit here
                        emitter.onError(new IOException("Ownership Transfer failed"));
                    }
                });

            } catch (OcException e) {
                emitter.onError(e);
            }
        });
    }

    public Completable resetDevice(OcSecureResource ocSecureResource, int timeout) {
        return Completable.create(emitter -> {
            try {
                ocSecureResource.resetDevice(timeout,
                        (results, hasError) -> {
                            if (hasError == 0) {
                                emitter.onComplete();
                            } else {
                                Timber.e("Error reseting device");
                                // TODO: Create OffboardException and emit here
                                emitter.onError(new IOException("Error reseting device"));
                            }
                        });
            } catch (OcException e) {
                Timber.e(e);
                emitter.onError(e);
            }
        });
    }

    public Single<List<OxmType>> retrieveOTMethods(OcSecureResource ocSecureResource) {
        return Single.fromCallable(ocSecureResource::getSupportedOTMethods);
    }

    public Completable selectOTMethod(OcSecureResource ocSecureResource, OxmType selectedMethod) {
        return Completable.create(emitter -> {
            ocSecureResource.setOTMethod(selectedMethod);
            emitter.onComplete();
        });
    }
}

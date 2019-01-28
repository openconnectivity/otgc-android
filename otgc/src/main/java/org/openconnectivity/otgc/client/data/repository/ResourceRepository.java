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

package org.openconnectivity.otgc.client.data.repository;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import timber.log.Timber;

import org.iotivity.base.ObserveType;
import org.iotivity.base.OcException;
import org.iotivity.base.OcHeaderOption;
import org.iotivity.base.OcRepresentation;
import org.iotivity.base.OcResource;
import org.iotivity.base.QualityOfService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class ResourceRepository {

    private Map<OcResource, OcResource.OnObserveListener> observeMap = new HashMap<>();
    private Map<OcResource, ObservableEmitter> emitterMap = new HashMap<>();

    @Inject
    public ResourceRepository() {}

    public Observable<OcRepresentation> observeResource(OcResource ocResource) {
        return Observable.create(emitter -> {
            // Add resource to map
            observeMap.put(ocResource, new OcResource.OnObserveListener() {
                @Override
                public void onObserveCompleted(List<OcHeaderOption> list, OcRepresentation ocRepresentation, int i) {
                    if (!emitterMap.containsKey(ocResource)) {
                        emitterMap.put(ocResource, emitter);
                    }
                    emitter.onNext(ocRepresentation);
                }

                @Override
                public void onObserveFailed(Throwable throwable) {
                    emitter.onError(throwable);
                }
            });

            try {
                ocResource.observe(ObserveType.OBSERVE, new HashMap<>(), observeMap.get(ocResource));
            } catch (OcException ex) {
                Timber.e(ex.getLocalizedMessage());
                emitter.onError(ex);
            }
        });
    }

    public Completable cancelObserve(String resourceUri) {
        return Completable.create(emitter -> {
            // Search resource in map
            OcResource res = null;
            for (OcResource ocResource : observeMap.keySet()) {
                if (ocResource.getUri().equals(resourceUri)) {
                    res = ocResource;
                    break;
                }
            }

            try {
                res.cancelObserve(QualityOfService.HIGH);
            } catch (OcException ex) {
                Timber.e(ex.getLocalizedMessage());
                emitter.onError(ex);
            }

            // Delete callback from map
            observeMap.remove(res);
            ObservableEmitter observableEmitter = emitterMap.get(res);
            emitterMap.remove(res);
            observableEmitter.onComplete();

            emitter.onComplete();
        });
    }
}



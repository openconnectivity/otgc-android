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

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;

import org.iotivity.OCClientResponse;
import org.iotivity.OCEndpoint;
import org.iotivity.OCEndpointUtil;
import org.iotivity.OCMain;
import org.iotivity.OCQos;
import org.iotivity.OCResponseHandler;
import org.iotivity.OCStatus;
import org.openconnectivity.otgc.domain.model.client.SerializableResource;

import javax.inject.Inject;
import javax.inject.Singleton;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class ResourceRepository {

    private Map<String, OCEndpoint> observeMap = new HashMap<>();
    private Map<String, ObservableEmitter> emitterMap = new HashMap<>();

    @Inject
    public ResourceRepository() {}

    public Observable<SerializableResource> observeResource(String endpoint, SerializableResource resource) {
        return Observable.create(emitter -> {
            OCEndpoint ep = OCEndpointUtil.newEndpoint();
            OCEndpointUtil.stringToEndpoint(endpoint, ep, new String[1]);

            OCResponseHandler handler = (OCClientResponse response) -> {
                OCStatus code = response.getCode();
                if (code == OCStatus.OC_STATUS_OK
                        || code == OCStatus.OC_STATUS_CHANGED) {
                    resource.setProperties(response.getPayload());
                    if (!emitterMap.containsKey(resource)) {
                        emitterMap.put(resource.getUri(), emitter);
                    }
                    emitter.onNext(resource);
                } else {
                    emitter.onError(new Exception("Observe resource " + resource.getUri() + " error - code: " + code));
                }
            };
            // Add resource to map
            observeMap.put(resource.getUri(), ep);

            if (!OCMain.doObserve(resource.getUri(), ep, null, handler, OCQos.HIGH_QOS)) {
                emitter.onError(new Exception("Observe resource " + resource.getUri() + " error"));
            }
        });
    }

    public Completable cancelObserve(String resourceUri) {
        return Completable.create(emitter -> {
            OCEndpoint ep = observeMap.get(resourceUri);
            if (ep != null) {
                if (!OCMain.stopObserve(resourceUri, ep)) {
                    emitter.onError(new Exception("Stop observe resource " + resourceUri + " error"));
                }

                // Delete callback from map
                OCEndpointUtil.freeEndpoint(ep);
                observeMap.remove(resourceUri);
                ObservableEmitter observableEmitter = emitterMap.get(resourceUri);
                observableEmitter.onComplete();
                emitterMap.remove(resourceUri);
            }
            emitter.onComplete();
        });
    }

    public Completable cancelAllObserve() {
        return Completable.create(emitter -> {
            for (String uri : observeMap.keySet()) {
                cancelObserve(uri).blockingGet();
            }

            emitter.onComplete();
        });
    }
}



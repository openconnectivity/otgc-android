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

package org.openconnectivity.otgc.client.domain.usecase;

import org.iotivity.base.OcResource;
import org.openconnectivity.otgc.client.domain.model.SerializableResource;
import org.openconnectivity.otgc.common.data.repository.IotivityRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;

public class GetResourcesUseCase {
    private final IotivityRepository iotivityRepository;

    @Inject
    public GetResourcesUseCase(IotivityRepository iotivityRepository) {
        this.iotivityRepository = iotivityRepository;
    }

    public Single<List<SerializableResource>> execute(String deviceId) {
        return iotivityRepository.getDeviceCoapIpv6Host(deviceId)
                .flatMap(iotivityRepository::getResourceTypes)
                .map(ocResources -> {
                    List<SerializableResource> serializableResources = new ArrayList<>();
                    for (OcResource ocResource : ocResources) {
                        serializableResources.add(new SerializableResource(ocResource));
                    }

                    Collections.sort(serializableResources,
                            (r1, r2) -> r1.getUri().compareTo(r2.getUri()));

                    return serializableResources;
                });
    }
}

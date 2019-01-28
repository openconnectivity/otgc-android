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

import org.iotivity.base.OcRepresentation;
import org.openconnectivity.otgc.common.data.repository.IotivityRepository;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;

public class PostRequestUseCase {
    private final IotivityRepository mIotivityRepository;

    @Inject
    public PostRequestUseCase(IotivityRepository iotivityRepository) {
        this.mIotivityRepository = iotivityRepository;
    }

    public Single<OcRepresentation> execute(String deviceId, String uri, List<String> resourceTypes, List<String> interfacesList,
                                            OcRepresentation rep) {
        return mIotivityRepository.getDeviceCoapsIpv6Host(deviceId)
                .flatMap(host ->
                    mIotivityRepository.constructResource(
                            host,
                            uri,
                            resourceTypes,
                            interfacesList))
                .flatMap(ocResource -> mIotivityRepository.post(ocResource, true, rep));
    }
}

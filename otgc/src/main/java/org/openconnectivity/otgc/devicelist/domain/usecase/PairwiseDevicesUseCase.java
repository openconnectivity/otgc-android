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

package org.openconnectivity.otgc.devicelist.domain.usecase;

import org.iotivity.base.OcSecureResource;
import org.openconnectivity.otgc.client.data.repository.ResourceRepository;
import org.openconnectivity.otgc.common.data.repository.IotivityRepository;
import org.openconnectivity.otgc.devicelist.data.repository.DoxsRepository;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Single;

public class PairwiseDevicesUseCase {
    private final IotivityRepository mIotivityRepository;
    private final DoxsRepository mDoxsRepository;
    private final ResourceRepository mResourceRepository;

    @Inject
    PairwiseDevicesUseCase(IotivityRepository iotivityRepository,
                           DoxsRepository doxsRepository,
                           ResourceRepository resourceRepository) {
        this.mIotivityRepository = iotivityRepository;
        this.mDoxsRepository = doxsRepository;
        this.mResourceRepository = resourceRepository;
    }

    public Completable execute(String serverId, String clientId) {
        Single<OcSecureResource> clientSecureResource = mIotivityRepository.findOcSecureResource(clientId);
        Single<OcSecureResource> serverSecureResource = mIotivityRepository.findOcSecureResource(serverId);

        return mIotivityRepository.getDeviceCoapIpv6Host(serverId)
                .flatMap(mIotivityRepository::findResources)
                .map(mResourceRepository::getVerticalResources)
                .flatMapCompletable(resources -> Single.concat(clientSecureResource, serverSecureResource).toList()
                        .flatMapCompletable(ocSecureResources -> mDoxsRepository.pairwiseDevices(ocSecureResources.get(0), ocSecureResources.get(1), resources)));

    }
}

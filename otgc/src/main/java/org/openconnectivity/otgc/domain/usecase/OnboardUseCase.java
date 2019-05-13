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
package org.openconnectivity.otgc.domain.usecase;

import org.openconnectivity.otgc.data.repository.DoxsRepository;
import org.openconnectivity.otgc.domain.model.devicelist.Device;
import org.openconnectivity.otgc.data.repository.IotivityRepository;
import org.openconnectivity.otgc.utils.constant.OcfOxmType;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Single;

public class OnboardUseCase {
    private final IotivityRepository iotivityRepository;
    private final DoxsRepository doxsRepository;

    @Inject
    public OnboardUseCase(IotivityRepository iotivityRepository,
                          DoxsRepository doxsRepository) {
        this.iotivityRepository = iotivityRepository;
        this.doxsRepository = doxsRepository;
    }

    public Single<Device> execute(Device deviceToOnboard, OcfOxmType oxm) {
        final Single<Device> getUpdatedOcSecureResource = iotivityRepository.scanOwnedDevices()
                .filter(device -> deviceToOnboard.getDeviceId().equals(device.getDeviceId())
                        || deviceToOnboard.equalsHosts(device))
                .singleOrError();

        return doxsRepository.doOwnershipTransfer(deviceToOnboard.getDeviceId(), oxm)
                .delay(1, TimeUnit.SECONDS)
                .andThen(getUpdatedOcSecureResource)
                .onErrorResumeNext(error -> getUpdatedOcSecureResource
                        .retry(2)
                        .onErrorResumeNext(Single.error(error)));
    }
}

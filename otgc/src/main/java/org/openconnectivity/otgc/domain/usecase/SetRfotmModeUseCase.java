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

import org.openconnectivity.otgc.data.repository.IotivityRepository;
import org.openconnectivity.otgc.data.repository.ProvisioningRepository;
import org.openconnectivity.otgc.data.repository.DoxsRepository;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Completable;

public class SetRfotmModeUseCase {
    private final IotivityRepository iotivityRepository;
    private final ProvisioningRepository provisionRepository;
    private final DoxsRepository doxsRepository;

    @Inject
    public SetRfotmModeUseCase(IotivityRepository iotivityRepository,
                               ProvisioningRepository provisionRepository,
                               DoxsRepository doxsRepository) {
        this.iotivityRepository = iotivityRepository;
        this.provisionRepository = provisionRepository;
        this.doxsRepository = doxsRepository;
    }

    public Completable execute() {
        return iotivityRepository.scanOwnedDevices()
                .flatMapCompletable(device -> doxsRepository.resetDevice(device.getDeviceId()))
                .delay(500, TimeUnit.MILLISECONDS)
                .andThen(provisionRepository.resetSvrDb());
    }
}

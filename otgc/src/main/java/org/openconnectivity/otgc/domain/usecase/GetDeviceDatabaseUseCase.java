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
import org.openconnectivity.otgc.domain.model.devicelist.Device;

import javax.inject.Inject;

import io.reactivex.Single;

public class GetDeviceDatabaseUseCase {

    private final IotivityRepository iotivityRepository;

    @Inject
    public GetDeviceDatabaseUseCase(IotivityRepository iotivityRepository) {
        this.iotivityRepository = iotivityRepository;
    }

    public Single<Device> execute(Device device) {
        return iotivityRepository.getDeviceFromDatabase(device.getDeviceId())
                .flatMapSingle(deviceEntity -> Single.fromCallable(() ->
                        new Device(deviceEntity.getType(),
                                    device.getDeviceId(),
                                    device.getDeviceInfo(),
                                    deviceEntity.getHosts(),
                                    device.getPermits()))
                );
    }
}

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
import org.openconnectivity.otgc.common.data.repository.PreferencesRepository;
import org.openconnectivity.otgc.devicelist.data.repository.DoxsRepository;
import org.openconnectivity.otgc.devicelist.domain.model.Device;
import org.openconnectivity.otgc.devicelist.domain.model.DeviceType;
import org.openconnectivity.otgc.common.domain.model.OcDevice;
import org.openconnectivity.otgc.common.data.repository.IotivityRepository;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Single;

public class OffboardUseCase {
    private final PreferencesRepository mPreferencesRepository;
    private final DoxsRepository mDoxsRepository;
    private final IotivityRepository iotivityRepository;

    @Inject
    OffboardUseCase(PreferencesRepository preferencesRepository,
                                  DoxsRepository doxsRepository,
                                  IotivityRepository iotivityRepository) {
        this.mPreferencesRepository = preferencesRepository;
        this.mDoxsRepository = doxsRepository;
        this.iotivityRepository = iotivityRepository;
    }

    public Single<Device> execute(OcSecureResource deviceToOffboard) {
        final Single<OcSecureResource> getUpdatedOcSecureResource =
                iotivityRepository.scanUnownedDevices()
                        .filter(ocSecureResource ->
                                (ocSecureResource.getDeviceID().equals(deviceToOffboard.getDeviceID())
                                || ocSecureResource.getIpAddr().equals(deviceToOffboard.getIpAddr())))
                        .singleOrError();

        return mDoxsRepository.resetDevice(deviceToOffboard,
                    mPreferencesRepository.getDiscoveryTimeout())
                .delay(1, TimeUnit.SECONDS)
                .andThen(getUpdatedOcSecureResource)
                .onErrorResumeNext(error -> getUpdatedOcSecureResource
                        .retry(2)
                        .onErrorResumeNext(Single.error(error)))
                .map(ocSecureResource ->
                    new Device(DeviceType.UNOWNED,
                            ocSecureResource.getDeviceID(),
                            new OcDevice(),
                            ocSecureResource)
                );
    }
}

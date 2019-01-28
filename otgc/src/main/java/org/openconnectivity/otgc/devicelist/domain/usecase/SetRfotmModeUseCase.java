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

import org.openconnectivity.otgc.common.data.repository.IORepository;
import org.openconnectivity.otgc.common.data.repository.PreferencesRepository;
import org.openconnectivity.otgc.common.data.repository.IotivityRepository;
import org.openconnectivity.otgc.common.data.repository.ProvisioningRepository;
import org.openconnectivity.otgc.devicelist.data.repository.DoxsRepository;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Completable;

public class SetRfotmModeUseCase {
    private static final String CRT_FILE = "root.crt";
    private static final String PRIVATE_KEY_FILE = "root.prv";

    private final IORepository mIORepository;
    private final PreferencesRepository mPreferencesRepository;
    private final ProvisioningRepository mProvisioningRepository;
    private final DoxsRepository mDoxsRepository;
    private final IotivityRepository iotivityRepository;

    @Inject
    SetRfotmModeUseCase(IORepository ioRepository,
                        PreferencesRepository preferencesRepository,
                        ProvisioningRepository provisioningRepository,
                        DoxsRepository doxsRepository,
                        IotivityRepository iotivityRepository) {
        this.mIORepository = ioRepository;
        this.mPreferencesRepository = preferencesRepository;
        this.mProvisioningRepository = provisioningRepository;
        this.mDoxsRepository = doxsRepository;
        this.iotivityRepository = iotivityRepository;
    }

    public Completable execute() {
        return iotivityRepository.scanOwnedDevices()
                .flatMapCompletable(ocSecureResource ->
                        mDoxsRepository.resetDevice(
                                ocSecureResource,
                                mPreferencesRepository.getDiscoveryTimeout()))
                .delay(500, TimeUnit.MILLISECONDS)
                .andThen(mProvisioningRepository.resetSvrDb())
                .andThen(mProvisioningRepository.saveCertificates(
                        mIORepository.getBytesFromFile(CRT_FILE).blockingGet(),
                        mIORepository.getBytesFromFile(PRIVATE_KEY_FILE).blockingGet()
                ));
    }
}

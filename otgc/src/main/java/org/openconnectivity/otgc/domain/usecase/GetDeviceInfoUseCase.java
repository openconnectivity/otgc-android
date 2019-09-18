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
import org.openconnectivity.otgc.data.repository.PreferencesRepository;
import org.openconnectivity.otgc.domain.model.devicelist.Device;
import org.openconnectivity.otgc.domain.model.resource.virtual.d.OcDeviceInfo;
import org.openconnectivity.otgc.utils.rx.SchedulersFacade;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Single;

public class GetDeviceInfoUseCase {
    /* Repositories */
    private final IotivityRepository iotivityRepository;
    private final PreferencesRepository preferencesRepository;
    /* Scheduler */
    private final SchedulersFacade schedulersFacade;

    @Inject
    public GetDeviceInfoUseCase(IotivityRepository iotivityRepository,
                                PreferencesRepository preferencesRepository,
                                SchedulersFacade schedulersFacade) {
        this.iotivityRepository = iotivityRepository;
        this.preferencesRepository = preferencesRepository;

        this.schedulersFacade = schedulersFacade;
    }

    public Single<OcDeviceInfo> execute(Device device) {
        return iotivityRepository.getNonSecureEndpoint(device)
                .flatMap(iotivityRepository::getDeviceInfo)
                .delay(preferencesRepository.getRequestsDelay(), TimeUnit.SECONDS, schedulersFacade.ui());
    }
}

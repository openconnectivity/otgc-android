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

package org.openconnectivity.otgc.domain.usecase.accesscontrol;

import org.openconnectivity.otgc.data.repository.AmsRepository;
import org.openconnectivity.otgc.data.repository.IotivityRepository;
import org.openconnectivity.otgc.data.repository.PstatRepository;
import org.openconnectivity.otgc.domain.model.devicelist.Device;
import org.openconnectivity.otgc.utils.constant.OcfDosType;

import javax.inject.Inject;

import io.reactivex.Completable;

public class DeleteAclUseCase {
    private final IotivityRepository iotivityRepository;
    private final AmsRepository amsRepository;
    private final PstatRepository pstatRepository;

    @Inject
    DeleteAclUseCase(IotivityRepository iotivityRepository,
                     AmsRepository amsRepository,
                     PstatRepository pstatRepository) {
        this.iotivityRepository = iotivityRepository;
        this.amsRepository = amsRepository;
        this.pstatRepository = pstatRepository;
    }

    public Completable execute(Device device, long aceId) {
        return iotivityRepository.getSecureEndpoint(device)
                .flatMapCompletable(endpoint ->
                        pstatRepository.changeDeviceStatus(endpoint, device.getDeviceId(), OcfDosType.OC_DOSTYPE_RFPRO)
                                .andThen(amsRepository.deleteAcl(endpoint, device.getDeviceId(), aceId))
                                .andThen(pstatRepository.changeDeviceStatus(endpoint, device.getDeviceId(), OcfDosType.OC_DOSTYPE_RFNOP)));
    }
}

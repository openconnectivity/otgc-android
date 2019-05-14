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

package org.openconnectivity.otgc.domain.usecase.link;

import org.openconnectivity.otgc.data.repository.IotivityRepository;
import org.openconnectivity.otgc.data.repository.CmsRepository;
import org.openconnectivity.otgc.data.repository.PstatRepository;
import org.openconnectivity.otgc.domain.model.devicelist.Device;
import org.openconnectivity.otgc.domain.model.resource.secure.cred.OcCredential;
import org.openconnectivity.otgc.utils.constant.OcfDosType;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;

public class UnlinkRoleForClientUseCase {
    private final IotivityRepository iotivityRepository;
    private final PstatRepository pstatRepository;
    private final CmsRepository cmsRepository;

    @Inject
    public UnlinkRoleForClientUseCase(IotivityRepository iotivityRepository,
                                      PstatRepository pstatRepository,
                                      CmsRepository cmsRepository)
    {
        this.iotivityRepository = iotivityRepository;
        this.pstatRepository = pstatRepository;
        this.cmsRepository = cmsRepository;
    }

    public Completable execute(Device device, String roleId) {
        return iotivityRepository.getSecureEndpoint(device)
                .flatMapCompletable(endpoint -> cmsRepository.getCredentials(endpoint, device.getDeviceId())
                        .flatMapCompletable(ocCredentials -> {
                            List<Completable> deleteCredList = new ArrayList<>();
                            for(OcCredential cred : ocCredentials.getCredList()) {
                                if (cred.getRoleid() != null && cred.getRoleid().getRole().equals(roleId)) {
                                    Completable deleteCred = pstatRepository.changeDeviceStatus(endpoint, device.getDeviceId(), OcfDosType.OC_DOSTYPE_RFPRO)
                                            .andThen(cmsRepository.deleteCredential(endpoint, device.getDeviceId(), cred.getCredid()))
                                            .andThen(pstatRepository.changeDeviceStatus(endpoint, device.getDeviceId(), OcfDosType.OC_DOSTYPE_RFNOP));
                                    deleteCredList.add(deleteCred);
                                }
                            }
                            return Completable.merge(deleteCredList);
                        }));
    }
}

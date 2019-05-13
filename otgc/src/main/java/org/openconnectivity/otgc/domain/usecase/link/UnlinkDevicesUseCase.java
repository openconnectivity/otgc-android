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

import org.openconnectivity.otgc.data.repository.CmsRepository;
import org.openconnectivity.otgc.data.repository.IotivityRepository;
import org.openconnectivity.otgc.data.repository.PstatRepository;
import org.openconnectivity.otgc.domain.model.devicelist.Device;
import org.openconnectivity.otgc.domain.model.resource.secure.cred.OcCredential;
import org.openconnectivity.otgc.utils.constant.OcfCredType;
import org.openconnectivity.otgc.utils.constant.OcfDosType;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;

public class UnlinkDevicesUseCase {
    private final IotivityRepository iotivityRepository;
    private final PstatRepository pstatRepository;
    private final CmsRepository cmsRepository;

    @Inject
    public UnlinkDevicesUseCase(IotivityRepository iotivityRepository,
                                PstatRepository pstatRepository,
                                CmsRepository cmsRepository)
    {
        this.iotivityRepository = iotivityRepository;
        this.pstatRepository = pstatRepository;
        this.cmsRepository = cmsRepository;
    }

    public Completable execute(Device client, Device server)
    {
        Completable deleteClientPairwise = iotivityRepository.getSecureEndpoint(client)
                .flatMapCompletable(endpoint -> cmsRepository.getCredentials(endpoint)
                        .flatMapCompletable(ocCredentials -> {
                            List<Completable> deleteCredList = new ArrayList<>();
                            for(OcCredential cred : ocCredentials.getCredList()) {
                                if (cred.getSubjectuuid() != null && cred.getSubjectuuid().equals(server.getDeviceId())
                                        && cred.getCredtype() == OcfCredType.OC_CREDTYPE_PSK) {
                                    Completable deleteCred = pstatRepository.changeDeviceStatus(endpoint, OcfDosType.OC_DOSTYPE_RFPRO)
                                            .andThen(cmsRepository.deleteCredential(endpoint, cred.getCredid()))
                                            .andThen(pstatRepository.changeDeviceStatus(endpoint, OcfDosType.OC_DOSTYPE_RFNOP));
                                    deleteCredList.add(deleteCred);
                                }
                            }
                            return Completable.merge(deleteCredList);
                        }));

        Completable deleteServerPairwise = iotivityRepository.getSecureEndpoint(server)
                .flatMapCompletable(endpoint -> cmsRepository.getCredentials(endpoint)
                        .flatMapCompletable(ocCredentials -> {
                            List<Completable> deleteCredList = new ArrayList<>();
                            for(OcCredential cred : ocCredentials.getCredList()) {
                                if (cred.getSubjectuuid() != null && cred.getSubjectuuid().equals(client.getDeviceId())
                                        && cred.getCredtype() == OcfCredType.OC_CREDTYPE_PSK) {
                                    Completable deleteCred = pstatRepository.changeDeviceStatus(endpoint, OcfDosType.OC_DOSTYPE_RFPRO)
                                            .andThen(cmsRepository.deleteCredential(endpoint, cred.getCredid()))
                                            .andThen(pstatRepository.changeDeviceStatus(endpoint, OcfDosType.OC_DOSTYPE_RFNOP));
                                    deleteCredList.add(deleteCred);
                                }
                            }
                            return Completable.merge(deleteCredList);
                        }));

        return deleteClientPairwise
                .andThen(deleteServerPairwise);
    }
}

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

import org.iotivity.OCCredType;
import org.openconnectivity.otgc.data.repository.CmsRepository;
import org.openconnectivity.otgc.domain.model.devicelist.Device;
import org.openconnectivity.otgc.domain.model.resource.secure.cred.OcCredential;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;

public class UnlinkDevicesUseCase {
    private final CmsRepository cmsRepository;

    @Inject
    public UnlinkDevicesUseCase(CmsRepository cmsRepository)
    {
        this.cmsRepository = cmsRepository;
    }

    public Completable execute(Device client, Device server)
    {
        Completable deleteClientPairwise = cmsRepository.getCredentials(client.getDeviceId())
                .flatMapCompletable(ocCredentials -> {
                    List<Completable> deleteCredList = new ArrayList<>();
                    for(OcCredential cred : ocCredentials.getCredList()) {
                        if (cred.getSubjectuuid() != null && cred.getSubjectuuid().equals(server.getDeviceId())
                                && OCCredType.valueOf(cred.getCredtype()) == OCCredType.OC_CREDTYPE_PSK) {
                            Completable deleteCred = cmsRepository.deleteCredential(client.getDeviceId(), cred.getCredid());
                            deleteCredList.add(deleteCred);
                        }
                    }
                    return Completable.merge(deleteCredList);
                });

        Completable deleteServerPairwise = cmsRepository.getCredentials(server.getDeviceId())
                .flatMapCompletable(ocCredentials -> {
                    List<Completable> deleteCredList = new ArrayList<>();
                    for(OcCredential cred : ocCredentials.getCredList()) {
                        if (cred.getSubjectuuid() != null && cred.getSubjectuuid().equals(client.getDeviceId())
                                && OCCredType.valueOf(cred.getCredtype()) == OCCredType.OC_CREDTYPE_PSK) {
                            Completable deleteCred = cmsRepository.deleteCredential(server.getDeviceId(), cred.getCredid());
                            deleteCredList.add(deleteCred);
                        }
                    }
                    return Completable.merge(deleteCredList);
                });

        return deleteClientPairwise
                .andThen(deleteServerPairwise);
    }
}

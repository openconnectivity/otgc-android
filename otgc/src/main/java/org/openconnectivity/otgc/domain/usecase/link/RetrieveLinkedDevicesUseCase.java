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
import org.openconnectivity.otgc.domain.model.devicelist.Device;
import org.openconnectivity.otgc.domain.model.resource.secure.cred.OcCredential;
import org.openconnectivity.otgc.utils.constant.OcfCredType;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;

public class RetrieveLinkedDevicesUseCase {
    private final IotivityRepository iotivityRepository;
    private final CmsRepository cmsRepository;

    @Inject
    public RetrieveLinkedDevicesUseCase(IotivityRepository iotivityRepository,
                                        CmsRepository cmsRepository)
    {
        this.iotivityRepository = iotivityRepository;
        this.cmsRepository = cmsRepository;
    }

    public Single<List<String>> execute(Device device)
    {
        return iotivityRepository.getSecureEndpoint(device)
                .flatMap(endpoint -> cmsRepository.getCredentials(endpoint, device.getDeviceId()))
                .map(ocCredentials -> {
                    List<String> creds = new ArrayList<>();
                    for (OcCredential cred : ocCredentials.getCredList()) {
                        if (cred.getSubjectuuid() != null
                                && cred.getCredtype() == OcfCredType.OC_CREDTYPE_PSK) {
                            creds.add(cred.getSubjectuuid());
                        }
                    }

                    return creds;
                });
    }
}

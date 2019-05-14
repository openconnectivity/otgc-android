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

import org.openconnectivity.otgc.data.repository.AmsRepository;
import org.openconnectivity.otgc.data.repository.PstatRepository;
import org.openconnectivity.otgc.data.repository.IotivityRepository;
import org.openconnectivity.otgc.domain.model.devicelist.Device;
import org.openconnectivity.otgc.domain.model.resource.virtual.res.OcResource;
import org.openconnectivity.otgc.utils.constant.OcfDosType;
import org.openconnectivity.otgc.utils.constant.OcfResourceType;
import org.openconnectivity.otgc.utils.constant.OcfResourceUri;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;

public class LinkRolesForServerUseCase {
    private final IotivityRepository iotivityRepository;
    private final PstatRepository pstatRepository;
    private final AmsRepository amsRepository;

    @Inject
    public LinkRolesForServerUseCase(IotivityRepository iotivityRepository,
                                    PstatRepository pstatRepository,
                                    AmsRepository amsRepository)
    {
        this.iotivityRepository = iotivityRepository;
        this.pstatRepository = pstatRepository;
        this.amsRepository = amsRepository;
    }

    public Completable execute(Device device, String roleId, String roleAuthority) {
        return iotivityRepository.getSecureEndpoint(device)
                .flatMapCompletable(endpoint -> iotivityRepository.findResources(endpoint)
                        .map(ocRes -> {
                            List<String> resources = new ArrayList<>();
                            for (OcResource resource : ocRes.getResourceList()) {
                                for (String resourceType : resource.getResourceTypes()) {
                                    if (OcfResourceType.isVerticalResourceType(resourceType)
                                            && !OcfResourceUri.DEVICE_INFO_URI.equals(resource.getHref())) {
                                        resources.add(resource.getHref());
                                    }
                                }
                            }
                            return resources;
                        })
                        .flatMapCompletable(resources ->
                                pstatRepository.changeDeviceStatus(endpoint, device.getDeviceId(), OcfDosType.OC_DOSTYPE_RFPRO)
                                        .andThen(amsRepository.provisionRoleAcl(endpoint, device.getDeviceId(), roleId, roleAuthority, resources, 31))
                                        .andThen(pstatRepository.changeDeviceStatus(endpoint, device.getDeviceId(), OcfDosType.OC_DOSTYPE_RFNOP))));
    }
}

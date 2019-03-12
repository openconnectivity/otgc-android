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

import org.iotivity.base.OcResource;
import org.openconnectivity.otgc.common.constant.OcfResourceType;
import org.openconnectivity.otgc.common.data.repository.IotivityRepository;
import org.openconnectivity.otgc.devicelist.domain.model.Role;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Single;

public class GetDeviceRoleUseCase {
    private final IotivityRepository mIotivityRepository;

    @Inject
    GetDeviceRoleUseCase(IotivityRepository iotivityRepository) {
        this.mIotivityRepository = iotivityRepository;
    }

    public Single<Role> execute(String deviceId) {
        return mIotivityRepository.getDeviceCoapIpv6Host(deviceId)
                .flatMap(mIotivityRepository::findResources)
                .timeout(mIotivityRepository.getDiscoveryTimeout() + 5_00L, TimeUnit.SECONDS)
                .map(ocResources -> {
                    Role deviceRole = Role.CLIENT;
                    for (OcResource resource : ocResources) {
                        for (String resourceType : resource.getResourceTypes()) {
                            if (OcfResourceType.isVerticalResourceType(resourceType)) {
                                deviceRole = Role.SERVER;
                                break;
                            }
                        }

                        if (deviceRole.equals(Role.SERVER)) {
                            break;
                        }
                    }

                    return deviceRole;
                });
    }
}

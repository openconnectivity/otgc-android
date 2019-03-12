/*
 * *****************************************************************
 *
 *  Copyright 2019 DEKRA Testing and Certification, S.A.U. All Rights Reserved.
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

package org.openconnectivity.otgc.linkedroles.domain.usecase;

import org.iotivity.base.AceSubjectRole;
import org.iotivity.base.AceSubjectType;
import org.iotivity.base.OicSecAce;
import org.iotivity.base.OicSecAceSubject;
import org.openconnectivity.otgc.accesscontrol.data.repository.AmsRepository;
import org.openconnectivity.otgc.client.data.repository.ResourceRepository;
import org.openconnectivity.otgc.common.data.repository.IotivityRepository;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.Completable;

public class LinkRolesForServerUseCase {
    private final IotivityRepository mIotivityRepository;
    private final AmsRepository mAmsRepository;
    private final ResourceRepository mResourceRepository;

    @Inject
    public LinkRolesForServerUseCase(IotivityRepository iotivityRepository,
                                     AmsRepository amsRepository,
                                     ResourceRepository resourceRepository) {
        this.mIotivityRepository = iotivityRepository;
        this.mAmsRepository = amsRepository;
        this.mResourceRepository = resourceRepository;
    }

    public Completable execute(String deviceId, String roleId, String roleAuthority) {
        return mIotivityRepository.getDeviceCoapIpv6Host(deviceId)
                .flatMap(mIotivityRepository::findResources)
                .map(mResourceRepository::getVerticalResources)
                .flatMapCompletable(resources -> mIotivityRepository.findOcSecureResource(deviceId)
                    .flatMapCompletable(ocSecureResource -> {
                        AceSubjectRole role = new AceSubjectRole(roleId, roleAuthority);
                        OicSecAceSubject subject = new OicSecAceSubject(AceSubjectType.SUBJECT_ROLE.getValue(), null, role, null);

                        OicSecAce ace = new OicSecAce(0, subject, 31, resources, new ArrayList<>());
                        return mAmsRepository.provisionAcl(ocSecureResource, ace);
                    })
                );
    }
}

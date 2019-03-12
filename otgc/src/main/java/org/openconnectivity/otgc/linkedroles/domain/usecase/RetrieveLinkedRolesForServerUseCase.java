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

import org.iotivity.base.OicSecAce;
import org.openconnectivity.otgc.accesscontrol.data.repository.AmsRepository;
import org.openconnectivity.otgc.common.data.repository.IotivityRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;

public class RetrieveLinkedRolesForServerUseCase {
    private final IotivityRepository mIotivityRepository;
    private final AmsRepository mAmsRepository;

    @Inject
    public RetrieveLinkedRolesForServerUseCase(IotivityRepository iotivityRepository,
                                               AmsRepository amsRepository) {
        this.mIotivityRepository = iotivityRepository;
        this.mAmsRepository = amsRepository;
    }

    public Single<List<String>> execute(String deviceId) {
        return mIotivityRepository.findOcSecureResource(deviceId)
                .flatMap(mAmsRepository::getAcl)
                .map(acl -> {
                    List<String> roles = new ArrayList<>();

                    for(OicSecAce ace : acl.getOicSecAces()) {
                        if (ace.getSubject().getRole() != null) {
                            roles.add(ace.getSubject().getRole().getId());
                        }
                    }

                    return roles;
                });
    }
}

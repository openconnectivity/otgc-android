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

import org.iotivity.base.OicSecCred;
import org.openconnectivity.otgc.common.data.repository.IotivityRepository;
import org.openconnectivity.otgc.credential.data.repository.CmsRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;

public class RetrieveLinkedRolesForClientUseCase {
    private final IotivityRepository mIotivityRepository;
    private final CmsRepository mCmsRepository;

    @Inject
    public RetrieveLinkedRolesForClientUseCase(IotivityRepository iotivityRepository,
                                      CmsRepository cmsRepository) {
        this.mIotivityRepository = iotivityRepository;
        this.mCmsRepository = cmsRepository;
    }

    public Single<List<String>> execute(String deviceId) {
        return mIotivityRepository.findOcSecureResource(deviceId)
                .flatMap(mCmsRepository::getCredentials)
                .map(credentials -> {
                    List<String> roles = new ArrayList<>();

                    for (OicSecCred cred : credentials.getOicSecCreds()) {
                        if (cred.getRole() != null) {
                            roles.add(cred.getRole().getId());
                        }
                    }

                    return roles;
                });
    }
}

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

package org.openconnectivity.otgc.accesscontrol.domain.usecase;

import org.iotivity.base.AceSubjectRole;
import org.iotivity.base.AceSubjectType;
import org.iotivity.base.OicSecAce;
import org.iotivity.base.OicSecAceSubject;
import org.iotivity.base.OicSecResr;
import org.openconnectivity.otgc.accesscontrol.data.repository.AmsRepository;
import org.openconnectivity.otgc.common.data.repository.IotivityRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;

public class CreateAclUseCase {
    private final AmsRepository mAmsRepository;
    private final IotivityRepository mIotivityRepository;

    @Inject
    CreateAclUseCase(AmsRepository amsRepository,
                     IotivityRepository iotivityRepository) {
        this.mAmsRepository = amsRepository;
        this.mIotivityRepository = iotivityRepository;
    }

    public Completable execute(String targetDeviceId, String subjectId, int permission, List<String> resources) {
        return mIotivityRepository.findOcSecureResource(targetDeviceId)
                .flatMapCompletable(ocSecureResource -> {
                    OicSecAceSubject subject = new OicSecAceSubject(AceSubjectType.SUBJECT_UUID.getValue(), subjectId, null, null);

                    OicSecAce ace = new OicSecAce(0, subject, permission, getResources(resources), new ArrayList<>());
                    return mAmsRepository.provisionAcl(ocSecureResource, ace);
                });
    }

    public Completable execute(String targetDeviceId, String roleId, String roleAuthority, int permission, List<String> resources) {
        return mIotivityRepository.findOcSecureResource(targetDeviceId)
                .flatMapCompletable(ocSecureResource -> {
                    AceSubjectRole role = new AceSubjectRole(roleId, roleAuthority);
                    OicSecAceSubject subject = new OicSecAceSubject(AceSubjectType.SUBJECT_ROLE.getValue(), null, role, null);

                    OicSecAce ace = new OicSecAce(0, subject, permission, getResources(resources), new ArrayList<>());
                    return mAmsRepository.provisionAcl(ocSecureResource, ace);
                });
    }

    public Completable execute(String targetDeviceId, boolean isAuthCrypt, int permission, List<String> resources) {
        return mIotivityRepository.findOcSecureResource(targetDeviceId)
                .flatMapCompletable(ocSecureResource -> {
                    OicSecAceSubject subject =
                            new OicSecAceSubject(
                                    AceSubjectType.SUBJECT_CONNTYPE.getValue(),
                                    null,
                                    null,
                                    isAuthCrypt ? "auth-crypt" : "anon-clear");


                    OicSecAce ace = new OicSecAce(0, subject, permission, getResources(resources), new ArrayList<>());
                    return mAmsRepository.provisionAcl(ocSecureResource, ace);
                });
    }

    private List<OicSecResr> getResources(List<String> stringResources) {
        List<OicSecResr> resources = new ArrayList<>();
        for (String stringResource : stringResources) {
            OicSecResr res = new OicSecResr();
            res.setHref(stringResource);
            List<String> types = new ArrayList<>();
            types.add("*");
            res.setTypes(types);
            res.setTypeLen(types.size());
            List<String> interfaces = new ArrayList<>();
            interfaces.add("*");
            res.setInterfaces(interfaces);
            res.setInterfaceLen(interfaces.size());
            resources.add(res);
        }

        return resources;
    }
}

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

package org.openconnectivity.otgc.domain.usecase.credential;

import org.openconnectivity.otgc.data.repository.IotivityRepository;
import org.openconnectivity.otgc.data.repository.CmsRepository;
import org.openconnectivity.otgc.data.repository.PstatRepository;
import org.openconnectivity.otgc.domain.model.devicelist.Device;
import org.openconnectivity.otgc.utils.constant.OcfDosType;

import javax.inject.Inject;

import io.reactivex.Completable;

public class DeleteCredentialUseCase {
    private final IotivityRepository iotivityRepository;
    private final CmsRepository mCmsRepository;
    private final PstatRepository mPstatRepository;

    @Inject
    public DeleteCredentialUseCase(IotivityRepository iotivityRepository,
                                   CmsRepository cmsRepository,
                                   PstatRepository pstatRepository) {
        this.iotivityRepository = iotivityRepository;
        this.mCmsRepository = cmsRepository;
        this.mPstatRepository = pstatRepository;
    }

    /**
     * Use case steps:
     * 1.   POST    /oic/sec/pstat  -> dos.s=2
     * 2.   DELETE  /oic/sec/cred   -> ?credid=x
     * 3.   POST    /oic/sec/pstat  -> dos.s=3
     */

    public Completable execute(Device device, long credId) {
        return iotivityRepository.getSecureEndpoint(device)
                .flatMapCompletable(endpoint ->
                        mPstatRepository.changeDeviceStatus(endpoint, device.getDeviceId(), OcfDosType.OC_DOSTYPE_RFPRO)
                                .andThen(mCmsRepository.deleteCredential(endpoint, device.getDeviceId(), credId))
                                .andThen(mPstatRepository.changeDeviceStatus(endpoint, device.getDeviceId(), OcfDosType.OC_DOSTYPE_RFNOP)));
    }
}

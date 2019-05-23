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
import org.openconnectivity.otgc.data.repository.PstatRepository;
import org.openconnectivity.otgc.data.repository.IotivityRepository;
import org.openconnectivity.otgc.domain.model.devicelist.Device;
import org.openconnectivity.otgc.utils.constant.OcfDosType;

import java.security.SecureRandom;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;

import io.reactivex.Completable;

public class PairwiseDevicesUseCase {
    private final IotivityRepository iotivityRepository;
    private final PstatRepository pstatRepository;
    private final CmsRepository cmsRepository;

    @Inject
    public PairwiseDevicesUseCase(IotivityRepository iotivityRepository,
                              PstatRepository pstatRepository,
                              CmsRepository cmsRepository)
    {
        this.iotivityRepository = iotivityRepository;
        this.pstatRepository = pstatRepository;
        this.cmsRepository = cmsRepository;
    }

    public Completable execute(Device client, Device server)
    {
        SecureRandom secureRandom = new SecureRandom();
        byte[] symmetricKey = new byte[16];
        secureRandom.nextBytes(symmetricKey);
        SecretKey secretKey = new SecretKeySpec(symmetricKey, "AES");

        Completable clientPairwise = iotivityRepository.getSecureEndpoint(client)
                .flatMapCompletable(endpoint ->
                        pstatRepository.changeDeviceStatus(endpoint, client.getDeviceId(), OcfDosType.OC_DOSTYPE_RFPRO)
                                .andThen(cmsRepository.createPskCredential(endpoint, client.getDeviceId(), server.getDeviceId(), secretKey.getEncoded()))
                                .andThen(pstatRepository.changeDeviceStatus(endpoint, client.getDeviceId(), OcfDosType.OC_DOSTYPE_RFNOP)));

        Completable serverPairwise = iotivityRepository.getSecureEndpoint(server)
                .flatMapCompletable(endpoint ->
                        pstatRepository.changeDeviceStatus(endpoint, server.getDeviceId(), OcfDosType.OC_DOSTYPE_RFPRO)
                                .andThen(cmsRepository.createPskCredential(endpoint, server.getDeviceId(), client.getDeviceId(), secretKey.getEncoded()))
                                .andThen(pstatRepository.changeDeviceStatus(endpoint, server.getDeviceId(), OcfDosType.OC_DOSTYPE_RFNOP)));

        return clientPairwise
                .andThen(serverPairwise);
    }
}

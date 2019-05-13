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

package org.openconnectivity.otgc.domain.usecase.easysetup;

import org.openconnectivity.otgc.data.repository.IotivityRepository;
import org.openconnectivity.otgc.data.repository.EasySetupRepository;

import javax.inject.Inject;

import io.reactivex.Completable;

public class WiFiEasySetupUseCase {
    private final IotivityRepository mIotivityRepository;
    private final EasySetupRepository mEasySetupRepository;

    @Inject
    WiFiEasySetupUseCase(IotivityRepository iotivityRepository,
                         EasySetupRepository easySetupRepository) {
        this.mIotivityRepository = iotivityRepository;
        this.mEasySetupRepository = easySetupRepository;
    }

    public Completable execute(String deviceId, String ssid, String pwd,
                               int authenticationType, int encodingType) {
        return Completable.complete();
        /*return mIotivityRepository.getDeviceCoapIpv6Host(deviceId)
                .flatMap(host -> mIotivityRepository.findResource(host,
                        ESConstants.OC_RSRVD_ES_RES_TYPE_EASYSETUP))
                .flatMapCompletable(ocResource ->
                        mEasySetupRepository.configureAndConnect(ocResource, ssid, pwd,
                                authenticationType, encodingType)
                );*/
    }
}

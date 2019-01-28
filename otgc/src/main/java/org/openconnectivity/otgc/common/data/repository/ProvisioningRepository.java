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

package org.openconnectivity.otgc.common.data.repository;

import android.content.Context;

import org.iotivity.base.OcConnectivityType;
import org.iotivity.base.OcProvisioning;
import org.iotivity.base.OxmType;
import org.iotivity.ca.CaInterface;
import org.iotivity.ca.OicCipher;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Completable;
import timber.log.Timber;

@Singleton
public class ProvisioningRepository {

    private final Context mContext;

    private final OcProvisioning.PinCallbackListener listener = () -> "";

    @Inject
    public ProvisioningRepository(Context context) {
        this.mContext = context;
    }

    public Completable initialize(String pdmDbFile) {
        return Completable.fromAction(() -> {
            String pdmDbPath = mContext.getFilesDir().getAbsolutePath()
                    .replace("files", "databases") + File.separator;
            File file = new File(pdmDbPath);
            if (!(file.isDirectory())) {
                file.mkdirs();
                Timber.d("Pdm DB directory created at %s", pdmDbPath);
            }
            OcProvisioning.provisionInit(pdmDbPath + pdmDbFile);

            OcProvisioning.setOwnershipTransferCBdata(OxmType.OIC_JUST_WORKS, listener);
        });
    }

    public Completable doSelfOwnership() {
        return Completable.fromAction(OcProvisioning::doSelfOwnershiptransfer);
    }

    public Completable resetSvrDb() {
        return Completable.fromAction(OcProvisioning::resetSvrDb);
    }

    public Completable saveCertificates(byte[] caCertPem, byte[] caKeyPem) {
        return Completable.create(emitter -> {
            int ret = CaInterface.setCipherSuite(OicCipher.TLS_ECDH_anon_WITH_AES_128_CBC_SHA,
                    OcConnectivityType.CT_ADAPTER_IP);
            Timber.d("CaInterface.setCipherSuite returned = %d", ret);

            emitter.onComplete();
        });
    }

    public Completable close() {
        return Completable.fromAction(OcProvisioning::provisionClose);
    }
}

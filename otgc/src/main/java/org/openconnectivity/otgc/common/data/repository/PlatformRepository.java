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

import org.iotivity.base.ModeType;
import org.iotivity.base.OcException;
import org.iotivity.base.OcPlatform;
import org.iotivity.base.PayloadType;
import org.iotivity.base.PlatformConfig;
import org.iotivity.base.QualityOfService;
import org.iotivity.base.ServiceType;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Completable;
import io.reactivex.Single;
import timber.log.Timber;

@Singleton
public class PlatformRepository {

    private final Context mContext;

    @Inject
    public PlatformRepository(Context context) {
        this.mContext = context;
    }

    public Completable initialize(String svrDbFile, String introspectionFile) {
        return Completable.fromAction(() -> {
            String filesPath = mContext.getFilesDir().getPath() + File.separator; // data/data/<package>/files/

            // Create platform config
            PlatformConfig platformConfig = new PlatformConfig(
                    null,
                    mContext,
                    ServiceType.IN_PROC,
                    ModeType.CLIENT_SERVER,
                    "0.0.0.0",
                    5683,
                    QualityOfService.HIGH,
                    filesPath + svrDbFile,
                    filesPath + introspectionFile
            );
            OcPlatform.Configure(platformConfig);

            OcPlatform.setPropertyValue(PayloadType.DEVICE.getValue(), "n", "OTGC");
            OcPlatform.setPropertyValue(PayloadType.DEVICE.getValue(), "icv", "ocf.1.3.0");
            OcPlatform.setPropertyValue(PayloadType.DEVICE.getValue(), "dmv", "ocf.res.1.3.0");
            OcPlatform.setPropertyValue(PayloadType.PLATFORM.getValue(), "mnmn", "DEKRA Testing and Certification, S.A.U.");
        });
    }

    public Completable close() {
        return Completable.fromAction(OcPlatform::Shutdown);
    }

    public Single<String> getDeviceId() {
        return Single.fromCallable(() -> {
            ByteBuffer bb = ByteBuffer.wrap(OcPlatform.getDeviceId());
            UUID uuid = new UUID(bb.getLong(), bb.getLong());
            return uuid.toString();
        });
    }

    public void setDevicePiid(String piid) {
        try {
            OcPlatform.setPropertyValue(PayloadType.DEVICE.getValue(), "piid", piid);
        } catch (OcException e) {
            Timber.e(e);
        }

    }

    public String getDevicePiid() {
        String piid = "";
        try {
            piid = OcPlatform.getPropertyValue(PayloadType.DEVICE.getValue(), "piid");
        } catch (OcException e) {
            Timber.e(e);
        }
        return piid;
    }
}

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

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Single;

@Singleton
public class PermissionRepository {

    private static final String[] sPermissions;

    static {
        sPermissions = new String[] {
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
    }

    private Context mContext;

    @Inject
    public PermissionRepository(Context ctx) {
        this.mContext = ctx;
    }

    public Single<List<String>> getMissedPermissions() {
        return Single.create(emitter -> {
            List<String> missedPermissions = new ArrayList<>();

            for (String permission : sPermissions) {
                if (ContextCompat.checkSelfPermission(mContext, permission)
                        != PackageManager.PERMISSION_GRANTED) {
                    missedPermissions.add(permission);
                }
            }
            emitter.onSuccess(missedPermissions);
        });
    }
}

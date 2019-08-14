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

package org.openconnectivity.otgc.data.entity;

import org.iotivity.OCEndpoint;
import org.iotivity.OCEndpointUtil;
import org.openconnectivity.otgc.domain.model.devicelist.DeviceType;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "devices")
public class DeviceEntity {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "deviceid")
    private String mId;

    @ColumnInfo(name = "name")
    private String mName;

    @ColumnInfo(name = "hosts")
    private List<String> mHosts;

    @ColumnInfo(name = "deviceType")
    private DeviceType mType;

    @ColumnInfo(name = "devicePermits")
    private int mPermits;

    public DeviceEntity(@NonNull String id, String name, OCEndpoint endpoints, @Nullable DeviceType type, int permits) {
        this.mId = id;
        this.mName = name;
        this.mType = type;
        this.mPermits = permits;
        mHosts = new ArrayList<>();

        while(endpoints != null) {
            String endpointStr = OCEndpointUtil.toString(endpoints);
            mHosts.add(endpointStr);

            endpoints = endpoints.getNext();
        }
    }


    public DeviceEntity(@NonNull String id, String name, List<String> hosts, @Nullable DeviceType type, int permits) {
        this.mId = id;
        this.mName = name;
        this.mHosts = hosts;
        this.mType = type;
        this.mPermits = permits;
    }

    public String getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public List<String> getHosts() {
        return mHosts;
    }

    public DeviceType getType() {
        return mType;
    }

    public int getPermits() {
        return mPermits;
    }

}

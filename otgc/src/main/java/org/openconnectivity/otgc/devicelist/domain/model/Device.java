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
package org.openconnectivity.otgc.devicelist.domain.model;

import org.iotivity.base.OcSecureResource;
import org.openconnectivity.otgc.common.domain.model.OcDevice;

/**
 * Model class for unowned and owned devices
 */

public class Device {
    private DeviceType mType;
    private String mDeviceId;
    private OcDevice mDeviceInfo;
    private OcSecureResource mOcSecureResource;

    public Device(DeviceType type, String deviceId, OcDevice deviceInfo, OcSecureResource ocSecureResource) {
        super();

        this.mType = type;
        this.mDeviceId = deviceId;
        this.mDeviceInfo = deviceInfo;
        this.mOcSecureResource = ocSecureResource;
    }

    public DeviceType getType() {
        return mType;
    }

    public void setType(DeviceType type) {
        this.mType = type;
    }

    public String getDeviceId() {
        return mDeviceId;
    }

    public void setDeviceId(String deviceId) {
        this.mDeviceId = deviceId;
    }

    public OcDevice getDeviceInfo() {
        return mDeviceInfo;
    }

    public void setDeviceInfo(OcDevice deviceInfo) {
        this.mDeviceInfo = deviceInfo;
    }

    public OcSecureResource getOcSecureResource() {
        return mOcSecureResource;
    }

    public void setOcSecureResource(OcSecureResource ocSecureResource) {
        this.mOcSecureResource = ocSecureResource;
    }
}

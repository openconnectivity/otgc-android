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

package org.openconnectivity.otgc.common.domain.model;

import org.iotivity.base.OcException;
import org.iotivity.base.OcRepresentation;

import timber.log.Timber;

public class OicPlatform {
    private static final String PLATFORM_ID_KEY = "pi";
    private static final String MAN_NAME_KEY = "mnmn";
    private static final String MAN_URL_KEY = "mnml";
    private static final String MAN_MODEL_NO_KEY = "mnmo";
    private static final String MAN_DATE_KEY = "mndt";
    private static final String MAN_PLATFORM_VER_KEY = "mnpv";
    private static final String MAN_OS_VER_KEY = "mnos";
    private static final String MAN_HW_VER_KEY = "mnhw";
    private static final String MAN_FW_VER_KEY = "mnfv";
    private static final String MAN_SUPPORT_URL_KEY = "mnsl";
    private static final String MAN_SYSTEM_TIME_KEY = "st";

    private String mPlatformId;
    private String mManufacturerName;
    private String mManufacturerUrl;
    private String mManufacturerModelNumber;
    private String mManufacturedDate;
    private String mManufacturerPlatformVersion;
    private String mManufacturerOsVersion;
    private String mManufacturerHwVersion;
    private String mManufacturerFwVersion;
    private String mManufacturerSupportUrl;
    private String mManufacturerSystemTime;

    public OicPlatform() {
        mPlatformId = "";
        mManufacturerName = "";
        mManufacturerUrl = "";
        mManufacturerModelNumber = "";
        mManufacturedDate = "";
        mManufacturerPlatformVersion = "";
        mManufacturerOsVersion = "";
        mManufacturerHwVersion = "";
        mManufacturerFwVersion = "";
        mManufacturerSupportUrl = "";
        mManufacturerSystemTime = "";
    }

    public void setOcRepresentation(OcRepresentation rep) {
        try {
            mPlatformId = rep.getValue(PLATFORM_ID_KEY);
        } catch (OcException e) {
            Timber.d("Field %s not found", PLATFORM_ID_KEY);
        }

        try {
            mManufacturerName = rep.getValue(MAN_NAME_KEY);
        } catch (OcException e) {
            Timber.d("Field %s not found", MAN_NAME_KEY);
        }

        try {
            mManufacturerUrl = rep.getValue(MAN_URL_KEY);
        } catch (OcException e) {
            Timber.d("Field %s not found", MAN_URL_KEY);
        }

        try {
            mManufacturerModelNumber = rep.getValue(MAN_MODEL_NO_KEY);
        } catch (OcException e) {
            Timber.d("Field %s not found", MAN_MODEL_NO_KEY);
        }

        try {
            mManufacturedDate = rep.getValue(MAN_DATE_KEY);
        } catch (OcException e) {
            Timber.d("Field %s not found", MAN_DATE_KEY);
        }

        try {
            mManufacturerPlatformVersion = rep.getValue(MAN_PLATFORM_VER_KEY);
        } catch (OcException e) {
            Timber.d("Field %s not found", MAN_PLATFORM_VER_KEY);
        }

        try {
            mManufacturerOsVersion = rep.getValue(MAN_OS_VER_KEY);
        } catch (OcException e) {
            Timber.d("Field %s not found", MAN_OS_VER_KEY);
        }

        try {
            mManufacturerHwVersion = rep.getValue(MAN_HW_VER_KEY);
        } catch (OcException e) {
            Timber.d("Field %s not found", MAN_HW_VER_KEY);
        }

        try {
            mManufacturerFwVersion = rep.getValue(MAN_FW_VER_KEY);
        } catch (OcException e) {
            Timber.d("Field %s not found", MAN_FW_VER_KEY);
        }

        try {
            mManufacturerSupportUrl = rep.getValue(MAN_SUPPORT_URL_KEY);
        } catch (OcException e) {
            Timber.d("Field %s not found", MAN_SUPPORT_URL_KEY);
        }

        try {
            mManufacturerSystemTime = rep.getValue(MAN_SYSTEM_TIME_KEY);
        } catch (OcException e) {
            Timber.d("Field %s not found", MAN_SYSTEM_TIME_KEY);
        }
    }

    public OcRepresentation getOcRepresentation() throws OcException {
        OcRepresentation rep = new OcRepresentation();
        rep.setValue(PLATFORM_ID_KEY, mPlatformId);
        rep.setValue(MAN_NAME_KEY, mManufacturerName);
        rep.setValue(MAN_URL_KEY, mManufacturerUrl);
        rep.setValue(MAN_MODEL_NO_KEY, mManufacturerModelNumber);
        rep.setValue(MAN_DATE_KEY, mManufacturedDate);
        rep.setValue(MAN_PLATFORM_VER_KEY, mManufacturerPlatformVersion);
        rep.setValue(MAN_OS_VER_KEY, mManufacturerOsVersion);
        rep.setValue(MAN_HW_VER_KEY, mManufacturerHwVersion);
        rep.setValue(MAN_FW_VER_KEY, mManufacturerFwVersion);
        rep.setValue(MAN_SUPPORT_URL_KEY, mManufacturerSupportUrl);
        rep.setValue(MAN_SYSTEM_TIME_KEY, mManufacturerSystemTime);

        return rep;
    }

    public String getPlatformId() {
        return mPlatformId;
    }

    public void setPlatformId(String platformId) {
        this.mPlatformId = platformId;
    }

    public String getManufacturerName() {
        return mManufacturerName;
    }

    public void setManufacturerName(String manufacturerName) {
        this.mManufacturerName = manufacturerName;
    }

    public String getManufacturerUrl() {
        return mManufacturerUrl;
    }

    public void setManufacturerUrl(String manufacturerUrl) {
        this.mManufacturerUrl = manufacturerUrl;
    }

    public String getManufacturerModelNumber() {
        return mManufacturerModelNumber;
    }

    public void setManufacturerModelNumber(String manufacturerModelNumber) {
        this.mManufacturerModelNumber = manufacturerModelNumber;
    }

    public String getManufacturedDate() {
        return mManufacturedDate;
    }

    public void setManufacturedDate(String manufacturedDate) {
        this.mManufacturedDate = manufacturedDate;
    }

    public String getManufacturerPlatformVersion() {
        return mManufacturerPlatformVersion;
    }

    public void setManufacturerPlatformVersion(String manufacturerPlatformVersion) {
        this.mManufacturerPlatformVersion = manufacturerPlatformVersion;
    }

    public String getManufacturerOsVersion() {
        return mManufacturerOsVersion;
    }

    public void setManufacturerOsVersion(String manufacturerOsVersion) {
        this.mManufacturerOsVersion = manufacturerOsVersion;
    }

    public String getManufacturerHwVersion() {
        return mManufacturerHwVersion;
    }

    public void setManufacturerHwVersion(String manufacturerHwVersion) {
        this.mManufacturerHwVersion = manufacturerHwVersion;
    }

    public String getManufacturerFwVersion() {
        return mManufacturerFwVersion;
    }

    public void setManufacturerFwVersion(String manufacturerFwVersion) {
        this.mManufacturerFwVersion = manufacturerFwVersion;
    }

    public String getManufacturerSupportUrl() {
        return mManufacturerSupportUrl;
    }

    public void setManufacturerSupportUrl(String manufacturerSupportUrl) {
        this.mManufacturerSupportUrl = manufacturerSupportUrl;
    }

    public String getManufacturerSystemTime() {
        return mManufacturerSystemTime;
    }

    public void setManufacturerSystemTime(String manufacturerSystemTime) {
        this.mManufacturerSystemTime = manufacturerSystemTime;
    }

    @Override
    public String toString() {
        return "\t" + PLATFORM_ID_KEY + ": " + mPlatformId
                + "\n\t" + MAN_NAME_KEY + ": " + mManufacturerName
                + "\n\t" + MAN_URL_KEY + ": " + mManufacturerUrl
                + "\n\t" + MAN_MODEL_NO_KEY + ": " + mManufacturerModelNumber
                + "\n\t" + MAN_DATE_KEY + ": " + mManufacturedDate
                + "\n\t" + MAN_PLATFORM_VER_KEY + ": " + mManufacturerPlatformVersion
                + "\n\t" + MAN_OS_VER_KEY + ": " + mManufacturerOsVersion
                + "\n\t" + MAN_HW_VER_KEY + ": " + mManufacturerHwVersion
                + "\n\t" + MAN_FW_VER_KEY + ": " + mManufacturerFwVersion
                + "\n\t" + MAN_SUPPORT_URL_KEY + ": " + mManufacturerSupportUrl
                + "\n\t" + MAN_SYSTEM_TIME_KEY + ": " + mManufacturerSystemTime;
    }
}


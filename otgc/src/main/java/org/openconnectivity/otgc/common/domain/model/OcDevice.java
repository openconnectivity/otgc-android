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

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class OcDevice {
    private static final String NAME_KEY = "n";
    private static final String SPEC_VERSION_URL_KEY = "icv";
    private static final String DEVICE_ID_KEY = "di";
    private static final String DATA_MODEL_KEY = "dmv";
    private static final String PIID_KEY = "piid";
    private static final String DESCRIPTIONS_KEY = "ld";
    private static final String SW_VERSION_KEY = "sv";
    private static final String MAN_NAME_KEY = "dmn";
    private static final String MODEL_NO_KEY = "dmno";

    private String mName;
    private String mSpecVersionUrl;
    private String mDeviceId;
    private String mDataModel;
    private String mPiid;
    private List<String> mLocDescriptions;
    private String mSwVersion;
    private String mManufacturerName;
    private String mModelNumber;

    private List<String> mDeviceTypes;

    public OcDevice() {
        mName = "";
        mSpecVersionUrl = "";
        mDeviceId = "";
        mDataModel = "";
        mPiid = "";
        mLocDescriptions = new ArrayList<>();
        mSwVersion = "";
        mManufacturerName = "";
        mModelNumber = "";
        mDeviceTypes = new ArrayList<>();
    }

    public void setOcRepresentation(OcRepresentation rep) {
        try {
            mName = rep.getValue(NAME_KEY);
        } catch (OcException e) {
            Timber.d("Field %s not found", NAME_KEY);
        }

        try {
            mSpecVersionUrl = rep.getValue(SPEC_VERSION_URL_KEY);
        } catch (OcException e) {
            Timber.d("Field %s not found", SPEC_VERSION_URL_KEY);
        }

        try {
            mDeviceId = rep.getValue(DEVICE_ID_KEY);
        } catch (OcException e) {
            Timber.d("Field %s not found", DEVICE_ID_KEY);
        }

        try {
            mDataModel = rep.getValue(DATA_MODEL_KEY);
        } catch (OcException e) {
            Timber.d("Field %s not found", DATA_MODEL_KEY);
        }

        try {
            mPiid = rep.getValue(PIID_KEY);
        } catch (OcException e) {
            Timber.d("Field %s not found", PIID_KEY);
        }

        try {
            mLocDescriptions = rep.getValue(DESCRIPTIONS_KEY);
        } catch (OcException e) {
            Timber.d("Field %s not found", DESCRIPTIONS_KEY);
        }

        try {
            mSwVersion = rep.getValue(SW_VERSION_KEY);
        } catch (OcException e) {
            Timber.d("Field %s not found", SW_VERSION_KEY);
        }

        try {
            mManufacturerName = rep.getValue(MAN_NAME_KEY);
        } catch (OcException e) {
            Timber.d("Field %s not found", MAN_NAME_KEY);
        }

        try {
            mModelNumber = rep.getValue(MODEL_NO_KEY);
        } catch (OcException e) {
            Timber.d("Field %s not found", MODEL_NO_KEY);
        }

        mDeviceTypes = rep.getResourceTypes();
    }

    public OcRepresentation getOcRepresentation() throws OcException {
        OcRepresentation rep = new OcRepresentation();
        rep.setValue(NAME_KEY, mName);
        rep.setValue(SPEC_VERSION_URL_KEY, mSpecVersionUrl);
        rep.setValue(DEVICE_ID_KEY, mDeviceId);
        rep.setValue(DATA_MODEL_KEY, mDataModel);
        rep.setValue(PIID_KEY, mPiid);
        if (mLocDescriptions.size() > 0) {
            rep.setValue(DESCRIPTIONS_KEY, mLocDescriptions.toArray(new String[mLocDescriptions.size()]));
        }
        rep.setValue(SW_VERSION_KEY, mSwVersion);
        rep.setValue(MAN_NAME_KEY, mManufacturerName);
        rep.setValue(MODEL_NO_KEY, mModelNumber);

        rep.setResourceTypes(mDeviceTypes);

        return rep;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getSpecVersionUrl() {
        return mSpecVersionUrl;
    }

    public void setSpecVersionUrl(String specVersionUrl) {
        this.mSpecVersionUrl = specVersionUrl;
    }

    public String getDeviceId() {
        return mDeviceId;
    }

    public void setDeviceId(String deviceId) {
        this.mDeviceId = deviceId;
    }

    public String getDataModel() {
        return mDataModel;
    }

    public void setDataModel(String dataModel) {
        this.mDataModel = dataModel;
    }

    public String getPiid() {
        return mPiid;
    }

    public void setPiid(String piid) {
        this.mPiid = piid;
    }

    public List<String> getLocalizedDescriptions() {
        return mLocDescriptions;
    }

    public void setLocalizedDescriptions(List<String> locDescriptions) {
        this.mLocDescriptions = locDescriptions;
    }

    public String getSoftwareVersion() {
        return mSwVersion;
    }

    public void setSoftwareVersion(String swVersion) {
        this.mSwVersion = swVersion;
    }

    public String getManufacturerName() {
        return mManufacturerName;
    }

    public void setmManufacturerName(String manufacturerName) {
        this.mManufacturerName = manufacturerName;
    }

    public String getModelNumber() {
        return mModelNumber;
    }

    public void setModelNumber(String modelNumber) {
        this.mModelNumber = modelNumber;
    }

    public List<String> getDeviceTypes() {
        return mDeviceTypes;
    }

    public List<String> getFormattedDeviceTypes() {
        List<String> formattedDeviceTypes = new ArrayList<>();
        for (String deviceType : mDeviceTypes) {
            if (!deviceType.equals("oic.wk.d")) {
                formattedDeviceTypes.add(deviceType.replace("oic.d.", ""));
            }
        }

        return formattedDeviceTypes;
    }

    public void setDeviceTypes(List<String> deviceTypes) {
        this.mDeviceTypes = deviceTypes;
    }

    @Override
    public String toString() {
        return "\t" + NAME_KEY + ": " + mName
                + "\n\t" + SPEC_VERSION_URL_KEY + ": " + mSpecVersionUrl
                + "\n\t" + DEVICE_ID_KEY + ": " + mDeviceId
                + "\n\t" + DATA_MODEL_KEY + ": " + mDataModel
                + "\n\t" + PIID_KEY + ": " + mPiid
                + "\n\t" + DESCRIPTIONS_KEY + ": " + mLocDescriptions
                + "\n\t" + SW_VERSION_KEY + ": " + mSwVersion
                + "\n\t" + MAN_NAME_KEY + ": " + mManufacturerName
                + "\n\t" + MODEL_NO_KEY + ": " + mModelNumber;
    }
}

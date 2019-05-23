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

package org.openconnectivity.otgc.domain.model.resource.virtual.d;

import org.iotivity.OCRep;
import org.iotivity.OCRepresentation;
import org.openconnectivity.otgc.domain.model.resource.OcResourceBase;
import org.openconnectivity.otgc.utils.constant.OcfResourceAttributeKey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OcDeviceInfo extends OcResourceBase {

    private String specVersionUrl;
    private String deviceId;
    private String dataModel;
    private String piid;
    private List<String> locDescriptions;
    private String swVersion;
    private String manufacturerName;
    private String modelNumber;

    public OcDeviceInfo() {
        super();
    }

    public String getSpecVersionUrl() {
        return specVersionUrl;
    }

    public void setSpecVersionUrl(String specVersionUrl) {
        this.specVersionUrl = specVersionUrl;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDataModel() {
        return dataModel;
    }

    public void setDataModel(String dataModel) {
        this.dataModel = dataModel;
    }

    public String getPiid() {
        return piid;
    }

    public void setPiid(String piid) {
        this.piid = piid;
    }

    public List<String> getLocalizedDescriptions() {
        return locDescriptions;
    }

    public void setLocalizedDescriptions(List<String> locDescriptions) {
        this.locDescriptions = locDescriptions;
    }

    public String getSoftwareVersion() {
        return swVersion;
    }

    public void setSoftwareVersion(String swVersion) {
        this.swVersion = swVersion;
    }

    public String getManufacturerName() {
        return manufacturerName;
    }

    public void setManufacturerName(String manufacturerName) {
        this.manufacturerName = manufacturerName;
    }

    public String getModelNumber() {
        return modelNumber;
    }

    public void setModelNumber(String modelNumber) {
        this.modelNumber = modelNumber;
    }

    public List<String> getFormattedDeviceTypes() {
        List<String> formattedDeviceTypes = new ArrayList<>();
        if (this.getResourceTypes() != null) {
            for (String deviceType : this.getResourceTypes()) {
                if (!deviceType.equals("oic.wk.d")) {
                    formattedDeviceTypes.add(deviceType.replace("oic.d", ""));
                }
            }
        }

        return formattedDeviceTypes;
    }

    /**
     * Required ["n", "di", "icv", "dmv", "piid"]
     */

    public void parseOCRepresentation(OCRepresentation rep) {
        /* di */
        String di = OCRep.getString(rep, OcfResourceAttributeKey.DEVICE_ID_KEY);
        this.setDeviceId(di);
        /* piid */
        String piid = OCRep.getString(rep, OcfResourceAttributeKey.PIID_KEY);
        this.setPiid(piid);
        /* icv */
        String icv = OCRep.getString(rep, OcfResourceAttributeKey.SPEC_VERSION_URL_KEY);
        this.setSpecVersionUrl(icv);
        /* dmv */
        String dmv = OCRep.getString(rep, OcfResourceAttributeKey.DATA_MODEL_KEY);
        this.setDataModel(dmv);
        /* sv */
        String sv = OCRep.getString(rep, OcfResourceAttributeKey.SW_VERSION_KEY);
        this.setSoftwareVersion(sv);
        /* dmn */
        String dmn = OCRep.getString(rep, OcfResourceAttributeKey.DEV_MAN_NAME_KEY);
        this.setManufacturerName(dmn);
        /* dmno */
        String dmno = OCRep.getString(rep, OcfResourceAttributeKey.DEV_MODEL_NO_KEY);
        this.setModelNumber(dmno);
        /* ld */
        String[] ld = OCRep.getStringArray(rep, OcfResourceAttributeKey.DESCRIPTIONS_KEY);
        this.setLocalizedDescriptions(ld != null ? Arrays.asList(ld) : null);

        /* id */
        String id = OCRep.getString(rep, OcfResourceAttributeKey.ID_KEY);
        this.setId(id);
        /* n */
        String name = OCRep.getString(rep, OcfResourceAttributeKey.NAME_KEY);
        this.setName(name);
        /* rt */
        String[] resourceTypes = OCRep.getStringArray(rep, OcfResourceAttributeKey.RESOURCE_TYPES_KEY);
        this.setResourceTypes(resourceTypes != null ? Arrays.asList(resourceTypes) : null);
        /* if */
        String[] interfaces = OCRep.getStringArray(rep, OcfResourceAttributeKey.INTERFACES_KEY);
        this.setInterfaces(interfaces != null ? Arrays.asList(interfaces) : null);
    }

    @Override
    public String toString() {
        return "\t" + OcfResourceAttributeKey.NAME_KEY + ": " + this.getName()
                + "\n\t" + OcfResourceAttributeKey.SPEC_VERSION_URL_KEY + ": " + specVersionUrl
                + "\n\t" + OcfResourceAttributeKey.DEVICE_ID_KEY + ": " + deviceId
                + "\n\t" + OcfResourceAttributeKey.DATA_MODEL_KEY + ": " + dataModel
                + "\n\t" + OcfResourceAttributeKey.PIID_KEY + ": " + piid
                + "\n\t" + OcfResourceAttributeKey.DESCRIPTIONS_KEY + ": " + locDescriptions
                + "\n\t" + OcfResourceAttributeKey.SW_VERSION_KEY + ": " + swVersion
                + "\n\t" + OcfResourceAttributeKey.DEV_MAN_NAME_KEY + ": " + manufacturerName
                + "\n\t" + OcfResourceAttributeKey.DEV_MODEL_NO_KEY + ": " + modelNumber;
    }
}
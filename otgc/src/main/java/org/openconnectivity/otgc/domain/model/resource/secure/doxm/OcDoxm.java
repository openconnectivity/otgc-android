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

package org.openconnectivity.otgc.domain.model.resource.secure.doxm;

import org.iotivity.CborEncoder;
import org.iotivity.OCRep;
import org.iotivity.OCRepresentation;
import org.openconnectivity.otgc.domain.model.resource.OcResourceBase;
import org.openconnectivity.otgc.utils.constant.OcfOxmType;
import org.openconnectivity.otgc.utils.constant.OcfResourceAttributeKey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OcDoxm extends OcResourceBase {

    private List<OcfOxmType> oxms;
    private OcfOxmType oxmsel;
    private Long supportedCredential;
    private Boolean owned;
    private String deviceuuid;
    private String devowneruuid;
    private String rowneruuid;

    public OcDoxm() {
    }

    public List<OcfOxmType> getOxms() {
        return oxms;
    }

    public void setOxms(List<OcfOxmType> oxms) {
        this.oxms = oxms;
    }

    public OcfOxmType getOxmsel() {
        return oxmsel;
    }

    public void setOxmsel(OcfOxmType oxmsel) {
        this.oxmsel = oxmsel;
    }

    public Long getSupportedCredential() {
        return supportedCredential;
    }

    public void setSupportedCredential(Long supportedCredential) {
        this.supportedCredential = supportedCredential;
    }

    public Boolean getOwned() {
        return owned;
    }

    public void setOwned(Boolean owned) {
        this.owned = owned;
    }

    public String getDeviceuuid() {
        return deviceuuid;
    }

    public void setDeviceuuid(String deviceuuid) {
        this.deviceuuid = deviceuuid;
    }

    public String getDevowneruuid() {
        return devowneruuid;
    }

    public void setDevowneruuid(String devowneruuid) {
        this.devowneruuid = devowneruuid;
    }

    public String getRowneruuid() {
        return rowneruuid;
    }

    public void setRowneruuid(String rowneruuid) {
        this.rowneruuid = rowneruuid;
    }

    public void parseOCRepresentation(OCRepresentation rep) {
        /* oxms */
        long[] oxmsValue = OCRep.getLongArray(rep, OcfResourceAttributeKey.OXMS_KEY);
        List<OcfOxmType> oxmTypes = new ArrayList<>();
        for (long value : oxmsValue) {
            oxmTypes.add(OcfOxmType.valueToEnum((int)value));
        }
        this.setOxms(oxmTypes);
        /* oxmsel */
        Long oxms = OCRep.getLong(rep, OcfResourceAttributeKey.OXMSEL_KEY);
        this.setOxmsel(oxms == -1 ? oxmTypes.get(0): OcfOxmType.valueToEnum(oxms.intValue()));
        /* sct */
        Long sct = OCRep.getLong(rep, OcfResourceAttributeKey.SUPPORTED_CREDENTIAL_KEY);
        this.setSupportedCredential(sct);
        /* owned */
        boolean owned = OCRep.getBoolean(rep, OcfResourceAttributeKey.OWNED_KEY);
        this.setOwned(Boolean.valueOf(owned));
        /* deviceuuid */
        String deviceuuid = OCRep.getString(rep, OcfResourceAttributeKey.DEVICE_UUID_KEY);
        this.setDeviceuuid(deviceuuid);
        /* devowneruuid */
        String devowneruuid = OCRep.getString(rep, OcfResourceAttributeKey.DEVOWNER_UUID_KEY);
        this.setDevowneruuid(devowneruuid);
        /* rowneruuid */
        String rowneruuid = OCRep.getString(rep, OcfResourceAttributeKey.ROWNER_UUID_KEY);
        this.setRowneruuid(rowneruuid);
        /* rt */
        String[] resourceTypes = OCRep.getStringArray(rep, OcfResourceAttributeKey.RESOURCE_TYPES_KEY);
        this.setResourceTypes(Arrays.asList(resourceTypes));
        /* if */
        String[] interfaces = OCRep.getStringArray(rep, OcfResourceAttributeKey.INTERFACES_KEY);
        this.setResourceTypes(Arrays.asList(interfaces));
    }

    public CborEncoder parseToCbor() {
        CborEncoder root = OCRep.beginRootObject();

        /* oxms */
        if (this.getOxms() != null && !this.getOxms().isEmpty()) {
            long[] intArray = new long[this.getOxms().size()];
            int i = 0;
            for (OcfOxmType value : this.getOxms()) {
                intArray[i] = value.getValue();
                i++;
            }
            OCRep.setLongArray(root, OcfResourceAttributeKey.OXMS_KEY, intArray);
        }

        /* oxmsel */
        if (this.getOxmsel() != null) {
            OCRep.setLong(root, OcfResourceAttributeKey.OXMSEL_KEY, this.getOxmsel().getValue());
        }
        /* sct */
        if (this.getSupportedCredential() != null) {
            OCRep.setLong(root, OcfResourceAttributeKey.SUPPORTED_CREDENTIAL_KEY, this.getSupportedCredential());
        }
        /* owned */
        if (this.getOwned() != null) {
            OCRep.setBoolean(root, OcfResourceAttributeKey.OWNED_KEY, this.getOwned());
        }
        /* deviceuuid */
        if (this.getDeviceuuid() != null && !this.getDeviceuuid().isEmpty()) {
            OCRep.setTextString(root, OcfResourceAttributeKey.DEVICE_UUID_KEY, this.getDeviceuuid());
        }
        /* devowneruuid */
        if (this.getDevowneruuid() != null && !this.getDevowneruuid().isEmpty()) {
            OCRep.setTextString(root, OcfResourceAttributeKey.DEVOWNER_UUID_KEY, this.getDevowneruuid());
        }
        /* rowneruuid */
        if (this.getRowneruuid() != null && !this.getRowneruuid().isEmpty()) {
            OCRep.setTextString(root, OcfResourceAttributeKey.ROWNER_UUID_KEY, this.getRowneruuid());
        }
        /* rt */
        if (this.getResourceTypes() != null && !this.getResourceTypes().isEmpty()) {
            OCRep.setStringArray(root, OcfResourceAttributeKey.RESOURCE_TYPES_KEY, this.getResourceTypes().toArray(new String[0]));
        }
        /* if */
        if (this.getInterfaces() != null && !this.getInterfaces().isEmpty()) {
            OCRep.setStringArray(root, OcfResourceAttributeKey.INTERFACES_KEY, this.getInterfaces().toArray(new String[0]));
        }

        OCRep.endRootObject();

        return root;
    }
}

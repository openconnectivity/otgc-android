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

package org.openconnectivity.otgc.domain.model.resource.secure.pstat;

import org.iotivity.CborEncoder;
import org.iotivity.OCRep;
import org.iotivity.OCRepresentation;
import org.openconnectivity.otgc.domain.model.resource.OcResourceBase;
import org.openconnectivity.otgc.utils.constant.OcfResourceAttributeKey;

import java.util.Arrays;
import java.util.List;

public class OcPstat extends OcResourceBase {

    private OcPstatDeviceState deviceState = null;
    private Boolean isOperational;
    private Long currentMode;
    private Long targetMode;
    private Long operationalMode;
    private Long supportedMode;
    private String deviceuuid;
    private String rowneruuid;

    public OcPstat() {
        super();
    }


    public OcPstatDeviceState getDeviceState() {
        return deviceState;
    }

    public void setDeviceState(OcPstatDeviceState deviceState) {
        this.deviceState = deviceState;
    }

    public Boolean getOperational() {
        return isOperational;
    }

    public void setOperational(Boolean operational) {
        isOperational = operational;
    }

    public Long getCurrentMode() {
        return currentMode;
    }

    public void setCurrentMode(Long currentMode) {
        this.currentMode = currentMode;
    }

    public Long getTargetMode() {
        return targetMode;
    }

    public void setTargetMode(Long targetMode) {
        this.targetMode = targetMode;
    }

    public Long getOperationalMode() {
        return operationalMode;
    }

    public void setOperationalMode(Long operationalMode) {
        this.operationalMode = operationalMode;
    }

    public Long getSupportedMode() {
        return supportedMode;
    }

    public void setSupportedMode(Long supportedMode) {
        this.supportedMode = supportedMode;
    }

    public String getDeviceuuid() {
        return deviceuuid;
    }

    public void setDeviceuuid(String deviceuuid) {
        this.deviceuuid = deviceuuid;
    }

    public String getRowneruuid() {
        return rowneruuid;
    }

    public void setRowneruuid(String rowneruuid) {
        this.rowneruuid = rowneruuid;
    }

    public void parseOCRepresentation(OCRepresentation rep) {
        /* dos */
        OCRepresentation obj = OCRep.getObject(rep, OcfResourceAttributeKey.DEVICE_STATE_KEY);
        OcPstatDeviceState dos = new OcPstatDeviceState();
        dos.parseOCRepresentation(obj);
        this.setDeviceState(dos);
        /* isop */
        boolean isop = OCRep.getBoolean(rep, OcfResourceAttributeKey.IS_OPERATIONAL_KEY);
        this.setOperational(Boolean.valueOf(isop));
        /* cm */
        Long cm = OCRep.getLong(rep, OcfResourceAttributeKey.CURRENT_MODE_KEY);
        this.setCurrentMode(cm);
        /* tm */
        Long tm = OCRep.getLong(rep, OcfResourceAttributeKey.TARGET_MODE_KEY);
        this.setTargetMode(tm);
        /* om */
        Long om = OCRep.getLong(rep, OcfResourceAttributeKey.OPERATIONAL_MODE_KEY);
        this.setOperationalMode(om);
        /* sm */
        Long sm = OCRep.getLong(rep, OcfResourceAttributeKey.SUPPORT_MODE_KEY);
        this.setSupportedMode(sm);
        /* deviceuuid */
        String deviceuuid = OCRep.getString(rep, OcfResourceAttributeKey.DEVICE_UUID_KEY);
        this.setDeviceuuid(deviceuuid);
        /* rowneruuid*/
        String rowneruuid = OCRep.getString(rep, OcfResourceAttributeKey.ROWNER_UUID_KEY);
        this.setRowneruuid(rowneruuid);
        /* rt */
        String[] resourceTypes = OCRep.getStringArray(rep, OcfResourceAttributeKey.RESOURCE_TYPES_KEY);
        this.setResourceTypes(Arrays.asList(resourceTypes));
        /* if */
        String[] interfaces = OCRep.getStringArray(rep, OcfResourceAttributeKey.INTERFACES_KEY);
        this.setInterfaces(Arrays.asList(interfaces));
    }

    public CborEncoder parseToCbor() {
        CborEncoder root = OCRep.beginRootObject();

        /* dos */
        if (this.getDeviceState() != null) {
            CborEncoder dos = OCRep.openObject(root, OcfResourceAttributeKey.DEVICE_STATE_KEY);
            this.getDeviceState().parseToCbor(dos);
            OCRep.closeObject(root, dos);
        }
        /* om */
        if (this.getOperationalMode() != null) {
            OCRep.setLong(root, OcfResourceAttributeKey.OPERATIONAL_MODE_KEY, this.getOperationalMode());
        }
        /* deviceuuid */
        if (this.getDeviceuuid() != null && !this.getDeviceuuid().isEmpty()) {
            OCRep.setTextString(root, OcfResourceAttributeKey.DEVICE_UUID_KEY, this.getDeviceuuid());
        }
        /* rowneruuid */
        if (this.getRowneruuid() != null && !this.getRowneruuid().isEmpty()) {
            OCRep.setTextString(root, OcfResourceAttributeKey.ROWNER_UUID_KEY, this.getRowneruuid());
        }

        OCRep.endRootObject();

        return root;
    }
}

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

package org.openconnectivity.otgc.domain.model.resource.virtual.p;

import org.iotivity.OCRep;
import org.iotivity.OCRepresentation;
import org.openconnectivity.otgc.domain.model.resource.OcResourceBase;
import org.openconnectivity.otgc.utils.constant.OcfResourceAttributeKey;

import java.util.Arrays;

public class OcPlatformInfo extends OcResourceBase {

    private String platformId;
    private String manufacturerName;
    private String manufacturerUrl;
    private String manufacturerModelNumber;
    private String manufacturedDate;
    private String manufacturerInfo;
    private String manufacturerPlatformVersion;
    private String manufacturerOsVersion;
    private String manufacturerHwVersion;
    private String manufacturerFwVersion;
    private String manufacturerSupportUrl;
    private String manufacturerSystemTime;
    private String manufacturerSerialNumber;

    public OcPlatformInfo() {
        super();
    }

    public String getPlatformId() {
        return platformId;
    }

    public void setPlatformId(String platformId) {
        this.platformId = platformId;
    }

    public String getManufacturerName() {
        return manufacturerName;
    }

    public void setManufacturerName(String manufacturerName) {
        this.manufacturerName = manufacturerName;
    }

    public String getManufacturerInfo() {
        return manufacturerInfo;
    }

    public void setManufacturerInfo(String manufacturerInfo) {
        this.manufacturerInfo = manufacturerInfo;
    }

    public String getManufacturerUrl() {
        return manufacturerUrl;
    }

    public void setManufacturerUrl(String manufacturerUrl) {
        this.manufacturerUrl = manufacturerUrl;
    }

    public String getManufacturerModelNumber() {
        return manufacturerModelNumber;
    }

    public void setManufacturerModelNumber(String manufacturerModelNumber) {
        this.manufacturerModelNumber = manufacturerModelNumber;
    }

    public String getManufacturedDate() {
        return manufacturedDate;
    }

    public void setManufacturedDate(String manufacturedDate) {
        this.manufacturedDate = manufacturedDate;
    }

    public String getManufacturerPlatformVersion() {
        return manufacturerPlatformVersion;
    }

    public void setManufacturerPlatformVersion(String manufacturerPlatformVersion) {
        this.manufacturerPlatformVersion = manufacturerPlatformVersion;
    }

    public String getManufacturerOsVersion() {
        return manufacturerOsVersion;
    }

    public void setManufacturerOsVersion(String manufacturerOsVersion) {
        this.manufacturerOsVersion = manufacturerOsVersion;
    }

    public String getManufacturerHwVersion() {
        return manufacturerHwVersion;
    }

    public void setManufacturerHwVersion(String manufacturerHwVersion) {
        this.manufacturerHwVersion = manufacturerHwVersion;
    }

    public String getManufacturerFwVersion() {
        return manufacturerFwVersion;
    }

    public void setManufacturerFwVersion(String manufacturerFwVersion) {
        this.manufacturerFwVersion = manufacturerFwVersion;
    }

    public String getManufacturerSupportUrl() {
        return manufacturerSupportUrl;
    }

    public void setManufacturerSupportUrl(String manufacturerSupportUrl) {
        this.manufacturerSupportUrl = manufacturerSupportUrl;
    }

    public String getManufacturerSystemTime() {
        return manufacturerSystemTime;
    }

    public void setManufacturerSystemTime(String manufacturerSystemTime) {
        this.manufacturerSystemTime = manufacturerSystemTime;
    }

    public String getManufacturerSerialNumber() {
        return manufacturerSerialNumber;
    }

    public void setManufacturerSerialNumber(String manufacturerSerialNumber) {
        this.manufacturerSerialNumber = manufacturerSerialNumber;
    }

    /**
     * Required ["pi", "mnmn"]
     */

    public void setOCRepresentation(OCRepresentation rep) {
        /* pi */
        String pi = OCRep.getString(rep, OcfResourceAttributeKey.PLATFORM_ID_KEY);
        this.setPlatformId(pi);
        /* mnfv */
        String mnfv = OCRep.getString(rep, OcfResourceAttributeKey.MAN_FW_VER_KEY);
        this.setManufacturerFwVersion(mnfv);
        /* vid */
        String vid = OCRep.getString(rep, OcfResourceAttributeKey.MAN_INFO);
        this.setManufacturerInfo(vid);
        /* mnmn */
        String mnmn = OCRep.getString(rep, OcfResourceAttributeKey.MAN_NAME_KEY);
        this.setManufacturerName(mnmn);
        /* mnmo */
        String mnmo = OCRep.getString(rep, OcfResourceAttributeKey.MAN_MODEL_NO_KEY);
        this.setManufacturerModelNumber(mnmo);
        /* mnhw */
        String mnhw = OCRep.getString(rep, OcfResourceAttributeKey.MAN_HW_VER_KEY);
        this.setManufacturerHwVersion(mnhw);
        /* mnos */
        String mnos = OCRep.getString(rep, OcfResourceAttributeKey.MAN_OS_VER_KEY);
        this.setManufacturerOsVersion(mnos);
        /* mndt */
        String mndt = OCRep.getString(rep, OcfResourceAttributeKey.MAN_DATE_KEY);
        this.setManufacturedDate(mndt);
        /* mnsl */
        String mnsl = OCRep.getString(rep, OcfResourceAttributeKey.MAN_SUPPORT_URL_KEY);
        this.setManufacturerSupportUrl(mnsl);
        /* mnpv */
        String mnpv = OCRep.getString(rep, OcfResourceAttributeKey.MAN_PLATFORM_VER_KEY);
        this.setManufacturerPlatformVersion(mnpv);
        /* st */
        String st = OCRep.getString(rep, OcfResourceAttributeKey.MAN_SYSTEM_TIME_KEY);
        this.setManufacturerSystemTime(st);
        /* mnml */
        String mnml = OCRep.getString(rep, OcfResourceAttributeKey.MAN_URL_KEY);
        this.setManufacturerUrl(mnml);
        /* mnsel */
        String mnsel = OCRep.getString(rep, OcfResourceAttributeKey.MAN_SERIAL_NO);
        this.setManufacturerSerialNumber(mnsel);

        /* id */
        String id = OCRep.getString(rep, OcfResourceAttributeKey.ID_KEY);
        this.setId(id);
        /* n */
        String n = OCRep.getString(rep, OcfResourceAttributeKey.NAME_KEY);
        this.setName(n);
        /* rt */
        String[] resourceTypes = OCRep.getStringArray(rep, OcfResourceAttributeKey.RESOURCE_TYPES_KEY);
        this.setResourceTypes(resourceTypes != null ? Arrays.asList(resourceTypes) : null);
        /* if */
        String[] interfaces = OCRep.getStringArray(rep, OcfResourceAttributeKey.INTERFACES_KEY);
        this.setInterfaces(interfaces != null ? Arrays.asList(interfaces) : null);
    }

    @Override
    public String toString() {
        return "\t" + OcfResourceAttributeKey.PLATFORM_ID_KEY + ": " + platformId
                + "\n\t" + OcfResourceAttributeKey.MAN_NAME_KEY + ": " + manufacturerName
                + "\n\t" + OcfResourceAttributeKey.MAN_INFO + ": " + manufacturerInfo
                + "\n\t" + OcfResourceAttributeKey.MAN_URL_KEY + ": " + manufacturerUrl
                + "\n\t" + OcfResourceAttributeKey.MAN_MODEL_NO_KEY + ": " + manufacturerModelNumber
                + "\n\t" + OcfResourceAttributeKey.MAN_DATE_KEY + ": " + manufacturedDate
                + "\n\t" + OcfResourceAttributeKey.MAN_PLATFORM_VER_KEY + ": " + manufacturerPlatformVersion
                + "\n\t" + OcfResourceAttributeKey.MAN_OS_VER_KEY + ": " + manufacturerOsVersion
                + "\n\t" + OcfResourceAttributeKey.MAN_HW_VER_KEY + ": " + manufacturerHwVersion
                + "\n\t" + OcfResourceAttributeKey.MAN_FW_VER_KEY + ": " + manufacturerFwVersion
                + "\n\t" + OcfResourceAttributeKey.MAN_SUPPORT_URL_KEY + ": " + manufacturerSupportUrl
                + "\n\t" + OcfResourceAttributeKey.MAN_SYSTEM_TIME_KEY + ": " + manufacturerSystemTime
                + "\n\t" + OcfResourceAttributeKey.MAN_SERIAL_NO + ": " + manufacturerSerialNumber;
    }
}
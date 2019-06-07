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

package org.openconnectivity.otgc.domain.model.resource.secure.cred;

import com.upokecenter.cbor.CBORObject;

import org.iotivity.CborEncoder;
import org.iotivity.OCRep;
import org.iotivity.OCRepresentation;
import org.openconnectivity.otgc.utils.constant.OcfCredType;
import org.openconnectivity.otgc.utils.constant.OcfCredUsage;
import org.openconnectivity.otgc.utils.constant.OcfResourceAttributeKey;

public class OcCredential {

    private Long credid;
    private String subjectuuid;
    private OcCredRole roleid;
    private OcfCredType credtype;
    private OcfCredUsage credusage;
    private OcCredPublicData publicData;
    private OcCredPrivateData privateData;
    private OcCredOptionalData optionalData;
    private String period;

    public OcCredential() {}

    public Long getCredid() {
        return credid;
    }

    public void setCredid(Long credid) {
        this.credid = credid;
    }

    public String getSubjectuuid() {
        return subjectuuid;
    }

    public void setSubjectuuid(String subjectuuid) {
        this.subjectuuid = subjectuuid;
    }

    public OcCredRole getRoleid() {
        return roleid;
    }

    public void setRoleid(OcCredRole roleid) {
        this.roleid = roleid;
    }

    public OcfCredType getCredtype() {
        return credtype;
    }

    public void setCredtype(OcfCredType credtype) {
        this.credtype = credtype;
    }

    public OcfCredUsage getCredusage() {
        return credusage;
    }

    public void setCredusage(OcfCredUsage credusage) {
        this.credusage = credusage;
    }

    public OcCredPublicData getPublicData() {
        return publicData;
    }

    public void setPublicData(OcCredPublicData publicData) {
        this.publicData = publicData;
    }

    public OcCredPrivateData getPrivateData() {
        return privateData;
    }

    public void setPrivateData(OcCredPrivateData privateData) {
        this.privateData = privateData;
    }

    public OcCredOptionalData getOptionalData() {
        return optionalData;
    }

    public void setOptionalData(OcCredOptionalData optionalData) {
        this.optionalData = optionalData;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public void parseCbor(CBORObject cbor) {
        /* credid */
        CBORObject credidObj = cbor.get(OcfResourceAttributeKey.CRED_ID_KEY);
        if (credidObj != null) {
            Long credid = credidObj.AsInt64();
            this.setCredid(credid);
        }
        /* credtype */
        CBORObject credtypeObj = cbor.get(OcfResourceAttributeKey.CRED_TYPE_KEY);
        if (credtypeObj != null) {
            Long credtype = credtypeObj.AsInt64();
            this.setCredtype(OcfCredType.valueToEnum(credtype.intValue()));
        }
        /* credusage */
        CBORObject credUsageObj = cbor.get(OcfResourceAttributeKey.CRED_USAGE_KEY);
        if (credUsageObj != null) {
            String credusage = credUsageObj.AsString();
            this.setCredusage(credusage != null ? OcfCredUsage.valueToEnum(credusage) : null);
        }
        /* subjectuuid */
        CBORObject subjectuuidObj = cbor.get(OcfResourceAttributeKey.SUBJECTUUID_KEY);
        if (subjectuuidObj != null) {
            String subjectuuid = subjectuuidObj.AsString();
            this.setSubjectuuid(subjectuuid);
        }
        /* period */
        CBORObject periodObj = cbor.get(OcfResourceAttributeKey.PERIOD_KEY);
        if (periodObj != null) {
            String period = periodObj.AsString();
            this.setPeriod(period);
        }
        /* publicdata */
        CBORObject publicdataObj = cbor.get(OcfResourceAttributeKey.PUBLIC_DATA_KEY);
        if (publicdataObj != null) {
            OcCredPublicData publicData = new OcCredPublicData();
            publicData.parseCbor(publicdataObj);
            this.setPublicData(publicData);
        }
        /* optionaldata */
        // TODO:

        /* roleid */
        CBORObject roleidObj = cbor.get(OcfResourceAttributeKey.ROLE_ID_KEY);
        if (roleidObj != null) {
            OcCredRole roleid = new OcCredRole();
            roleid.parseCbor(roleidObj);
            this.setRoleid(roleid);
        }
    }

    public void parseOCRepresentation(OCRepresentation rep) {
        /* credid */
        Long credid = OCRep.getLong(rep, OcfResourceAttributeKey.CRED_ID_KEY);
        this.setCredid(credid);
        /* credtype */
        Long credtype = OCRep.getLong(rep, OcfResourceAttributeKey.CRED_TYPE_KEY);
        this.setCredtype(OcfCredType.valueToEnum(credtype.intValue()));
        /* credusage */
        String credusage = OCRep.getString(rep, OcfResourceAttributeKey.CRED_USAGE_KEY);
        this.setCredusage(credusage != null ? OcfCredUsage.valueToEnum(credusage) : null);
        /* subjectuuid */
        String subjectuuid = OCRep.getString(rep, OcfResourceAttributeKey.SUBJECTUUID_KEY);
        this.setSubjectuuid(subjectuuid);
        /* period */
        String period = OCRep.getString(rep, OcfResourceAttributeKey.PERIOD_KEY);
        this.setPeriod(period);
        /* publicdata */
        OCRepresentation publicdataObj = OCRep.getObject(rep, OcfResourceAttributeKey.PUBLIC_DATA_KEY);
        if (publicData != null) {
            OcCredPublicData publicData = new OcCredPublicData();
            publicData.parseOCRepresentation(publicdataObj);
            this.setPublicData(publicData);
        }
        /* optionaldata */
        OCRepresentation optionaldataObj = OCRep.getObject(rep, OcfResourceAttributeKey.OPTIONAL_DATA_KEY);
        if (optionalData != null) {
            OcCredOptionalData optionalData = new OcCredOptionalData();
            optionalData.parseOCRepresentation(optionaldataObj);
            this.setOptionalData(optionalData);
        }
        /* roleid */
        OCRepresentation roleidObj = OCRep.getObject(rep, OcfResourceAttributeKey.ROLE_ID_KEY);
        if (roleidObj != null) {
            OcCredRole roleid = new OcCredRole();
            roleid.parseOCRepresentation(roleidObj);
            this.setRoleid(roleid);
        }
    }

    public void parseToCbor(CborEncoder parent) {
        if (this.getCredid() != null) {
            OCRep.setLong(parent, OcfResourceAttributeKey.CRED_ID_KEY, this.getCredid());
        }

        if (this.getSubjectuuid() != null && !this.getSubjectuuid().isEmpty()) {
            OCRep.setTextString(parent, OcfResourceAttributeKey.SUBJECTUUID_KEY, this.getSubjectuuid());
        }

        if (this.getRoleid() != null) {
            CborEncoder roleId = OCRep.openObject(parent, OcfResourceAttributeKey.ROLE_ID_KEY);
            this.getRoleid().parseToCbor(roleId);
            OCRep.closeObject(parent, roleId);
        }

        if (this.getCredtype() != null) {
            OCRep.setLong(parent, OcfResourceAttributeKey.CRED_TYPE_KEY, this.getCredtype().getValue());
        }

        if (this.getCredusage() != null) {
            OCRep.setTextString(parent, OcfResourceAttributeKey.CRED_USAGE_KEY, this.getCredusage().getValue());
        }

        if (this.getPublicData() != null) {
            CborEncoder publicObj = OCRep.openObject(parent, OcfResourceAttributeKey.PUBLIC_DATA_KEY);
            this.getPublicData().parseToCbor(publicObj);
            OCRep.closeObject(parent, publicObj);
        }

        if (this.getPrivateData() != null) {
            CborEncoder privateObj = OCRep.openObject(parent, OcfResourceAttributeKey.PRIVATE_DATA_KEY);
            this.getPrivateData().parseToCbor(privateObj);
            OCRep.closeObject(parent, privateObj);
        }

        if (this.getOptionalData() != null) {
            // TODO
        }

        if (this.getPeriod() != null && !this.getPeriod().isEmpty()) {
            // TODO
        }
    }
}

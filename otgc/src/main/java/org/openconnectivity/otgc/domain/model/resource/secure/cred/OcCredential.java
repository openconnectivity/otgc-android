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

import org.iotivity.OCCred;
import org.iotivity.OCCredUtil;
import org.iotivity.OCUuidUtil;

public class OcCredential {

    private Integer credid;
    private String subjectuuid;
    private OcCredRole roleid;
    private String credtype;
    private String credusage;
    private OcCredPublicData publicData;
    private OcCredPrivateData privateData;
    private OcCredOptionalData optionalData;
    private String period;

    public OcCredential() {}

    public Integer getCredid() {
        return credid;
    }

    public void setCredid(Integer credid) {
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

    public String getCredtype() {
        return credtype;
    }

    public void setCredtype(String credtype) {
        this.credtype = credtype;
    }

    public String getCredusage() {
        return credusage;
    }

    public void setCredusage(String credusage) {
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

    public void parseOCRepresentation(OCCred cred) {
        /* credid */
        Integer credid = cred.getCredId();
        this.setCredid(credid);
        /* credtype */
        String credtype = OCCredUtil.credTypeString(cred.getCredType());
        this.setCredtype(credtype);
        /* credusage */
        String credusage = OCCredUtil.readCredUsage(cred.getCredUsage());
        this.setCredusage(credusage);
        /* subjectuuid */
        String subjectuuid = OCUuidUtil.uuidToString(cred.getSubjectUuid());
        this.setSubjectuuid(subjectuuid);
        /* publicdata */
        if (cred.getPublicData() != null &&
                cred.getPublicData().getData() != null &&
                !cred.getPublicData().getData().isEmpty()) {
            OcCredPublicData publicData = new OcCredPublicData();
            publicData.parseOCRepresentation(cred.getPublicData());
            this.setPublicData(publicData);
        }
        /* privatedata */
        OcCredPrivateData privateData = new OcCredPrivateData();
        privateData.parseOCRepresentation(cred.getPrivateData());
        this.setPrivateData(privateData);
        /* roleid */
        if (cred.getRole() != null) {
            OcCredRole roleid = new OcCredRole();
            if (!cred.getRole().isEmpty()) {
                roleid.setRole(cred.getRole());
            }

            if (cred.getAuthority() != null && !cred.getAuthority().isEmpty()) {
                roleid.setAuthority(cred.getAuthority());
            }
            this.setRoleid(roleid);
        }
    }
}

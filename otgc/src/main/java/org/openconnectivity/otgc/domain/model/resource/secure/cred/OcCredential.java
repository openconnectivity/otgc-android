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

    private OCCred credential;
    private OcCredRole roleid;
    private OcCredPublicData publicData;
    private OcCredPrivateData privateData;
    private OcCredOptionalData optionalData;

    public OcCredential(OCCred credential) {
        this.credential = credential;
        parseOCRepresentation();
    }

    public int getCredid() {
        return credential.getCredId();
    }

    public String getSubjectuuid() {
        return OCUuidUtil.uuidToString(credential.getSubjectUuid());
    }

    public OcCredRole getRoleid() {
        return roleid;
    }

    private void setRoleid(OcCredRole roleid) {
        this.roleid = roleid;
    }

    public String getCredtype() {
        return OCCredUtil.credTypeString(credential.getCredType());
    }

    public String getCredusage() {
        return OCCredUtil.readCredUsage(credential.getCredUsage());
    }

    public OcCredPublicData getPublicData() {
        return publicData;
    }

    private void setPublicData(OcCredPublicData publicData) {
        this.publicData = publicData;
    }

    public OcCredPrivateData getPrivateData() {
        return privateData;
    }

    private void setPrivateData(OcCredPrivateData privateData) {
        this.privateData = privateData;
    }

    public OcCredOptionalData getOptionalData() {
        return optionalData;
    }

    public void setOptionalData(OcCredOptionalData optionalData) {
        this.optionalData = optionalData;
    }


    public void parseOCRepresentation() {
        /* publicdata */
        if (credential.getPublicData() != null &&
                credential.getPublicData().getData() != null &&
                !credential.getPublicData().getData().isEmpty()) {
            OcCredPublicData publicData = new OcCredPublicData(credential.getPublicData());
            this.setPublicData(publicData);
        }
        /* privatedata */
        OcCredPrivateData privateData = new OcCredPrivateData(credential.getPrivateData());
        this.setPrivateData(privateData);
        /* roleid */
        if (credential.getRole() != null) {
            OcCredRole roleid = new OcCredRole();
            if (!credential.getRole().isEmpty()) {
                roleid.setRole(credential.getRole());
            }

            if (credential.getAuthority() != null && !credential.getAuthority().isEmpty()) {
                roleid.setAuthority(credential.getAuthority());
            }
            this.setRoleid(roleid);
        }
    }
}

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

package org.openconnectivity.otgc.domain.model.resource.secure.acl;

import org.iotivity.OCAceConnectionType;
import org.iotivity.OCAceSubject;
import org.iotivity.OCAceSubjectType;
import org.iotivity.OCUuidUtil;

public class OcAceSubject {

    private String type;
    private String connType;
    private String uuid;
    private String roleId;
    private String authority;

    public OcAceSubject() {

    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getConnType() {
        return connType;
    }

    public void setConnType(String connType) {
        this.connType = connType;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public void parseOCRepresentation(OCAceSubjectType subjectType, OCAceSubject subject) {
        this.setType(subjectType.toString());
        if (subjectType == OCAceSubjectType.OC_SUBJECT_UUID) {
            this.setUuid(OCUuidUtil.uuidToString(subject.getUuid()));
        } else if (subjectType == OCAceSubjectType.OC_SUBJECT_ROLE) {
            this.setRoleId(subject.getRole());
            if (subject.getAuthority() != null && !subject.getAuthority().isEmpty()) {
                this.setAuthority(subject.getAuthority());
            }
        } else if (subjectType == OCAceSubjectType.OC_SUBJECT_CONN) {
            if (subject.getConn() == OCAceConnectionType.OC_CONN_AUTH_CRYPT) {
                this.setConnType("auth-crypt");
            } else {
                this.setConnType("anon-clear");
            }
        }
    }
}

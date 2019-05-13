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

import org.iotivity.CborEncoder;
import org.iotivity.OCRep;
import org.iotivity.OCRepresentation;
import org.openconnectivity.otgc.utils.constant.OcfResourceAttributeKey;

public class OcAceSubject {

    private OcAceSubjectType type;
    private String connType;
    private String uuid;
    private String roleId;
    private String authority;

    public OcAceSubject() {

    }

    public OcAceSubjectType getType() {
        return type;
    }

    public void setType(OcAceSubjectType type) {
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

    public void parseOCRepresentation(OCRepresentation rep) {
        while (rep != null) {
            switch (rep.getType()) {
                case OC_REP_STRING:
                    if (rep.getName().equals(OcfResourceAttributeKey.UUID_TYPE_KEY)) {
                        this.setType(OcAceSubjectType.UUID_TYPE);
                        this.setUuid(rep.getValue().getString());
                    } else if (rep.getName().equals(OcfResourceAttributeKey.CONN_TYPE_KEY)) {
                        this.setType(OcAceSubjectType.CONN_TYPE);
                        this.setConnType(rep.getValue().getString());
                    } else if (rep.getName().equals(OcfResourceAttributeKey.ROLE_AUTHORITY_KEY) || rep.getName().equals(OcfResourceAttributeKey.ROLE_KEY)) {
                        this.setType(OcAceSubjectType.ROLE_TYPE);
                        if (rep.getName().equals(OcfResourceAttributeKey.ROLE_KEY)) {
                            this.setRoleId(rep.getValue().getString());
                        } else {
                            this.setAuthority(rep.getValue().getString());
                        }
                    }
                    break;
                default:
                    break;
            }

            rep = rep.getNext();
        }
    }

    public void parseToCbor(CborEncoder parent) {
        /* subject */
        if (this.getType().equals(OcAceSubjectType.UUID_TYPE)) {
            OCRep.setTextString(parent, OcfResourceAttributeKey.UUID_TYPE_KEY, this.getUuid());
        } else if (this.getType().equals(OcAceSubjectType.CONN_TYPE)) {
            OCRep.setTextString(parent, OcfResourceAttributeKey.CONN_TYPE_KEY, this.getConnType());
        } else if (this.getType().equals(OcAceSubjectType.ROLE_TYPE)) {
            OCRep.setTextString(parent, OcfResourceAttributeKey.ROLE_KEY, this.getRoleId());
            OCRep.setTextString(parent, OcfResourceAttributeKey.ROLE_AUTHORITY_KEY, this.getAuthority());
        }
    }
}

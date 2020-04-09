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

    OCAceSubjectType subjectType;
    OCAceSubject subject;

    public OcAceSubject(OCAceSubjectType subjectType, OCAceSubject subject) {
        this.subjectType = subjectType;
        this.subject = subject;
    }

    public String getType() {
        return subjectType.toString();
    }

    public String getUuid() {
        if (subjectType == OCAceSubjectType.OC_SUBJECT_UUID) {
            return OCUuidUtil.uuidToString(subject.getUuid());
        } else {
            return null;
        }
    }

    public String getConnType() {
        if (subjectType == OCAceSubjectType.OC_SUBJECT_CONN) {
            if (subject.getConn() == OCAceConnectionType.OC_CONN_AUTH_CRYPT) {
                return "auth-crypt";
            } else {
                return "anon-clear";
            }
        } else {
            return null;
        }
    }

    public String getRoleId() {
        if (subjectType == OCAceSubjectType.OC_SUBJECT_ROLE) {
            return subject.getRole();
        } else {
            return null;
        }
    }

    public String getAuthority() {
        return subject.getAuthority();
    }
}

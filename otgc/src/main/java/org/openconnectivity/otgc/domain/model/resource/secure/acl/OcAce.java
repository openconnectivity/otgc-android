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

import java.util.ArrayList;
import java.util.List;

public class OcAce {

    private Long aceid;
    private Long permission;
    private OcAceSubject subject;
    private List<OcAceResource> resources;

    public OcAce() {
        this.resources = new ArrayList<>();
    }

    public Long getAceid() {
        return aceid;
    }

    public void setAceid(Long aceid) {
        this.aceid = aceid;
    }

    public Long getPermission() {
        return permission;
    }

    public void setPermission(Long permission) {
        this.permission = permission;
    }

    public OcAceSubject getSubject() {
        return subject;
    }

    public void setSubject(OcAceSubject subject) {
        this.subject = subject;
    }

    public List<OcAceResource> getResources() {
        return resources;
    }

    public void setResources(List<OcAceResource> resources) {
        this.resources = resources;
    }

    public void parseOCRepresentation(OCRepresentation rep) {
        /* aceid */
        Long aceid = OCRep.getLong(rep, OcfResourceAttributeKey.ACE_ID_KEY);
        this.setAceid(aceid);
        /* permission */
        Long permission = OCRep.getLong(rep, OcfResourceAttributeKey.PERMISSION_KEY);
        this.setPermission(permission);
        /* subject */
        OCRepresentation subjectObj = OCRep.getObject(rep, OcfResourceAttributeKey.SUBJECT_KEY);
        OcAceSubject subject = new OcAceSubject();
        subject.parseOCRepresentation(subjectObj);
        this.setSubject(subject);
        /* resources */
        OCRepresentation resourcesObj = OCRep.getObjectArray(rep, OcfResourceAttributeKey.RESOURCES_KEY);
        List<OcAceResource> resources = new ArrayList<>();
        while (resourcesObj != null) {
            OcAceResource resource = new OcAceResource();
            resource.parseOCRepresentation(resourcesObj.getValue().getObject());
            resources.add(resource);
            resourcesObj = resourcesObj.getNext();
        }
        this.setResources(resources);
    }

    public void parseToCbor(CborEncoder aclist2) {
        CborEncoder aceObj = OCRep.beginObject(aclist2);

        /* aceid */
        if (this.getAceid() !=  null) {
            OCRep.setLong(aceObj, OcfResourceAttributeKey.ACE_ID_KEY, this.getAceid());
        }
        /* subject */
        if (this.getSubject() != null) {
            CborEncoder subjectObj = OCRep.openObject(aceObj, OcfResourceAttributeKey.SUBJECT_KEY);
            this.getSubject().parseToCbor(subjectObj);
            OCRep.closeObject(aceObj, subjectObj);
        }
        /* resources */
        if (this.getResources() != null) {
            CborEncoder resArray = OCRep.openArray(aceObj, OcfResourceAttributeKey.RESOURCES_KEY);
            for (OcAceResource res : this.getResources()) {
                res.parseToCbor(resArray);
            }
            OCRep.closeArray(aceObj, resArray);
        }
        /* permission */
        if (this.getPermission() != 0) {
            OCRep.setLong(aceObj, OcfResourceAttributeKey.PERMISSION_KEY, this.getPermission());
        }

        OCRep.closeObject(aclist2, aceObj);
    }
}

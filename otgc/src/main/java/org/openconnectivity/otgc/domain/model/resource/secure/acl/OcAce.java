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

import org.iotivity.OCAceResource;
import org.iotivity.OCSecurityAce;

import java.util.ArrayList;
import java.util.List;

public class OcAce {

    private Integer aceid;
    private Integer permission;
    private OcAceSubject subject;
    private List<OcAceResource> resources;

    public OcAce() {
        this.resources = new ArrayList<>();
    }

    public Integer getAceid() {
        return aceid;
    }

    public void setAceid(Integer aceid) {
        this.aceid = aceid;
    }

    public Integer getPermission() {
        return permission;
    }

    public void setPermission(Integer permission) {
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

    public void parseOCRepresentation(OCSecurityAce ace) {
        /* aceid */
        Integer aceid = ace.getAceid();
        this.setAceid(aceid);
        /* permission */
        Integer permission = ace.getPermission();
        this.setPermission(permission);
        /* subject */
        OcAceSubject subject = new OcAceSubject();
        subject.parseOCRepresentation(ace.getSubjectType(), ace.getSubject());
        this.setSubject(subject);
        /* resources */
        OCAceResource res = ace.getResourcesListHead();
        List<OcAceResource> resources = new ArrayList<>();
        while (res != null) {
            OcAceResource resource = new OcAceResource();
            resource.parseOCRepresentation(res);
            resources.add(resource);

            res = res.getNext();
        }
        this.setResources(resources);
    }
}

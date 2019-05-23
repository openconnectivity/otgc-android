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

package org.openconnectivity.otgc.domain.model.resource.virtual.res;

import org.iotivity.OCRep;
import org.iotivity.OCRepresentation;
import org.openconnectivity.otgc.domain.model.resource.OcResourceBase;
import org.openconnectivity.otgc.utils.constant.OcfResourceAttributeKey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OcResource extends OcResourceBase {

    private String anchor;
    private String href;
    private Long propertiesMask;
    private List<OcEndpoint> endpoints;

    public OcResource() {

    }

    public String getAnchor() {
        return anchor;
    }

    public void setAnchor(String anchor) {
        this.anchor = anchor;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public Long getPropertiesMask() {
        return propertiesMask;
    }

    public void setPropertiesMask(Long propertiesMask) {
        this.propertiesMask = propertiesMask;
    }

    public List<OcEndpoint> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(List<OcEndpoint> endpoints) {
        this.endpoints = endpoints;
    }

    public void parseOCRepresentation(OCRepresentation rep) {
        /* anchor */
        String anchor = OCRep.getString(rep, OcfResourceAttributeKey.ANCHOR_KEY);
        this.setAnchor(anchor);
        /* href */
        String href = OCRep.getString(rep, OcfResourceAttributeKey.HREF_RES_KEY);
        this.setHref(href);
        /* p.bm */
        OCRepresentation pObj = OCRep.getObject(rep, OcfResourceAttributeKey.PROPERTIES_KEY);
        Long bm = OCRep.getLong(pObj, OcfResourceAttributeKey.FRAMEWORK_POLICIES_KEY);
        this.setPropertiesMask(bm);
        /* eps */
        OCRepresentation epsObjArray = OCRep.getObjectArray(rep, OcfResourceAttributeKey.ENDPOINTS_KEY);
        List<OcEndpoint> endpointList = new ArrayList<>();
        while (epsObjArray != null) {
            OcEndpoint endpoint = new OcEndpoint();
            endpoint.parseOCRepresentation(epsObjArray.getValue().getObject());
            endpointList.add(endpoint);

            epsObjArray = epsObjArray.getNext();
        }
        this.setEndpoints(endpointList);

        /* rt */
        String[] resourceTypes = OCRep.getStringArray(rep, OcfResourceAttributeKey.RESOURCE_TYPES_KEY);
        this.setResourceTypes(resourceTypes != null ? Arrays.asList(resourceTypes): null);
        /* if */
        String[] interfaces = OCRep.getStringArray(rep, OcfResourceAttributeKey.INTERFACES_KEY);
        this.setInterfaces(interfaces != null ? Arrays.asList(interfaces) : null);
    }
}

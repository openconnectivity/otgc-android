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
import java.util.Arrays;
import java.util.List;

public class OcAceResource {

    private String href;
    private String wc;
    private List<String> resourceTypes;
    private List<String> interfaces;

    public OcAceResource() {
        resourceTypes = new ArrayList<>();
        interfaces = new ArrayList<>();
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getWildcard() {
        return wc;
    }

    public void setWildCard(String wc) {
        this.wc = wc;
    }

    public List<String> getResourceTypes() {
        return resourceTypes;
    }

    public void setResourceTypes(List<String> resourceTypes) {
        this.resourceTypes = resourceTypes;
    }

    public List<String> getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(List<String> interfaces) {
        this.interfaces = interfaces;
    }

    public void parseOCRepresentation(OCRepresentation rep) {
        /* href */
        String href = OCRep.getString(rep, OcfResourceAttributeKey.HREF_KEY);
        this.setHref(href);
        /* wc */
        String wc = OCRep.getString(rep, OcfResourceAttributeKey.WILDCARD_KEY);
        this.setWildCard(wc);
        /* rt */
        String[] resourceTypes = OCRep.getStringArray(rep, OcfResourceAttributeKey.RESOURCE_TYPES_KEY);
        this.setResourceTypes(resourceTypes != null ? Arrays.asList(resourceTypes) : null);
        /* if */
        String[] interfaces = OCRep.getStringArray(rep, OcfResourceAttributeKey.INTERFACES_KEY);
        this.setInterfaces(interfaces != null ? Arrays.asList(interfaces) : null);
    }

    public void parseToCbor(CborEncoder resArray) {
        CborEncoder resObj = OCRep.beginObject(resArray);

        /* href */
        if (this.getHref() != null && !this.getHref().isEmpty()) {
            OCRep.setTextString(resObj, OcfResourceAttributeKey.HREF_KEY, this.getHref());
        }
        /* wc */
        if (this.getWildcard() != null && !this.getWildcard().isEmpty()) {
            OCRep.setTextString(resObj, OcfResourceAttributeKey.WILDCARD_KEY, this.getWildcard());
        }
        /* rt */
        if (this.getResourceTypes() != null && !this.getResourceTypes().isEmpty()) {
            CborEncoder resourceType = OCRep.openArray(resObj, OcfResourceAttributeKey.RESOURCE_TYPES_KEY);
            for (String rtStr : this.getResourceTypes()) {
                OCRep.addTextString(resourceType, rtStr);
            }
            OCRep.closeArray(resObj, resourceType);
        }
        /* if */
        if (this.getInterfaces() != null && !this.getInterfaces().isEmpty()) {
            CborEncoder interfaces = OCRep.openArray(resObj, OcfResourceAttributeKey.INTERFACES_KEY);
            for (String ifStr : this.getInterfaces()) {
                OCRep.addTextString(interfaces, ifStr);
            }
            OCRep.closeArray(resObj, interfaces);
        }

        OCRep.closeObject(resArray, resObj);
    }
}

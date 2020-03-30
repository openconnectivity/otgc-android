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

import java.util.ArrayList;
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

    public void parseOCRepresentation(OCAceResource res) {
        if (res.getHref() != null && !res.getHref().isEmpty()) {
            /* href */
            String href = res.getHref();
            this.setHref(href);
        } else if (res.getWildcard() != null) {
            /* wc */
            switch (res.getWildcard()) {
                case OC_ACE_WC_ALL:
                    this.setWildCard("*");
                    break;
                case OC_ACE_WC_ALL_SECURED:
                    this.setWildCard("+");
                    break;
                case OC_ACE_WC_ALL_PUBLIC:
                    this.setWildCard("-");
                    break;
                default:
                    break;
            }
        }
    }
}

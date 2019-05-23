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

package org.openconnectivity.otgc.domain.model.resource.introspection;

import org.iotivity.OCRep;
import org.iotivity.OCRepresentation;
import org.openconnectivity.otgc.domain.model.resource.OcResourceBase;
import org.openconnectivity.otgc.utils.constant.OcfResourceAttributeKey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OcIntrospection extends OcResourceBase {

    private List<OcIntrospectionUrlInfo> urlInfo;

    public OcIntrospection() {
    }

    public List<OcIntrospectionUrlInfo> getUrlInfo() {
        return urlInfo;
    }

    public void setUrlInfo(List<OcIntrospectionUrlInfo> urlInfo) {
        this.urlInfo = urlInfo;
    }

    /**
     * Required ["urlInfo"]
     */

    public void parseOCRepresentation(OCRepresentation rep) {
        /* urlinfo */
        OCRepresentation urlInfoObjArray = OCRep.getObjectArray(rep, OcfResourceAttributeKey.URL_INFO_KEY);
        List<OcIntrospectionUrlInfo> urlInfoList = new ArrayList<>();
        while (urlInfoObjArray != null) {
            OcIntrospectionUrlInfo urlInfo = new OcIntrospectionUrlInfo();
            urlInfo.parseOCRepresentation(urlInfoObjArray.getValue().getObject());
            urlInfoList.add(urlInfo);

            urlInfoObjArray = urlInfoObjArray.getNext();
        }
        this.setUrlInfo(urlInfoList);

        /* id */
        String id = OCRep.getString(rep, OcfResourceAttributeKey.ID_KEY);
        this.setId(id);
        /* n */
        String n = OCRep.getString(rep, OcfResourceAttributeKey.NAME_KEY);
        this.setName(n);
        /* rt */
        String[] resourceTypes = OCRep.getStringArray(rep, OcfResourceAttributeKey.RESOURCE_TYPES_KEY);
        this.setResourceTypes(resourceTypes != null ? Arrays.asList(resourceTypes) : null);
        /* if */
        String[] interfaces = OCRep.getStringArray(rep, OcfResourceAttributeKey.INTERFACES_KEY);
        this.setInterfaces(interfaces != null ? Arrays.asList(interfaces) : null);
    }

    public OcIntrospectionUrlInfo getCoapsIpv6Endpoint() {
        OcIntrospectionUrlInfo url = null;
        for (OcIntrospectionUrlInfo urlInfo : this.getUrlInfo()) {
            if (urlInfo.getProtocol().equals("coaps")
                    && !urlInfo.getHost().contains(".")) {
                return urlInfo;
            } else {
                url = urlInfo;
            }
        }

        return url;
    }
}

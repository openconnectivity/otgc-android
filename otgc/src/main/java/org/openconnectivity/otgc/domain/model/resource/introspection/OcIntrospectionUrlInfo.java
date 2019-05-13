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
import org.openconnectivity.otgc.utils.constant.OcfResourceAttributeKey;

public class OcIntrospectionUrlInfo {

    private String host;
    private String uri;
    private String protocol;
    private String contentType;
    private Long version;

    public OcIntrospectionUrlInfo() {
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    private void separateUrlInHostAndUri(String url) {
        int separatorIndex = url.indexOf('/', 8);
        host = url.substring(0, separatorIndex);
        uri = url.substring(separatorIndex);
    }

    /**
     * Required ["url", "protocol"]
     */

    public void parseOCRepresentation(OCRepresentation rep) {
        /* url */
        String url = OCRep.getString(rep, OcfResourceAttributeKey.URL_KEY);
        this.separateUrlInHostAndUri(url);
        /* protocol */
        String protocol = OCRep.getString(rep, OcfResourceAttributeKey.PROTOCOL_KEY);
        this.setProtocol(protocol);
        /* content-type */
        String contentType = OCRep.getString(rep, OcfResourceAttributeKey.CONTENT_TYPE_KEY);
        this.setContentType(contentType);
        /* version */
        Long version = OCRep.getLong(rep, OcfResourceAttributeKey.VERSION_KEY);
        this.setVersion(version);
    }
}

/*
 * *****************************************************************
 *
 *  Copyright 2018 DEKRA Testing and Certification, S.A.U. All Rights Reserved.
 *
 *  ******************************************************************
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  ******************************************************************
 */

package org.openconnectivity.otgc.client.domain.model;

import org.iotivity.base.OcException;
import org.iotivity.base.OcRepresentation;

public class OcIntrospectionUrlInfo {
    private static final String URL_KEY = "url";
    private static final String PROTOCOL_KEY = "protocol";
    private static final String CONTENT_TYPE_KEY = "content-type";
    private static final String VERSION_KEY = "version";

    private String mHost;
    private String mUri;
    private String mProtocol;
    private String mContentType;
    private int mVersion;

    public OcIntrospectionUrlInfo() {
        mHost = "";
        mUri = "";
        mProtocol = "";
        mContentType = "";
        mVersion = 0;
    }

    public void setOcRepresentation(OcRepresentation ocRepresentation) throws IntrospectionException {
        try {
            separateUrlInHostAndUri(ocRepresentation.getValue(URL_KEY));
        } catch (OcException e) {
            throw new IntrospectionException(e);
        }

        try {
            mProtocol = ocRepresentation.getValue(PROTOCOL_KEY);
        } catch (OcException e) {
            throw new IntrospectionException(e);
        }

        try {
            mContentType = ocRepresentation.getValue(CONTENT_TYPE_KEY);
        } catch (OcException e) {
            throw new IntrospectionException(e);
        }

        try {
            mVersion = ocRepresentation.getValue(VERSION_KEY);
        } catch (OcException e) {
            throw new IntrospectionException(e);
        }
    }

    public String getHost() {
        return mHost;
    }

    public void setHost(String host) {
        this.mHost = host;
    }

    public String getUri() {
        return mUri;
    }

    public void setUri(String uri) {
        this.mUri = uri;
    }

    public String getProtocol() {
        return mProtocol;
    }

    public void setProtocol(String protocol) {
        this.mProtocol = protocol;
    }

    public String getContentType() {
        return mContentType;
    }

    public void setContentType(String contentType) {
        this.mContentType = contentType;
    }

    public int getVersion() {
        return mVersion;
    }

    public void setVersion(int version) {
        this.mVersion = version;
    }

    private void separateUrlInHostAndUri(String url) {
        int separatorIndex = url.indexOf('/', 8);
        mHost = url.substring(0, separatorIndex);
        mUri = url.substring(separatorIndex);
    }
}

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

package org.openconnectivity.otgc.domain.model.resource.secure.cred;

import org.iotivity.OCCredData;
import org.iotivity.OCCredUtil;

public class OcCredPublicData {

    private String encoding;
    private String pemData;
    private byte[] derData;

    public OcCredPublicData() {

    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getPemData() {
        return pemData;
    }

    public void setPemData(String data) {
        this.pemData = data;
    }

    public byte[] getDerData() {
        return derData;
    }

    public void setDerData(byte[] derData) {
        this.derData = derData;
    }

    public void parseOCRepresentation(OCCredData data) {
        /* data PEM format */
        String dataPem = data.getData();
        this.setPemData(dataPem);
        /* encoding */
        String encoding = OCCredUtil.readEncoding(data.getEncoding());
        this.setEncoding(encoding);
    }
}

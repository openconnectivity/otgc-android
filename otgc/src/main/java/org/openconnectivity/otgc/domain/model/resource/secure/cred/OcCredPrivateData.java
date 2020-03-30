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

public class OcCredPrivateData {

    private byte[] dataDer;
    private String dataPem;
    private String encoding;

    public OcCredPrivateData() {

    }

    public byte[] getDataDer() {
        return dataDer;
    }

    public void setDataDer(byte[] dataDer) {
        this.dataDer = dataDer;
    }

    public String getDataPem() {
        return dataPem;
    }

    public void setDataPem(String dataPem) {
        this.dataPem = dataPem;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void parseOCRepresentation(OCCredData data) {
        /* data PEM format */
        String dataPem = data.getData();
        this.setDataPem(dataPem);
        /* encoding */
        String encoding = OCCredUtil.readEncoding(data.getEncoding());
        this.setEncoding(encoding);
    }
}

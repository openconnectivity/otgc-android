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

import org.iotivity.CborEncoder;
import org.iotivity.OCRep;
import org.iotivity.OCRepresentation;
import org.openconnectivity.otgc.utils.constant.OcfEncoding;
import org.openconnectivity.otgc.utils.constant.OcfResourceAttributeKey;

public class OcCredPrivateData {

    private byte[] dataDer;
    private String dataPem;
    private OcfEncoding encoding;

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

    public OcfEncoding getEncoding() {
        return encoding;
    }

    public void setEncoding(OcfEncoding encoding) {
        this.encoding = encoding;
    }

    public void parseOCRepresentation(OCRepresentation rep) {
        /* data DER format */
        byte[] dataDer = OCRep.getByteString(rep, OcfResourceAttributeKey.DATA_KEY);
        this.setDataDer(dataDer);
        /* data PEM format */
        String dataPem = OCRep.getString(rep, OcfResourceAttributeKey.DATA_KEY);
        this.setDataPem(dataPem);
        /* encoding */
        String encoding = OCRep.getString(rep, OcfResourceAttributeKey.ENCODING_KEY);
        this.setEncoding(OcfEncoding.valueToEnum(encoding));
    }

    public void parseToCbor(CborEncoder parent) {
        if (this.getEncoding() != null) {
            OCRep.setTextString(parent, OcfResourceAttributeKey.ENCODING_KEY, this.getEncoding().getValue());
        }

        if (this.getDataPem() != null) {
            OCRep.setTextString(parent, OcfResourceAttributeKey.DATA_KEY, this.getDataPem());
        }

        if (this.getDataDer() != null) {
            OCRep.setByteString(parent, OcfResourceAttributeKey.DATA_KEY, this.getDataDer());
        }
    }
}

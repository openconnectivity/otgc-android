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

package org.openconnectivity.otgc.domain.model.resource.secure.csr;

import org.iotivity.OCRep;
import org.iotivity.OCRepresentation;
import org.openconnectivity.otgc.domain.model.resource.OcResourceBase;
import org.openconnectivity.otgc.utils.constant.OcfResourceAttributeKey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OcCsr extends OcResourceBase {

    private byte[] derCsr;
    private String pemCsr = "";
    private String encoding = "";

    public OcCsr() {
        super();
    }

    public byte[] getDerCsr() {
        return derCsr;
    }

    public void setDerCsr(byte[] csr) {
        this.derCsr = csr;
    }

    public String getPemCsr() {
        return this.pemCsr;
    }

    public void setPemCsr(String pemCsr) {
        this.pemCsr = pemCsr;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void parseOCRepresentation(OCRepresentation rep) {
        /* csr DER format */
        byte[] csrDer = OCRep.getByteString(rep, OcfResourceAttributeKey.CSR_KEY);
        this.setDerCsr(csrDer);
        /* csr PEM format */
        String csrPem = OCRep.getString(rep, OcfResourceAttributeKey.CSR_KEY);
        this.setPemCsr(csrPem);
        /* encoding */
        String encoding = OCRep.getString(rep, OcfResourceAttributeKey.ENCODING_KEY);
        this.setEncoding(encoding);
        /* rt */
        String[] resourceTypes = OCRep.getStringArray(rep, OcfResourceAttributeKey.RESOURCE_TYPES_KEY);
        this.setResourceTypes(Arrays.asList(resourceTypes));
        /* if */
        String[] interfaces = OCRep.getStringArray(rep, OcfResourceAttributeKey.INTERFACES_KEY);
        this.setInterfaces(Arrays.asList(interfaces));
    }
}
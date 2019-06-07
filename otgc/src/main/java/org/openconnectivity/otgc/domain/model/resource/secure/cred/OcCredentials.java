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

import com.upokecenter.cbor.CBORObject;

import org.iotivity.CborEncoder;
import org.iotivity.OCRep;
import org.iotivity.OCRepresentation;
import org.openconnectivity.otgc.domain.model.resource.OcResourceBase;
import org.openconnectivity.otgc.utils.constant.OcfResourceAttributeKey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OcCredentials extends OcResourceBase {

    private List<OcCredential> credList;
    private String rowneruuid;

    public OcCredentials() {
        super();
    }

    public List<OcCredential> getCredList() {
        return credList;
    }

    public void setCredList(List<OcCredential> credList) {
        this.credList = credList;
    }

    public String getRowneruuid() {
        return rowneruuid;
    }

    public void setRowneruuid(String rowneruuid) {
        this.rowneruuid = rowneruuid;
    }

    public void parseCbor(CBORObject cbor) {
        /* creds */
        CBORObject credsObj = cbor.get(OcfResourceAttributeKey.CREDENTIALS_KEY);
        if (credsObj != null) {
            List<OcCredential> credList = new ArrayList<>();
            for (CBORObject credObj : credsObj.getValues()) {
                OcCredential cred = new OcCredential();
                cred.parseCbor(credObj);
                credList.add(cred);
            }
            this.setCredList(credList);
        }
        /* rowneruuid */
        CBORObject rowneruuidObj = cbor.get(OcfResourceAttributeKey.ROWNER_UUID_KEY);
        if (rowneruuidObj != null) {
            String rowneruuid = rowneruuidObj.AsString();
            this.setRowneruuid(rowneruuid);
        }
        /* rt */
        CBORObject rtsObj = cbor.get(OcfResourceAttributeKey.RESOURCE_TYPES_KEY);
        if (rtsObj != null) {
            List<String> resourceTypes = new ArrayList<>();
            for (CBORObject rtObj : rtsObj.getValues()){
                resourceTypes.add(rtObj.AsString());
            }
            this.setResourceTypes(resourceTypes);
        }
        /* if */
        CBORObject ifsObj = cbor.get(OcfResourceAttributeKey.INTERFACES_KEY);
        if (ifsObj != null) {
            List<String> interfaces = new ArrayList<>();
            for (CBORObject ifObj : ifsObj.getValues()){
                interfaces.add(ifObj.AsString());
            }
            this.setInterfaces(interfaces);
        }
    }

    public void parseOCRepresentation(OCRepresentation rep) {
        /* creds */
        OCRepresentation credsObj = OCRep.getObjectArray(rep, OcfResourceAttributeKey.CREDENTIALS_KEY);
        List<OcCredential> credList = new ArrayList<>();
        while (credsObj != null) {
            OcCredential cred = new OcCredential();
            cred.parseOCRepresentation(credsObj.getValue().getObject());
            credList.add(cred);

            credsObj = credsObj.getNext();
        }
        this.setCredList(credList);
        /* rowneruuid */
        String rowneruuid = OCRep.getString(rep, OcfResourceAttributeKey.ROWNER_UUID_KEY);
        this.setRowneruuid(rowneruuid);
        /* rt */
        String[] resourceTypes = OCRep.getStringArray(rep, OcfResourceAttributeKey.RESOURCE_TYPES_KEY);
        this.setResourceTypes(resourceTypes != null ? Arrays.asList(resourceTypes) : null);
        /* if */
        String[] interfaces = OCRep.getStringArray(rep, OcfResourceAttributeKey.INTERFACES_KEY);
        this.setInterfaces(interfaces != null ? Arrays.asList(interfaces) : null);
    }

    public CborEncoder parseToCbor() {
        CborEncoder root = OCRep.beginRootObject();

        if (this.getCredList() != null && !this.getCredList().isEmpty()) {
            CborEncoder credsArray = OCRep.openArray(root, OcfResourceAttributeKey.CREDENTIALS_KEY);
            for (OcCredential cred : this.getCredList()) {
                CborEncoder credObj = OCRep.beginObject(credsArray);
                cred.parseToCbor(credObj);
                OCRep.closeObject(credsArray, credObj);
            }

            OCRep.closeArray(root, credsArray);
        }

        if (this.getRowneruuid() != null && !this.getRowneruuid().isEmpty()) {
            OCRep.setTextString(root, OcfResourceAttributeKey.ROWNER_UUID_KEY, this.getRowneruuid());
        }

        OCRep.endRootObject();

        return root;
    }
}

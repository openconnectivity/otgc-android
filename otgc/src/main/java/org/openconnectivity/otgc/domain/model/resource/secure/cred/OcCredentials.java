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

import org.iotivity.OCCred;
import org.iotivity.OCCreds;
import org.iotivity.OCUuidUtil;
import org.openconnectivity.otgc.domain.model.resource.OcResourceBase;

import java.util.ArrayList;
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

    public void parseOCRepresentation(OCCreds creds) {
        /* creds */
        List<OcCredential> credList = new ArrayList<>();
        OCCred cr = creds.getCredsListHead();
        while (cr != null) {
            OcCredential cred = new OcCredential();
            cred.parseOCRepresentation(cr);
            credList.add(cred);

            cr = cr.getNext();
        }
        this.setCredList(credList);
        /* rowneruuid */
        String rowneruuid = OCUuidUtil.uuidToString(creds.getRowneruuid());
        this.setRowneruuid(rowneruuid);
    }
}

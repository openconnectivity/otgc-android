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

import org.iotivity.OCSecurityAce;
import org.iotivity.OCSecurityAcl;
import org.iotivity.OCUuidUtil;
import org.openconnectivity.otgc.domain.model.resource.OcResourceBase;

import java.util.ArrayList;
import java.util.List;

public class OcAcl extends OcResourceBase {

    private List<OcAce> aceList;
    private String rownerUuid;

    public OcAcl() {
        super();
    }

    public List<OcAce> getAceList() {
        return this.aceList;
    }

    public void setAceList(List<OcAce> aceList) {
        this.aceList = aceList;
    }

    public String getRownerUuid() {
        return this.rownerUuid;
    }

    public void setRownerUuid(String rownerUuid) {
        this.rownerUuid = rownerUuid;
    }

    public void parseOCRepresentation(OCSecurityAcl acl) {
        /* aclist2 */
        OCSecurityAce ac = acl.getSubjectsListHead();
        List<OcAce> aceList = new ArrayList<>();
        while (ac != null) {
            OcAce ace = new OcAce();
            ace.parseOCRepresentation(ac);
            aceList.add(ace);

            ac = ac.getNext();
        }
        this.setAceList(aceList);
        /* rowneruuid */
        String rowneruuid = OCUuidUtil.uuidToString(acl.getRowneruuid());
        this.setRownerUuid(rowneruuid);
    }
}

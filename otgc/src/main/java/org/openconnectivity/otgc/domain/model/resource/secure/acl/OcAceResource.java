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

import org.iotivity.OCAceResource;
import org.iotivity.OCInterfaceMask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OcAceResource {

    OCAceResource aceResource;

    public OcAceResource(OCAceResource res) {
        this.aceResource = res;
    }

    public String getHref() {
        if (aceResource.getHref() != null && !aceResource.getHref().isEmpty()) {
            return aceResource.getHref();
        }
        return null;
    }

    public String getWildcard() {
        if (aceResource.getWildcard() != null) {
            /* wc */
            switch (aceResource.getWildcard()) {
                case OC_ACE_WC_ALL:
                    return "*";
                case OC_ACE_WC_ALL_SECURED:
                    return "+";
                case OC_ACE_WC_ALL_PUBLIC:
                    return "-";
                default:
                    return null;
            }
        }
        return null;
    }

    public List<String> getResourceTypes() {
        return Arrays.asList(aceResource.getTypes());
    }


    public List<String> getInterfaces() {
        List<String> list = new ArrayList<>();
        if((OCInterfaceMask.BASELINE | aceResource.getInterfaces()) == OCInterfaceMask.BASELINE) {
            list.add("baseline");
        }
        if((OCInterfaceMask.LL | aceResource.getInterfaces()) == OCInterfaceMask.LL) {
            list.add("ll");
        }
        if((OCInterfaceMask.B | aceResource.getInterfaces()) == OCInterfaceMask.B) {
            list.add("b");
        }
        if((OCInterfaceMask.R | aceResource.getInterfaces()) == OCInterfaceMask.R) {
            list.add("r");
        }
        if((OCInterfaceMask.RW | aceResource.getInterfaces()) == OCInterfaceMask.RW) {
            list.add("rw");
        }
        if((OCInterfaceMask.S | aceResource.getInterfaces()) == OCInterfaceMask.S) {
            list.add("s");
        }
        if((OCInterfaceMask.CREATE | aceResource.getInterfaces()) == OCInterfaceMask.CREATE) {
            list.add("create");
        }
        return list;
    }
}

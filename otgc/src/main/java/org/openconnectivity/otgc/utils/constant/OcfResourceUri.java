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

package org.openconnectivity.otgc.utils.constant;

import org.apache.commons.lang3.NotImplementedException;

public class OcfResourceUri {

    private OcfResourceUri() {
        throw new NotImplementedException("Constant class");
    }

    // URIs
    private static final String OIC_URI = "/oic";
    public static final String RES_URI = OIC_URI + "/res";
    public static final String DEVICE_INFO_URI = OIC_URI + "/d";
    public static final String PLATFORM_INFO_URI = OIC_URI + "/p";
    private static final String SECURITY_URI = OIC_URI + "/sec";
    public static final String DOXM_URI = SECURITY_URI + "/doxm";
    public static final String PSTAT_URI = SECURITY_URI + "/pstat";
    public static final String ACL2_URI = SECURITY_URI + "/acl2";
    public static final String CRED_URI = SECURITY_URI + "/cred";
    public static final String CSR_URI = SECURITY_URI + "/csr";

    // Filters
    public static final String RESOURCE_TYPE_FILTER = "rt=";
    public static final String DELETE_ACE_QUERY = "aceid=";
    public static final String DELETE_CRED_QUERY = "credid=";
}

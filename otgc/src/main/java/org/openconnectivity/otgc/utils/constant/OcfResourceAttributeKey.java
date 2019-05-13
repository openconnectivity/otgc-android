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

public class OcfResourceAttributeKey {

    private OcfResourceAttributeKey() {
        throw new NotImplementedException("Constant class");
    }

    /* Resource base */
    public static final String ID_KEY = "id";
    public static final String NAME_KEY = "n";
    public static final String RESOURCE_TYPES_KEY = "rt";
    public static final String INTERFACES_KEY = "if";

    /* Common doxm, pstat, acl2, cred */
    public static final String ROWNER_UUID_KEY = "rowneruuid";

    /* doxm */
    public static final String OXMS_KEY = "oxms";
    public static final String OXMSEL_KEY = "oxmsel";
    public static final String SUPPORTED_CREDENTIAL_KEY = "sct";
    public static final String OWNED_KEY = "owned";
    public static final String DEVICE_UUID_KEY = "deviceuuid";
    public static final String DEVOWNER_UUID_KEY = "devowneruuid";

    /* pstat */
    public static final String DEVICE_STATE_KEY = "dos";
    public static final String IS_OPERATIONAL_KEY = "isop";
    public static final String CURRENT_MODE_KEY = "cm";
    public static final String TARGET_MODE_KEY = "tm";
    public static final String OPERATIONAL_MODE_KEY = "om";
    public static final String SUPPORT_MODE_KEY = "sm";
    /* pstat.dos */
    public static final String DEVICE_ONBOARDING_STATE_KEY = "s";
    public static final String PENDING_STATE_KEY = "p";

    /* Common acl2, cred, csr */
    public static final String ENCODING_KEY = "encoding";

    /* acl2 */
    public static final String ACE_LIST_KEY = "aclist2";
    /* acl2.ace */
    public static final String ACE_ID_KEY = "aceid";
    public static final String PERMISSION_KEY = "permission";
    public static final String SUBJECT_KEY = "subject";
    public static final String RESOURCES_KEY = "resources";
    /* aclist2.ace.subject */
    public static final String UUID_TYPE_KEY = "uuid";
    public static final String CONN_TYPE_KEY = "conntype";
    public static final String ROLE_AUTHORITY_KEY = "authority";
    public static final String ROLE_KEY = "role";
    /* aclist2.ace.resources */
    public static final String HREF_KEY = "href";
    public static final String WILDCARD_KEY = "wc";

    /* cred */
    public static final String CREDENTIALS_KEY = "creds";
    /* creds.cred*/
    public static final String CRED_ID_KEY = "credid";
    public static final String SUBJECTUUID_KEY = "subjectuuid";
    public static final String ROLE_ID_KEY = "roleid";
    public static final String CRED_TYPE_KEY = "credtype";
    public static final String CRED_USAGE_KEY = "credusage";
    public static final String PUBLIC_DATA_KEY = "publicdata";
    public static final String PRIVATE_DATA_KEY = "privatedata";
    public static final String OPTIONAL_DATA_KEY = "optionaldata";
    public static final String PERIOD_KEY = "period";
    /* creds.cred.publicdata, creds.cred.optionaldata */
    public static final String DATA_KEY = "data";

    /* csr */
    public static final String CSR_KEY = "csr";

    /* d */
    public static final String SPEC_VERSION_URL_KEY = "icv";
    public static final String DEVICE_ID_KEY = "di";
    public static final String DATA_MODEL_KEY = "dmv";
    public static final String PIID_KEY = "piid";
    public static final String DESCRIPTIONS_KEY ="ld";
    public static final String SW_VERSION_KEY = "sv";
    public static final String DEV_MAN_NAME_KEY = "dmn";
    public static final String DEV_MODEL_NO_KEY = "dmno";

    /* p */
    public static final String PLATFORM_ID_KEY = "pi";
    public static final String MAN_INFO = "vid";
    public static final String MAN_NAME_KEY = "mnmn";
    public static final String MAN_URL_KEY = "mnml";
    public static final String MAN_MODEL_NO_KEY = "mnmo";
    public static final String MAN_DATE_KEY = "mndt";
    public static final String MAN_PLATFORM_VER_KEY = "mnpv";
    public static final String MAN_OS_VER_KEY = "mnos";
    public static final String MAN_HW_VER_KEY = "mnhw";
    public static final String MAN_FW_VER_KEY = "mnfv";
    public static final String MAN_SUPPORT_URL_KEY = "mnsl";
    public static final String MAN_SYSTEM_TIME_KEY = "st";
    public static final String MAN_SERIAL_NO = "mnsel";

    /* res */
    public static final String ANCHOR_KEY = "anchor";
    public static final String HREF_RES_KEY = "href";
    public static final String PROPERTIES_KEY = "p";
    public static final String ENDPOINTS_KEY = "eps";
    /* res.p*/
    public static final String FRAMEWORK_POLICIES_KEY = "bm";
    /* res.eps */
    public static final String ENDPOINT_KEY = "ep";

    /* introspection */
    public static final String URL_INFO_KEY = "urlInfo";
    /* introspection.urlinfo */
    public static final String URL_KEY = "url";
    public static final String PROTOCOL_KEY = "protocol";
    public static final String CONTENT_TYPE_KEY = "content-type";
    public static final String VERSION_KEY = "version";
}


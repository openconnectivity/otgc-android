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

public enum OcfCredUsage {
    OC_CREDUSAGE_UNKNOWN(""),
    OC_CREDUSAGE_TRUSTCA("oic.sec.cred.trustca"),
    OC_CREDUSAGE_CERT("oic.sec.cred.cert"),
    OC_CREDUSAGE_ROLECERT("oic.sec.cred.rolecert"),
    OC_CREDUSAGE_MFGTRUSTCA("oic.sec.cred.mfgtrustca"),
    OC_CREDUSAGE_MFGCERT("oic.sec.cred.mfgcert");

    private String credUsage;

    OcfCredUsage(String credUsage) {
        this.credUsage = credUsage;
    }

    public String getValue()
    {
        return credUsage;
    }

    public static OcfCredUsage valueToEnum(String credusage) {
        if (credusage.equals(OC_CREDUSAGE_TRUSTCA.getValue())){
            return OC_CREDUSAGE_TRUSTCA;
        } else if (credusage.equals(OC_CREDUSAGE_CERT.getValue())) {
            return OC_CREDUSAGE_CERT;
        } else if (credusage.equals(OC_CREDUSAGE_ROLECERT.getValue())) {
            return OC_CREDUSAGE_ROLECERT;
        } else if (credusage.equals(OC_CREDUSAGE_MFGTRUSTCA.getValue())) {
            return OC_CREDUSAGE_MFGTRUSTCA;
        } else if (credusage.equals(OC_CREDUSAGE_MFGCERT.getValue())) {
            return OC_CREDUSAGE_MFGCERT;
        } else {
            return OC_CREDUSAGE_UNKNOWN;
        }
    }
}


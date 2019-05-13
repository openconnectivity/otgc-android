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

public enum OcfEncoding {
    OC_ENCODING_UNKNOWN(""),
    OC_ENCODING_BASE64("oic.sec.encoding.base64"),
    OC_ENCODING_RAW("oic.sec.encoding.raw"),
    OC_ENCODING_PEM("oic.sec.encoding.pem"),
    OC_ENCODING_DER("oic.sec.encoding.der");

    private String encoding;

    OcfEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getValue()
    {
        return encoding;
    }

    public static OcfEncoding valueToEnum(String encoding) {
        if (encoding.equals(OC_ENCODING_BASE64)) {
            return OC_ENCODING_BASE64;
        } else if (encoding.equals(OC_ENCODING_RAW)) {
            return OC_ENCODING_RAW;
        } else if (encoding.equals(OC_ENCODING_PEM)) {
            return OC_ENCODING_PEM;
        } else if (encoding.equals(OC_ENCODING_DER)) {
            return OC_ENCODING_DER;
        } else {
            return OC_ENCODING_UNKNOWN;
        }
    }
}


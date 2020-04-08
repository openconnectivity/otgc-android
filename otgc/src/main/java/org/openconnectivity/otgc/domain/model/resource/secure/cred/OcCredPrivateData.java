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

import org.iotivity.OCCredData;
import org.iotivity.OCCredUtil;
import org.spongycastle.util.encoders.Base64;

public class OcCredPrivateData {

    OCCredData privateData;

    public OcCredPrivateData(OCCredData privateData) {
        this.privateData = privateData;
    }

    public byte[] getDataDer() {
        if (privateData.getData() == null) {
            return null;
        }

        String pem = privateData.getData();

        String base64 = pem.replaceAll("\\s", "")
                .replaceAll("\\r\\n", "")
                .replace("-----BEGINCERTIFICATE-----", "")
                .replace("-----ENDCERTIFICATE-----", "");
        return Base64.decode(base64.getBytes());
    }

    public String getDataPem() {
        return privateData.getData();
    }

    public String getEncoding() {
        return OCCredUtil.readEncoding(privateData.getEncoding());
    }
}

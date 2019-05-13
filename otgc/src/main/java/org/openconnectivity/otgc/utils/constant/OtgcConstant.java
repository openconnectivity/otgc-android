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

import java.io.File;

public class OtgcConstant {

    private OtgcConstant() {
        throw new NotImplementedException("Constant class");
    }

    // Credential directory
    public static final String OTGC_CREDS_DIR = "otgc_creds";

    // File databases for IoTivity
    public static final String INTROSPECTION_CBOR_FILE = "introspection.dat";

    // Root certificate and keypair
    public static String ROOT_CERTIFICATE = "root.crt";
    public static String ROOT_PRIVATE_KEY = "root.prv";
    public static String ROOT_PUBLIC_KEY = "root.pub";
}

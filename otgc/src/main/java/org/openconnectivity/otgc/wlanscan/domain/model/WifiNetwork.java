/*
 * *****************************************************************
 *
 *  Copyright 2018 DEKRA Testing and Certification, S.A.U. All Rights Reserved.
 *
 *  ******************************************************************
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  ******************************************************************
 */

package org.openconnectivity.otgc.wlanscan.domain.model;

import org.openconnectivity.otgc.devicelist.data.model.AuthenticationType;
import org.openconnectivity.otgc.devicelist.data.model.EncryptationType;

public class WifiNetwork {

    // Constants used for different security types
    private static final String WEP = "WEP";
    private static final String WPA = "WPA";
    private static final String WPA2 = "WPA2";
    private static final String OPEN = "Open";

    // For EAP Enterprise fields
    private static final String WPA_EAP = "WPA-EAP";
    private static final String IEEE8021X = "IEEE8021X";

    private String mName;
    private String mCapabilities;
    private String mSecurity;
    private int mLevel;

    public WifiNetwork(String name, String capabilities, int level) {
        this.mName = name;
        this.mCapabilities = capabilities;
        this.mSecurity = getCapabilitiesSecurity(capabilities);
        this.mLevel = level;
    }

    public String getName() {
        return mName;
    }

    public String getCapabilities() {
        return mCapabilities;
    }

    public String getSecurity() {
        return mSecurity;
    }

    public int getLevel() {
        return mLevel;
    }

    public boolean isSecured() {
        return !mSecurity.equals(OPEN);
    }

    public boolean isWep() {
        return mSecurity.equals(WEP);
    }

    public boolean isWpa() {
        return mSecurity.equals(WPA);
    }

    public boolean isWpa2() {
        return mSecurity.equals(WPA2);
    }

    private static String getCapabilitiesSecurity(String capabilities) {
        final String[] securityModes = { WEP, WPA, WPA2, WPA_EAP, IEEE8021X };
        for (int i = securityModes.length - 1; i >= 0; i--) {
            if (capabilities.contains(securityModes[i])) {
                return securityModes[i];
            }
        }

        return OPEN;
    }

    public int getAuthenticationType() {
        if (mCapabilities.contains(AuthenticationType.WEP.toString())) {
            return AuthenticationType.WEP.ordinal();
        } else if (mCapabilities.contains(AuthenticationType.WPA_PSK.toString().replace("_", "-"))) {
            return AuthenticationType.WPA_PSK.ordinal();
        } else if (mCapabilities.contains(AuthenticationType.WPA2_PSK.toString().replace("_", "-"))) {
            return AuthenticationType.WPA2_PSK.ordinal();
        } else {
            return AuthenticationType.NONE_AUTH.ordinal();
        }

    }

    public int getEncryptionType() {
        if (mCapabilities.contains(EncryptationType.WEP_64.toString().replace("_", "-"))) {
            return EncryptationType.WEP_64.ordinal();
        } else if (mCapabilities.contains(EncryptationType.WEP_128.toString().replace("_", "-"))) {
            return EncryptationType.WEP_128.ordinal();
        } else if (mCapabilities.contains(EncryptationType.TKIP_AES.toString().replace("_", "-"))) {
            return EncryptationType.TKIP_AES.ordinal();
        } else if (mCapabilities.contains(EncryptationType.TKIP.toString())) {
            return EncryptationType.TKIP.ordinal();
        } else if (mCapabilities.contains(EncryptationType.AES.toString())
                || mCapabilities.contains("CCMP")) {
            return EncryptationType.AES.ordinal();
        } else {
            return EncryptationType.NONE_ENC.ordinal();
        }
    }
}

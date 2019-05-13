package org.openconnectivity.otgc.utils.constant;

public enum OcfOxmType {
    OC_OXMTYPE_UNKNOWN(-1),
    OC_OXMTYPE_JW(0),
    OC_OXMTYPE_RDP(1),
    OC_OXMTYPE_MFG_CERT(2);

    private int oxm;

    OcfOxmType(int oxm) {
        this.oxm = oxm;
    }

    public int getValue() {
        return oxm;
    }

    public static OcfOxmType valueToEnum(int oxm) {
        if (oxm == OC_OXMTYPE_JW.getValue()) {
            return OC_OXMTYPE_JW;
        } else if (oxm == OC_OXMTYPE_RDP.getValue()) {
            return OC_OXMTYPE_RDP;
        } else if (oxm == OC_OXMTYPE_MFG_CERT.getValue()) {
            return OC_OXMTYPE_MFG_CERT;
        } else {
            return OC_OXMTYPE_UNKNOWN;
        }
    }
}

package org.openconnectivity.otgc.utils.constant;

public enum OcfCredType {
    OC_CREDTYPE_UNKNOWN(-1),
    OC_CREDTYPE_PSK(1),
    OC_CREDTYPE_CERT(8);

    private int credType;

    OcfCredType(int credType) {
        this.credType = credType;
    }

    public int getValue() {
        return credType;
    }

    public static OcfCredType valueToEnum(int credType) {
        if (credType == OC_CREDTYPE_PSK.getValue()) {
            return OC_CREDTYPE_PSK;
        } else if (credType == OC_CREDTYPE_CERT.getValue()) {
            return OC_CREDTYPE_CERT;
        } else {
            return OC_CREDTYPE_UNKNOWN;
        }
    }
}

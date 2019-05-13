package org.openconnectivity.otgc.utils.constant;

public enum OcfDosType {
    OC_DOSTYPE_UNKNOWN(-1),
    OC_DOSTYPE_RESET(0),
    OC_DOSTYPE_RFOTM(1),
    OC_DOSTYPE_RFPRO(2),
    OC_DOSTYPE_RFNOP(3),
    OC_DOSTYPE_SRESET(4);

    private int dos;

    OcfDosType(int dos) {
        this.dos = dos;
    }

    public int getValue() {
        return dos;
    }

    public static OcfDosType valueToEnum(int dos) {
        if (dos == OC_DOSTYPE_RESET.getValue()) {
            return OC_DOSTYPE_RESET;
        } else if (dos == OC_DOSTYPE_RFOTM.getValue()) {
            return OC_DOSTYPE_RFOTM;
        } else if (dos == OC_DOSTYPE_RFPRO.getValue()) {
            return OC_DOSTYPE_RFPRO;
        } else if (dos == OC_DOSTYPE_RFNOP.getValue()) {
            return OC_DOSTYPE_RFNOP;
        } else if (dos == OC_DOSTYPE_SRESET.getValue()) {
            return OC_DOSTYPE_SRESET;
        } else {
            return OC_DOSTYPE_UNKNOWN;
        }
    }
}

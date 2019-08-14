package org.openconnectivity.otgc.utils.constant;

public class OcfWildcard {

    private OcfWildcard() {
        throw new IllegalStateException("Constant class");
    }

    public static final String OC_WILDCARD_ALL_NCR = "*";
    public static final String OC_WILDCARD_ALL_SECURE_NCR = "+";
    public static final String OC_WILDCARD_ALL_NON_SECURE_NCR = "-";

    public static boolean isWildcard(String resourceUri) {
        if (resourceUri.equals(OC_WILDCARD_ALL_NCR)
                || resourceUri.equals(OC_WILDCARD_ALL_SECURE_NCR)
                || resourceUri.equals(OC_WILDCARD_ALL_NON_SECURE_NCR)) {
            return true;
        }
        return false;
    }
}

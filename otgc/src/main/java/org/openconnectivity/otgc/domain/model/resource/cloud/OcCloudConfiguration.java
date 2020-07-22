package org.openconnectivity.otgc.domain.model.resource.cloud;

public class OcCloudConfiguration {

    private String authProvider;
    private String cloudUrl;
    private String accessToken;
    private String cloudUuid;

    public OcCloudConfiguration() { }

    public OcCloudConfiguration(String apn, String url, String at, String uuid) {
        this.authProvider = apn;
        this.cloudUrl = url;
        this.accessToken = at;
        this.cloudUuid = uuid;
    }

    public String getAuthProvider() {
        return authProvider;
    }

    public void setAuthProvider(String authProvider) {
        this.authProvider = authProvider;
    }

    public String getCloudUrl() {
        return cloudUrl;
    }

    public void setCloudUrl(String cloudUrl) {
        this.cloudUrl = cloudUrl;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getCloudUuid() {
        return cloudUuid;
    }

    public void setCloudUuid(String cloudUuid) {
        this.cloudUuid = cloudUuid;
    }
}

package com.lumi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "classroom-storage")
public class ClassroomStorageProperties {

    private boolean enabled;
    private String endpoint;
    private String bucket;
    private String accessKey;
    private String secretKey;
    private int signedUrlSeconds = 300;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public int getSignedUrlSeconds() {
        return signedUrlSeconds;
    }

    public void setSignedUrlSeconds(int signedUrlSeconds) {
        this.signedUrlSeconds = signedUrlSeconds;
    }
}

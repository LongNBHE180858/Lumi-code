package com.lumi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "course-storage")
public class CourseStorageProperties {

    private boolean enabled;
    private String endpoint;
    private String bucket;
    private String accessKey;
    private String secretKey;
    private int signedUrlSeconds = 300;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean value) {
        enabled = value;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String value) {
        endpoint = value;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String value) {
        bucket = value;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String value) {
        accessKey = value;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String value) {
        secretKey = value;
    }

    public int getSignedUrlSeconds() {
        return signedUrlSeconds;
    }

    public void setSignedUrlSeconds(int value) {
        signedUrlSeconds = value;
    }
}

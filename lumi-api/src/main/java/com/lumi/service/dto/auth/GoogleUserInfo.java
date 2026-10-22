package com.lumi.service.dto.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GoogleUserInfo(
    String sub,
    String email,
    @JsonProperty("email_verified") String emailVerified,
    String name,
    @JsonProperty("given_name") String givenName,
    @JsonProperty("family_name") String familyName,
    String picture
) implements Serializable {}

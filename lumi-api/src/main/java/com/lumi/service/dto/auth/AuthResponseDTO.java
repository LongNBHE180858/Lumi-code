package com.lumi.service.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

public class AuthResponseDTO implements Serializable {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("token_type")
    private String tokenType = "Bearer";

    @JsonProperty("expires_in")
    private Long expiresIn;

    @JsonProperty("user")
    private UserAuthDTO user;

    @JsonProperty("is_2fa_required")
    private Boolean is2faRequired = false;

    @JsonProperty("email")
    private String email;

    public AuthResponseDTO() {}

    public static AuthResponseDTO require2fa(String email) {
        AuthResponseDTO response = new AuthResponseDTO();
        response.setIs2faRequired(true);
        response.setEmail(email);
        return response;
    }

    public AuthResponseDTO(String accessToken, String refreshToken, Long expiresIn, UserAuthDTO user) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = "Bearer";
        this.expiresIn = expiresIn;
        this.user = user;
        this.is2faRequired = false;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public UserAuthDTO getUser() {
        return user;
    }

    public void setUser(UserAuthDTO user) {
        this.user = user;
    }

    public Boolean getIs2faRequired() {
        return is2faRequired;
    }

    public void setIs2faRequired(Boolean is2faRequired) {
        this.is2faRequired = is2faRequired;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

package com.lumi.service.dto.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lumi.domain.Authority;
import com.lumi.domain.User;
import java.io.Serializable;
import java.util.Set;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserAuthDTO implements Serializable {

    private Long id;
    private String login;
    private String firstName;
    private String lastName;
    private String email;
    private String imageUrl;
    private boolean activated;
    private String langKey;
    private Set<String> authorities;
    private UserProfileAuthDTO profile;
    private TeacherVerificationAuthDTO teacherVerification;

    public UserAuthDTO() {}

    public UserAuthDTO(User user) {
        if (user != null) {
            this.id = user.getId();
            this.login = user.getLogin();
            this.firstName = user.getFirstName();
            this.lastName = user.getLastName();
            this.email = user.getEmail();
            this.imageUrl = user.getImageUrl();
            this.activated = user.isActivated();
            this.langKey = user.getLangKey();
            if (user.getAuthorities() != null) {
                this.authorities = user.getAuthorities().stream().map(Authority::getName).collect(Collectors.toSet());
            }
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public String getLangKey() {
        return langKey;
    }

    public void setLangKey(String langKey) {
        this.langKey = langKey;
    }

    public Set<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<String> authorities) {
        this.authorities = authorities;
    }

    public UserProfileAuthDTO getProfile() {
        return profile;
    }

    public void setProfile(UserProfileAuthDTO profile) {
        this.profile = profile;
    }

    public TeacherVerificationAuthDTO getTeacherVerification() {
        return teacherVerification;
    }

    public void setTeacherVerification(TeacherVerificationAuthDTO teacherVerification) {
        this.teacherVerification = teacherVerification;
    }
}

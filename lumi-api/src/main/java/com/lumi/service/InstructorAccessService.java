package com.lumi.service;

import com.lumi.domain.User;
import com.lumi.domain.enumeration.InstructorApplicationStatus;
import com.lumi.repository.UserProfileRepository;
import com.lumi.repository.UserRepository;
import com.lumi.security.AuthoritiesConstants;
import com.lumi.security.SecurityUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class InstructorAccessService {

    private final UserRepository users;
    private final UserProfileRepository profiles;

    public InstructorAccessService(UserRepository users, UserProfileRepository profiles) {
        this.users = users;
        this.profiles = profiles;
    }

    public boolean canUseInstructorFeatures() {
        return SecurityUtils.getCurrentUserLogin()
            .flatMap(users::findOneByLogin)
            .filter(User::isActivated)
            .map(user -> {
                if (
                    user
                        .getAuthorities()
                        .stream()
                        .anyMatch(authority -> AuthoritiesConstants.ADMIN.equals(authority.getName()))
                ) {
                    return true;
                }
                if (
                    user
                        .getAuthorities()
                        .stream()
                        .noneMatch(authority -> AuthoritiesConstants.INSTRUCTOR.equals(authority.getName()))
                ) {
                    return false;
                }
                return profiles
                    .findOneByUserId(user.getId())
                    .map(profile -> profile.getInstructorStatus() == InstructorApplicationStatus.ACTIVE)
                    .orElse(false);
            })
            .orElse(false);
    }

    public User requireActiveInstructor() {
        if (!canUseInstructorFeatures()) {
            throw new AccessDeniedException("Active instructor required");
        }
        return users.findOneByLogin(SecurityUtils.getCurrentUserLogin().orElseThrow()).orElseThrow();
    }
}

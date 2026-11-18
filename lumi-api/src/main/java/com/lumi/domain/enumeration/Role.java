package com.lumi.domain.enumeration;

/**
 * Business roles available for managed users.
 */
public enum Role {
    ROLE_ADMIN,
    ROLE_INSTRUCTOR,
    ROLE_STUDENT,
    ROLE_PARENT,
    ROLE_REVIEWER;

    public String getRoleName() {
        return name();
    }
}

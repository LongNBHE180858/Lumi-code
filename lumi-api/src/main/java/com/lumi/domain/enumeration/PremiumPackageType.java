package com.lumi.domain.enumeration;

public enum PremiumPackageType {
    PARENT_PERSONAL("PERSONAL", "PARENT"),
    PARENT_FAMILY("FAMILY", "PARENT"),
    INSTRUCTOR_PERSONAL("PERSONAL", "INSTRUCTOR"),
    INSTRUCTOR_CLASS("CLASS", "INSTRUCTOR");

    private final String planType;
    private final String targetRole;

    PremiumPackageType(String planType, String targetRole) {
        this.planType = planType;
        this.targetRole = targetRole;
    }

    public String getPlanType() {
        return planType;
    }

    public String getTargetRole() {
        return targetRole;
    }

    public static PremiumPackageType from(String planType, String targetRole) {
        for (PremiumPackageType type : values()) {
            if (type.planType.equals(planType) && type.targetRole.equals(targetRole)) {
                return type;
            }
        }
        return null;
    }
}

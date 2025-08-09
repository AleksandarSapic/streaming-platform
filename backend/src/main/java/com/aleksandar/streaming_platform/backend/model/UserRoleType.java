package com.aleksandar.streaming_platform.backend.model;

public enum UserRoleType {
    USER("USER"),
    ADMIN("ADMIN");

    private final String roleName;

    UserRoleType(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }

    public static UserRoleType fromString(String roleName) {
        if (roleName == null) {
            return USER;
        }
        
        for (UserRoleType roleType : UserRoleType.values()) {
            if (roleType.roleName.equalsIgnoreCase(roleName)) {
                return roleType;
            }
        }
        
        throw new IllegalArgumentException("Unknown role: " + roleName);
    }

    @Override
    public String toString() {
        return roleName;
    }
}
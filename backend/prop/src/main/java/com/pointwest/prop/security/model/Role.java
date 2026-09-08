package com.pointwest.prop.security.model;

public enum Role {
    ADMIN;

    public String authority() {
        return "ROLE_" + name();
    }

    public boolean isAdmin() {
        return this == ADMIN;
    }
}

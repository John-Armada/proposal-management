package com.pointwest.prop.auth.model;

public enum Role {
    ADMIN;

    public String authority() {
        return "ROLE_" + name();
    }

    public boolean isAdmin() {
        return this == ADMIN;
    }
}

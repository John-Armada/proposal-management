package com.pointwest.bootcamp.prop.security.model;

public enum Role {
    ADMIN;

    public String authority() {
        return "ROLE_" + name();
    }

    public boolean isAdmin() {
        return this == ADMIN;
    }
}

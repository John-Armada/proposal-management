package com.pointwest.prop.auth.model;

public enum Role {
    AUTHOR,
    REVIEWER,
    ADMIN;

    public String authority() {
        return "ROLE_" + name();
    }

    public boolean isAdmin() {
        return this == ADMIN;
    }

    public boolean isReviewer() {
        return this == REVIEWER;
    }

    public boolean isAuthor() {
        return this == AUTHOR;
    }
}
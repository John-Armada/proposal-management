package com.pointwest.prop.email.entity;

public enum EmailEvent {

    ACCOUNT_CREATED,
    PASSWORD_RESET;

    public String templateName() {
        return name().toLowerCase().replace('_', '-');
    }
}

package com.pointwest.prop.entity;

public enum EmailEvent {

    ACCOUNT_CREATED,
    PASSWORD_RESET;

    public String templateName() {
        return name().toLowerCase().replace('_', '-');
    }
}

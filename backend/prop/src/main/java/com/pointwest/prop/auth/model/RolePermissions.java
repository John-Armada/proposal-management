package com.pointwest.prop.auth.model;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

public final class RolePermissions {

    private static final Map<Role, Set<String>> MAP = new EnumMap<>(Role.class);

    static {
        MAP.put(Role.AUTHOR, Set.of(
        // Permission.PROPOSAL_CREATE,
        // Permission.PROPOSAL_EDIT_OWN,
        ));

        MAP.put(Role.REVIEWER, Set.of(
        // Permission.PROPOSAL_APPROVE,
        // Permission.REPORT_VIEW_ALL,
        ));

        MAP.put(Role.ADMIN, Set.of(
        // Permission.USER_MANAGE,
        // Permission.ROLE_MANAGE,
        // Permission.CATEGORY_MANAGE,
        ));
    }

    private RolePermissions() {
    }

    public static Set<String> of(Role role) {
        return MAP.getOrDefault(role, Set.of());
    }
}
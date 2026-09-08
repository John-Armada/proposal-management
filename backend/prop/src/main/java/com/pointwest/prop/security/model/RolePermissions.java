package com.pointwest.prop.security.model;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

public final class RolePermissions {

    private static final Map<Role, Set<String>> MAP = new EnumMap<>(Role.class);

    static {
        MAP.put(Role.ADMIN, Set.of(
        // Permission.PROP_READ_ORG,
        ));
    }

    private RolePermissions() {
    }

    public static Set<String> of(Role role) {
        return MAP.getOrDefault(role, Set.of());
    }
}

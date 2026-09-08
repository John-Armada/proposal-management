package com.pointwest.prop.auth.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.pointwest.prop.auth.jwt.JwtAuthenticationToken;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static JwtAuthenticationToken currentAuth() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            return jwtAuth;
        }
        throw new IllegalStateException("No authenticated JWT principal in the current SecurityContext");
    }

    public static Long currentUserId() {
        return currentAuth().getUserId();
    }

    public static Long currentDeptId() {
        return currentAuth().getDeptId();
    }

    public static boolean isAdmin() {
        return currentAuth().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
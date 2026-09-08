package com.pointwest.prop.auth.util;

import java.util.UUID;

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

    private static boolean hasAuthority(String authority) {
        return currentAuth().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(authority));
    }

    public static boolean isAdmin() {
        return hasAuthority("ROLE_ADMIN");
    }

    public static boolean isReviewer() {
        return hasAuthority("ROLE_REVIEWER");
    }

    public static boolean isAuthor() {
        return hasAuthority("ROLE_AUTHOR");
    }

    public static boolean isSameIdentityAs(Long otherUserId) {
        return otherUserId != null && otherUserId.equals(currentUserId());
    }
}
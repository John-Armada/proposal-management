package com.pointwest.bootcamp.prop.security.jwt;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import lombok.Getter;

@Getter
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final Long userId;
    private final Long deptId;
    private final String subjectEmailOrId;

    public JwtAuthenticationToken(Long userId, Long deptId, String subjectEmailOrId,
            Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.userId = userId;
        this.deptId = deptId;
        this.subjectEmailOrId = subjectEmailOrId;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return subjectEmailOrId;
    }
}
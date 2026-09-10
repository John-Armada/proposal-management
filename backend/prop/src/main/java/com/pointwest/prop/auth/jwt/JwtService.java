package com.pointwest.prop.auth.jwt;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import com.pointwest.prop.auth.model.Role;
import com.pointwest.prop.auth.model.RolePermissions;
import com.pointwest.prop.common.repository.RevokedTokenRepository;
import com.pointwest.prop.user.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final SecretKey signingKey;
    private final JwtProperties properties;
    private final RevokedTokenRepository revokedTokens;

    public JwtService(JwtProperties properties, RevokedTokenRepository revokedTokens) {
        this.properties = properties;
        this.signingKey = buildKey(properties.getSecret());
        this.revokedTokens = revokedTokens;
    }

    private static SecretKey buildKey(String base64Secret) {
        byte[] bytes = Decoders.BASE64.decode(base64Secret);
        return Keys.hmacShaKeyFor(bytes);
    }

    public String generateAccessToken(User user) {
        Instant now = Instant.now();
        Instant expiry = now.plus(properties.getAccessTokenTtlMinutes(), ChronoUnit.MINUTES);

        Set<String> permissions = RolePermissions.of(user.getRole());

        String jti = UUID.randomUUID().toString();

        return Jwts.builder()
                .issuer(properties.getIssuer())
                .subject(String.valueOf(user.getUserId()))
                .id(jti)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .claim("user_id", user.getUserId())
                .claim("dept_id", user.getDeptId())
                .claim("email", user.getEmail())
                .claim("firstName", user.getFirstName())
                .claim("lastName", user.getLastName())
                .claim("roles", List.of(user.getRole().name()))
                .claim("permissions", List.copyOf(permissions))
                .signWith(signingKey)
                .compact();
    }

    public Claims parseAndValidate(String token) throws JwtException {
        return Jwts.parser()
                .requireIssuer(properties.getIssuer())
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isRevoked(String jti) {
        return revokedTokens.existsById(jti);
    }

    public Role extractRole(Claims claims) {
        List<?> roles = claims.get("roles", List.class);
        if (roles == null || roles.isEmpty()) {
            throw new JwtException("Token missing roles claim");
        }
        return Role.valueOf(String.valueOf(roles.get(0)));
    }

    @SuppressWarnings("unchecked")
    public List<String> extractPermissions(Claims claims) {
        return (List<String>) claims.getOrDefault("permissions", List.of());
    }

    public Long extractUserId(Claims claims) {
        Object userId = claims.get("user_id");
        return userId == null ? null : Long.valueOf(String.valueOf(userId));
    }

    public Long extractDeptId(Claims claims) {
        Object deptId = claims.get("dept_id");
        return deptId == null ? null : Long.valueOf(String.valueOf(deptId));
    }
}
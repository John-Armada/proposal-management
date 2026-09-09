package com.pointwest.prop.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.net.URI;
import java.util.Set;

import org.springframework.stereotype.Component;

@Component 
public class GoogleDocLinkValidator implements ConstraintValidator<ValidGoogleDocLink, String> {

    // Allowlist of legitimate Google Docs hosts
    private static final Set<String> ALLOWED_HOSTS = Set.of(
            "docs.google.com",
            "drive.google.com",
            "google.com"
    );

    // Path prefixes for common Google Workspace files
    private static final Set<String> ALLOWED_PATH_PREFIXES = Set.of(
            "/document", "/spreadsheets", "/presentation", "/forms", "/file"
    );

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true; // Use @NotBlank or @NotNull separately if required
        }

        try {
            // 1. Parse using URI (safer than URL as it doesn't resolve DNS)
            URI uri = new URI(value);

            // 2. Validate Scheme (Force HTTPS)
            if (!"https".equalsIgnoreCase(uri.getScheme())) {
                return false;
            }

            // 3. Validate Host against an explicit allowlist
            String host = uri.getHost();
            if (host == null || !ALLOWED_HOSTS.contains(host.toLowerCase())) {
                return false;
            }

            // 4. Validate Path structure to ensure it's a doc, not just google.com
            String path = uri.getPath();
            if (path == null) {
                return false;
            }

            return ALLOWED_PATH_PREFIXES.stream().anyMatch(path::startsWith);

        } catch (Exception e) {
            // Any parsing exception means the URL syntax is fundamentally broken
            return false;
        }
    }
}


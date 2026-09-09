package com.pointwest.prop.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = GoogleDocLinkValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidGoogleDocLink {
    String message() default "Invalid Google Docs URL structure or domain";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}


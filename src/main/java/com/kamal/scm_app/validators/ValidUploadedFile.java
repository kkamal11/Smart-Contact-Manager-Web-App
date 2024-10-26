package com.kamal.scm_app.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target(
        {ElementType.FIELD,ElementType.METHOD, //We will use this annotation on fields, methods
        ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, //annotations
        ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FileValidator.class) //this annotation will use the methods of this class to check/verify
public @interface ValidUploadedFile {
    String message() default "Invalid file";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}

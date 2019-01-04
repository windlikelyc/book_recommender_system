package com.lyc.validator;



import com.lyc.service.ClientService;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ClientValidator.class)
@SuppressWarnings("javadoc")
public @interface UsernameDup {

    String message() default "用户名已存在";
    boolean allowBlank() default false;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default{};


}

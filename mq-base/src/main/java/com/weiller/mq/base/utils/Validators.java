package com.weiller.mq.base.utils;

import com.weiller.mq.base.exception.ValidationException;

import java.lang.reflect.Method;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

public abstract class Validators {

    private static ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

    /**
     *
     * @param object
     * @return
     */
    public static void validate(Object object) {
        if (object == null) return;
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<Object>> errors = validator.validate(object);
        if (errors.isEmpty()) return;
        throw new ValidationException(errors);
    }

    /**
     * 判断是否通过校验
     * @param object
     * @return
     */
    public static boolean validateNoException(Object object) {
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<Object>> errors = validator.validate(object);
        return errors.isEmpty();

    }

    public static void validateParameters(Object object, Method method, Object... values) {
        if (object == null) return;
        Set<ConstraintViolation<Object>> errors = factory.getValidator()
                .forExecutables()
                .validateParameters(object, method, values);
        if (errors.isEmpty()) return;
        throw new ValidationException(errors, method);
    }
}

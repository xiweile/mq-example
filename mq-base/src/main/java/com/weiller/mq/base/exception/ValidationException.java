package com.weiller.mq.base.exception;

import com.weiller.mq.base.utils.JacksonUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;

import javax.validation.ConstraintViolation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 校验异常
 */
public class ValidationException extends RuntimeException {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = -118159199582907435L;
	private List<ValidationDetail> details = new ArrayList<>(10);


    public static class ValidationDetail {
        private String field;
        private String message;
        private Object value;

        public static ValidationDetail
            valueOf(Class clazz, String property, String message, Object pathVal) {
            String path = StringUtils.defaultIfEmpty(property, clazz.toString());
            ValidationDetail detail = new ValidationDetail();
            detail.setField(path);
            detail.setMessage(message);
            detail.setValue(pathVal);
            return detail;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }
    }
    public void addDetail(ValidationDetail validationDetail) {
        details.add(validationDetail);
    }
    public ValidationException(String message) {
    	super(message);
    }
    public ValidationException(Set<ConstraintViolation<Object>> errors) {
    	for(ConstraintViolation<Object> violation : errors){
            String message = violation.getMessage();
            String path = violation.getPropertyPath().toString();
            Class clazz = violation.getRootBeanClass();
            Object pathVal = violation.getInvalidValue();
            addDetail(ValidationDetail.valueOf(clazz, path, message, pathVal));
    	}
    }
    public ValidationException(Set<ConstraintViolation<Object>> errors, Method method) {
    	for(ConstraintViolation<Object> violation : errors){
            String message = violation.getMessage();
            String path = getPath(method, violation.getPropertyPath().toString());
            Class clazz = violation.getRootBeanClass();
            Object pathVal = violation.getInvalidValue();
            addDetail(ValidationDetail.valueOf(clazz, path, message, pathVal));
    	}
    }

    private String getPath(Method method, String path) {
        String[] pathDetails = StringUtils.split(path, ".");
        String methodArg = pathDetails[pathDetails.length - 1];
        if (!methodArg.startsWith("arg")) {
            return path;
        }
        String newMethodArg = getParameterName(method, methodArg);
        return newMethodArg;

    }

    private String getParameterName(Method method, String var) {
        String index = StringUtils.split(var, "arg")[0];
        String[] parameterNames = new LocalVariableTableParameterNameDiscoverer().getParameterNames(method);
        return parameterNames[Integer.parseInt(index)];
    }

    public List<ValidationDetail> getDetails() {
        return this.details;
    }

    @Override
    public String getMessage() {
        if (this.details != null && this.details.size() > 0) {
            return this.getDetails().get(0).getMessage();
        }
        return super.getMessage();
    }

    @Override
    public String toString() {
        return JacksonUtil.toJson(this);
    }
}

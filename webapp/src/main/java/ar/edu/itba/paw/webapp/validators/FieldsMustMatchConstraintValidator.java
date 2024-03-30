package ar.edu.itba.paw.webapp.validators;

import ar.edu.itba.paw.webapp.validators.interfaces.FieldsMustMatchConstraint;
import org.springframework.beans.BeanWrapperImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class FieldsMustMatchConstraintValidator implements ConstraintValidator<FieldsMustMatchConstraint, Object> {
    private String first, second;
    @Override
    public void initialize(FieldsMustMatchConstraint constraintAnnotation) {
        this.first = constraintAnnotation.first();
        this.second = constraintAnnotation.second();
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        BeanWrapperImpl wrapper = new BeanWrapperImpl(o);
        Object firstO = wrapper.getPropertyValue(first);
        Object secondO = wrapper.getPropertyValue(second);
        return firstO.equals(secondO);
    }
}
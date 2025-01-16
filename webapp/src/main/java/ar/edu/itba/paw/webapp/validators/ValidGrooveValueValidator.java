package ar.edu.itba.paw.webapp.validators;

import ar.edu.itba.paw.models.GroovyEnum;
import ar.edu.itba.paw.webapp.validators.interfaces.ValidGrooveValue;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidGrooveValueValidator implements ConstraintValidator<ValidGrooveValue, Integer> {
    @Override
    public void initialize(ValidGrooveValue constraintAnnotation) {
    }

    @Override
    public boolean isValid(Integer s, ConstraintValidatorContext constraintValidatorContext) {
        return GroovyEnum.UP.getValue() == s.intValue() || GroovyEnum.DOWN.getValue() == s.intValue();
    }
}

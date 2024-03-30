package ar.edu.itba.paw.webapp.validators;


import ar.edu.itba.paw.services.UserService;
import ar.edu.itba.paw.webapp.validators.interfaces.UniqueEmailConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueEmailConstraintValidator implements ConstraintValidator<UniqueEmailConstraint, String> {

    @Autowired
    private UserService us;

    @Override
    public void initialize(UniqueEmailConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return !us.findByEmail(s).isPresent();
    }
}

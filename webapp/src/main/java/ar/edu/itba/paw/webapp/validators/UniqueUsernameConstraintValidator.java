package ar.edu.itba.paw.webapp.validators;


import ar.edu.itba.paw.services.UserService;

import ar.edu.itba.paw.webapp.validators.interfaces.UniqueUsernameConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueUsernameConstraintValidator implements ConstraintValidator<UniqueUsernameConstraint, String> {

    @Autowired
    private UserService us;

    @Override
    public void initialize(UniqueUsernameConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return !us.findByUsername(s).isPresent();
    }
}

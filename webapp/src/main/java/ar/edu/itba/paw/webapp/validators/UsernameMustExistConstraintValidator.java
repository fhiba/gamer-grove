package ar.edu.itba.paw.webapp.validators;

import ar.edu.itba.paw.services.UserService;
import ar.edu.itba.paw.webapp.validators.interfaces.UsernameMustExistConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UsernameMustExistConstraintValidator implements ConstraintValidator<UsernameMustExistConstraint, String> {

    @Autowired
    private UserService us;

    @Override
    public void initialize(UsernameMustExistConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return us.findByUsername(s).isPresent();
    }
}
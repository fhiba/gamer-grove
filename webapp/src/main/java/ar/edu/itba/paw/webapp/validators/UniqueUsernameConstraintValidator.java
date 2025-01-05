package ar.edu.itba.paw.webapp.validators;

import ar.edu.itba.paw.services.UserService;
import ar.edu.itba.paw.webapp.controller.UserController;
import ar.edu.itba.paw.webapp.validators.interfaces.UniqueUsernameConstraint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueUsernameConstraintValidator implements ConstraintValidator<UniqueUsernameConstraint, String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UniqueUsernameConstraintValidator.class);

    @Autowired
    private UserService us;

    @Override
    public void initialize(UniqueUsernameConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        LOGGER.atInfo().log("Validating username: {}", s);
        return us.findByUsername(s).isEmpty();
    }
}

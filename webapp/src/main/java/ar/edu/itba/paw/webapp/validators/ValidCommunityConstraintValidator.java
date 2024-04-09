package ar.edu.itba.paw.webapp.validators;

import ar.edu.itba.paw.exceptions.NoSuchCommunityException;
import ar.edu.itba.paw.services.CommunityService;
import ar.edu.itba.paw.webapp.validators.interfaces.ValidCommunityConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;

public class ValidCommunityConstraintValidator  implements ConstraintValidator<ValidCommunityConstraint, String>{

    @Autowired
    private CommunityService communityService;

    @Override
    public void initialize(ValidCommunityConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(String s, javax.validation.ConstraintValidatorContext constraintValidatorContext) {
        try {
            communityService.findByName(s);
        } catch (NoSuchCommunityException e) {
            return false;
        }
        return true;
    }
}

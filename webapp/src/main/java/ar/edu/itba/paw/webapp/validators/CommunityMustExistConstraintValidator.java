package ar.edu.itba.paw.webapp.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import ar.edu.itba.paw.services.CommunityService;
import ar.edu.itba.paw.webapp.validators.interfaces.CommunityMustExistConstraint;

public class CommunityMustExistConstraintValidator implements ConstraintValidator<CommunityMustExistConstraint, Long> {

    @Autowired
    private CommunityService cs;

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        try {
            cs.findById(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

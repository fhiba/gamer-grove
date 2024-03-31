package ar.edu.itba.paw.webapp.validators;

import ar.edu.itba.paw.models.PostCategories;
import ar.edu.itba.paw.webapp.validators.interfaces.ValidPostCategoryConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;


public class ValidPostCategoryConstraintValidator implements ConstraintValidator<ValidPostCategoryConstraint, String> {

    @Override
    public void initialize(ValidPostCategoryConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return Arrays.stream(PostCategories.values()).anyMatch(category -> category.getCategory().equals(s));
    }
}

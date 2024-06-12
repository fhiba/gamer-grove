package ar.edu.itba.paw.webapp.validators;

import ar.edu.itba.paw.models.CommunityCategories;
import ar.edu.itba.paw.webapp.validators.interfaces.ValidCommunityCategoriesConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

public class ValidCommunityCategoriesConstraintValidator implements ConstraintValidator<ValidCommunityCategoriesConstraint, String> {

    public void initialize(ValidCommunityCategoriesConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null) {
            return true;
        }
        List<CommunityCategories> categories = Arrays.stream(CommunityCategories.values()).toList();
        for (String category : s.split(",")) {
            if (category == null || category.isEmpty()) {
                break;
            }
            if (categories.stream().noneMatch(c -> c.getCategory().equals(category))) {
                return false;
            }
        }
        return true;
    }
}

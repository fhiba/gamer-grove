package ar.edu.itba.paw.webapp.validators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import ar.edu.itba.paw.models.CommunityCategories;
import ar.edu.itba.paw.webapp.validators.interfaces.ValidCommunityCategoriesListConstraint;

public class ValidCommunityCategoriesListConstraintValidator
        implements ConstraintValidator<ValidCommunityCategoriesListConstraint, List<String>> {
    private static final List<String> categories = new ArrayList<>(
            Arrays.stream(CommunityCategories.values()).map(CommunityCategories::getCategory)
                    .collect(Collectors.toList()));

    @Override
    public boolean isValid(List<String> value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return value.stream().allMatch(s -> s != null && !s.isEmpty() && categories.contains(s));
    }

}

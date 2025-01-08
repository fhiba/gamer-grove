package ar.edu.itba.paw.webapp.validators;

import java.util.Arrays;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import ar.edu.itba.paw.webapp.validators.interfaces.MustBeAcceptedLanguage;

public class MustBeAcceptedLanguageConstraintValidator implements ConstraintValidator<MustBeAcceptedLanguage, String> {

    private static final String[] ACCEPTED_LANGUAGES = { "en", "es" };

    @Override
    public void initialize(MustBeAcceptedLanguage constraintAnnotation) {
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null) {
            return false;
        }

        return Arrays.asList(ACCEPTED_LANGUAGES).contains(s);

    }
}

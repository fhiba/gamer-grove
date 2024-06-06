package ar.edu.itba.paw.webapp.validators;

import ar.edu.itba.paw.webapp.validators.interfaces.FileMustBeImageConstraint;
import ar.edu.itba.paw.webapp.validators.interfaces.MaxFileSizeConstraint;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class MaxFileSizeConstraintValidator implements ConstraintValidator<MaxFileSizeConstraint, MultipartFile> {

    private static final long MAX_SIZE = 1024 * 1024 * 5;

    @Override
    public void initialize(MaxFileSizeConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext constraintValidatorContext) {
        //menor a 10mb
        return multipartFile.getSize() < MAX_SIZE;
    }
}

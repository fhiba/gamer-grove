package ar.edu.itba.paw.webapp.validators;

import ar.edu.itba.paw.webapp.validators.interfaces.MaxFileSizeConstraint;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class MaxFileSizeConstraintValidator
        implements ConstraintValidator<MaxFileSizeConstraint, InputStream> {

    private static final long MAX_SIZE = 1024 * 1024 * 5;

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory
            .getLogger(MaxFileSizeConstraintValidator.class);

    @Override
    public void initialize(MaxFileSizeConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(InputStream s,
            ConstraintValidatorContext constraintValidatorContext) {
        // menor a 10mb
        try {
            long size = s.available();
            LOGGER.info("File size to validate: {}", size);
            return size < MAX_SIZE;
        } catch (IOException e) {
            return false;
        }

    }
}

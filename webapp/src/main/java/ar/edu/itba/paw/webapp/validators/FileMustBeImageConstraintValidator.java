package ar.edu.itba.paw.webapp.validators;

import ar.edu.itba.paw.webapp.validators.interfaces.FileMustBeImageConstraint;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.springframework.http.MediaType;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class FileMustBeImageConstraintValidator
        implements ConstraintValidator<FileMustBeImageConstraint, FormDataBodyPart> {

    @Override
    public void initialize(FileMustBeImageConstraint constraintAnnotation) {
    }

    private static final int MAX_FILE_SIZE = (int) 5 * 1000 * 1000;

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory
            .getLogger(FileMustBeImageConstraintValidator.class);

    @Override
    public boolean isValid(FormDataBodyPart m, ConstraintValidatorContext constraintValidatorContext) {
        if (m == null)
            return false;
        LOGGER.info("File size to validate: {}", m.getContentDisposition().getSize());
        String type = m.getMediaType().toString();
        LOGGER.info("File type to validate: {}", type);
        return m != null
                && (type.toString().equals(MediaType.IMAGE_JPEG_VALUE)
                        || type.toString().equals(MediaType.IMAGE_PNG_VALUE)
                        || type.toString().equals(MediaType.IMAGE_GIF_VALUE));
    }
}

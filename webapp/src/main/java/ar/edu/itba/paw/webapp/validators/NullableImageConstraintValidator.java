package ar.edu.itba.paw.webapp.validators;

import ar.edu.itba.paw.webapp.validators.interfaces.NullableImageConstraint;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NullableImageConstraintValidator implements ConstraintValidator<NullableImageConstraint, MultipartFile> {

    @Override
    public void initialize(NullableImageConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(MultipartFile m, ConstraintValidatorContext constraintValidatorContext) {
        return m.isEmpty() || (m.getContentType().equals(MediaType.IMAGE_JPEG_VALUE) || m.getContentType().equals(MediaType.IMAGE_PNG_VALUE) || m.getContentType().equals(MediaType.IMAGE_GIF_VALUE));
    }
}

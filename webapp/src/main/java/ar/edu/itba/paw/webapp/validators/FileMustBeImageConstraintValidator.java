package ar.edu.itba.paw.webapp.validators;

import ar.edu.itba.paw.webapp.validators.interfaces.FileMustBeImageConstraint;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class FileMustBeImageConstraintValidator implements ConstraintValidator<FileMustBeImageConstraint, MultipartFile> {

    @Override
    public void initialize(FileMustBeImageConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(MultipartFile m, ConstraintValidatorContext constraintValidatorContext) {
        return m.isEmpty()  || (m.getContentType().equals(MediaType.IMAGE_JPEG_VALUE) || m.getContentType().equals(MediaType.IMAGE_PNG_VALUE) || m.getContentType().equals(MediaType.IMAGE_GIF_VALUE));
    }
}

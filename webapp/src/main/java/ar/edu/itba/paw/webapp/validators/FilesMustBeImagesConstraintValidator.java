package ar.edu.itba.paw.webapp.validators;

import ar.edu.itba.paw.webapp.validators.interfaces.FilesMustBeImagesConstraint;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class FilesMustBeImagesConstraintValidator implements ConstraintValidator<FilesMustBeImagesConstraint, MultipartFile[]> {

    @Override
    public void initialize(FilesMustBeImagesConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(MultipartFile[] m, ConstraintValidatorContext constraintValidatorContext) {
        for (MultipartFile file : m) {
            if (file == null || !file.isEmpty() && !(file.getContentType().equals(MediaType.IMAGE_JPEG_VALUE) || file.getContentType().equals(MediaType.IMAGE_PNG_VALUE) || file.getContentType().equals(MediaType.IMAGE_GIF_VALUE))) {
                return false;
            }
        }
        return true;
    }
}

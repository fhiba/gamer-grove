package ar.edu.itba.paw.webapp.validators;

import ar.edu.itba.paw.webapp.validators.interfaces.FilesMustBeImagesConstraint;

import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.ContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class FilesMustBeImagesConstraintValidator
        implements ConstraintValidator<FilesMustBeImagesConstraint, FormDataBodyPart> {

    @Override
    public void initialize(FilesMustBeImagesConstraint constraintAnnotation) {
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(FilesMustBeImagesConstraintValidator.class);

    @Override
    public boolean isValid(FormDataBodyPart m, ConstraintValidatorContext constraintValidatorContext) {
        if (m == null || m.getParent() == null) {
            return true;
        }
        for (BodyPart part : m.getParent().getBodyParts()) {
            LOGGER.info("imageDetail {}", part.getContentDisposition().getType());
            String type = part.getContentDisposition().getType();

            ContentDisposition meta = part.getContentDisposition();
            if (meta.getFileName() != null
                    && (type == null || type != MediaType.IMAGE_GIF_VALUE || type != MediaType.IMAGE_PNG_VALUE
                            || MediaType.IMAGE_JPEG_VALUE != type)) {
                return false;
            }

        }

        return true;
    }

}

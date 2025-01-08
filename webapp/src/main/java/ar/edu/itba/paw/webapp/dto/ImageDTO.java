package ar.edu.itba.paw.webapp.dto;

import javax.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

import ar.edu.itba.paw.webapp.validators.interfaces.NullableImageConstraint;

public class ImageDTO {

    @NotNull
    @NullableImageConstraint
    private MultipartFile file;

}

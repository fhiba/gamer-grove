package ar.edu.itba.paw.webapp.dto;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.UriInfo;

import ar.edu.itba.paw.models.File;
import ar.edu.itba.paw.models.User;
import org.springframework.web.multipart.MultipartFile;

import ar.edu.itba.paw.webapp.validators.interfaces.NullableImageConstraint;

import java.net.URI;
import java.util.function.Function;

public class ImageDTO {

    private URI self;


    private byte[]  imageFile;

    public ImageDTO() {
    }

    public static Function<File, ImageDTO> mapper(UriInfo uriInfo) {
        return u -> fromFile(uriInfo, u);
    }

    public static ImageDTO fromFile(UriInfo uriInfo, File file) {
        ImageDTO imageDTO = new ImageDTO();
        imageDTO.imageFile = file.getFile();
        imageDTO.self = uriInfo.getBaseUriBuilder()
                .path("images").path(String.valueOf(file.getImageId())).build();

        return imageDTO;
    }

}

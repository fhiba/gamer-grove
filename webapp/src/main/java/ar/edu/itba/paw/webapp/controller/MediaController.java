package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.exceptions.NoLoggedUserException;
import ar.edu.itba.paw.exceptions.NoSuchImageException;
import ar.edu.itba.paw.exceptions.UserNotFoundException;
import ar.edu.itba.paw.models.File;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.services.FileService;
import ar.edu.itba.paw.services.UserService;
import ar.edu.itba.paw.webapp.dto.ImageDTO;
import ar.edu.itba.paw.webapp.dto.UserDTO;
import ar.edu.itba.paw.webapp.mediaType.VendorType;
import ar.edu.itba.paw.webapp.validators.interfaces.FileMustBeImageConstraint;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Size;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Optional;

@Component
@Path("/api/images")
public class MediaController {

    @Autowired
    private UserService us;

    @Autowired
    private FileService fs;

    @Context
    private UriInfo uriInfo;

    private final static int MAX_FILE_SIZE = (int) 5 * 1000 * 1000;




    @GET
    @Path("/{id}")
    @Produces(value = { "images/jpeg","images/png","images/jpg","images/gif" })
    public Response getById(@PathParam("id") final long id) throws NoSuchImageException {
        final Optional<File> maybeFile = fs.getFile(id);
        if (maybeFile.isPresent()) {
            File file = maybeFile.get();
            return Response.ok(file.getFile()).build();
        }
        throw new NoSuchImageException();
    }


    @POST
    @Consumes(value = { MediaType.MULTIPART_FORM_DATA })
    public Response createImage(@Size(max = MAX_FILE_SIZE, message = "{FileSize}") @FormDataParam("image") byte[] bytes,
                                       @FileMustBeImageConstraint(message = "{Image}") @FormDataParam("image") final FormDataBodyPart fileDetails)
            throws NoLoggedUserException {

        us.updateProfile(null, bytes);

        File file = us.getLoggedUser().get().getImage();
        URI uri = uriInfo.getBaseUriBuilder()
                .path("images")
                .path(String.valueOf(file.getImageId()))
                .build();
        return Response.ok()
                .contentLocation(uri)
                .build();
    }

}

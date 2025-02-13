package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.exceptions.IllegalPageException;
import ar.edu.itba.paw.exceptions.NoLoggedUserException;
import ar.edu.itba.paw.exceptions.NoSuchImageException;
import ar.edu.itba.paw.exceptions.NoSuchTokenException;
import ar.edu.itba.paw.exceptions.PageNotFoundException;
import ar.edu.itba.paw.exceptions.UserNotFoundException;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.pagination.PaginatedDataWrapper;
import ar.edu.itba.paw.models.pagination.PaginationRequest;
import ar.edu.itba.paw.services.CommunityService;
import ar.edu.itba.paw.services.ModderService;
import ar.edu.itba.paw.services.PostService;
import ar.edu.itba.paw.services.UserService;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Component;
import ar.edu.itba.paw.webapp.dto.UserDTO;
import ar.edu.itba.paw.webapp.dto.EmailDTO;
import ar.edu.itba.paw.webapp.dto.UserCreationDTO;
import ar.edu.itba.paw.webapp.dto.ResetPasswordDTO;
import ar.edu.itba.paw.webapp.dto.LocaleDTO;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.*;

import ar.edu.itba.paw.webapp.mediaType.VendorType;
import ar.edu.itba.paw.webapp.validators.interfaces.FileMustBeImageConstraint;

@Path("/api/users")
@Component
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private final static int MAX_FILE_SIZE = (int) 5 * 1000 * 1000;
    @Autowired
    private UserService us;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private ModderService md;
    @Autowired
    private CommunityService cs;
    @Autowired
    private PostService ps;

    @Context
    private UriInfo uriInfo;

    @POST
    @Consumes(value = { VendorType.APPLICATION_USER })
    public Response createUser(@Valid @NotNull final UserCreationDTO userDto) throws UserNotFoundException {

        final User user = us.create(userDto.getUsername(), userDto.getEmail(), userDto.getPassword());
        final URI uri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(user.getId())).build();

        return Response.created(uri).build();
    }

    @POST
    @Path("/{id}/profile-picture")
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

    // Hay que hacer el Access Control para que solo puedan entrar si ya tiene una
    // image
    // con pedro lo decidimos asi por motivos REST ya que no se esta creando una
    // nueva entidad
    @PUT
    @Path("/{id}/profile-picture")
    @Consumes(value = { MediaType.MULTIPART_FORM_DATA })
    public Response updateImage(@Size(max = MAX_FILE_SIZE, message = "{FileSize}") @FormDataParam("image") byte[] bytes,
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

    @GET
    @Path("/{id}/profile-picture")
    @Produces(value = { "images/jpeg", "images/png", "images/jpg", "images/gif" })
    public Response getProfilePicture(@PathParam("id") final long id)
            throws NoSuchImageException, UserNotFoundException {
        final User user = us.findById(id).orElseThrow(UserNotFoundException::new);
        final File profilePicture = user.getImage();
        if (profilePicture == null) {
            throw new NoSuchImageException();
        }
        return Response.ok(profilePicture.getFile()).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listUsers(@Context UriInfo uriInfo, @QueryParam("page") @DefaultValue("1") final int page)
            throws PageNotFoundException, IllegalPageException {

        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setPageNumber(page);
        PaginatedDataWrapper<User> users = us.listUsers(paginationRequest);

        if (users.getData().size() == 0) {
            return Response.noContent().build();
        }
        List<UserDTO> userDTOs = users.getData().stream()
                .map(UserDTO.mapper(uriInfo))
                .toList();

        return Response.ok(new GenericEntity<>(userDTOs) {
        })
                .link(uriInfo.getAbsolutePathBuilder().queryParam("page", users.getFirstPage()).build(), "first")
                .link(uriInfo.getAbsolutePathBuilder().queryParam("page", users.getTotalPages()).build(), "last")
                .link(uriInfo.getAbsolutePathBuilder().queryParam("page", users.getPreviousPage()).build(), "prev")
                .link(uriInfo.getAbsolutePathBuilder().queryParam("page", users.getNextPage()).build(), "next")
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = { VendorType.APPLICATION_USER, })
    public Response getById(@PathParam("id") final long id) throws UserNotFoundException {
        final Optional<User> maybeUser = us.findById(id);
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();
            return Response.ok(UserDTO.fromUser(uriInfo, user)).build();
        }
        throw new UserNotFoundException();
    }

    @POST
    @Path("/reset-password-token")
    @Consumes(value = { VendorType.APPLICATION_EMAIL })
    public Response resetPasswordToken(@Valid final EmailDTO emailDTO) {
        try {
            us.startResetPassword(emailDTO.getEmail());
        } catch (UserNotFoundException e) {
            // No hay que devolver nada (por cuestiones de seguridad)
        }
        return Response.ok().build();
    }

    @PATCH
    @Path("/{id}")
    @Consumes(value = { VendorType.APPLICATION_PASSWORD_RESET })
    public Response resetPassword(@PathParam("id") final long id, @Valid final ResetPasswordDTO resetPasswordDTO)
            throws NoSuchTokenException, UserNotFoundException {
        us.resetPassword(resetPasswordDTO.getToken(), resetPasswordDTO.getPassword());
        return Response
                .ok()
                .build();
    }

    @PUT
    @Path("/{id}/locale")
    @Consumes(value = { VendorType.APPLICATION_LOCALE })
    public Response updateLocale(@PathParam("id") final long id,
            @Valid final LocaleDTO localeDTO) throws NoLoggedUserException {
        us.updateProfile(localeDTO.getLocale(), null);
        return Response.ok().build();
    }

    @POST
    @Path("/{id}/verification-token")
    public Response sendVerificationToken(@PathParam("id") final long id)
            throws NoLoggedUserException, UserNotFoundException {
        us.resendVerification();

        return Response.ok().build();
    }

}

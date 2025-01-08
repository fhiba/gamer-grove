package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.exceptions.AlreadyModException;
import ar.edu.itba.paw.exceptions.NoLoggedUserException;
import ar.edu.itba.paw.exceptions.NoSuchCommunityException;
import ar.edu.itba.paw.exceptions.UserNotFoundException;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.pagination.PaginatedDataWrapper;
import ar.edu.itba.paw.models.pagination.PaginationRequest;
import ar.edu.itba.paw.services.CommunityService;
import ar.edu.itba.paw.services.ModderService;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.services.PostService;
import ar.edu.itba.paw.services.UserService;
import ar.edu.itba.paw.webapp.form.LogInForm;
import ar.edu.itba.paw.webapp.form.NewModForm;
import ar.edu.itba.paw.webapp.form.RegisterUserForm;
import ar.edu.itba.paw.webapp.form.RemoveModForm;
import ar.edu.itba.paw.webapp.form.*;
import org.glassfish.jersey.internal.guava.Lists;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.validation.BindingResult;
import ar.edu.itba.paw.webapp.dto.UserDTO;
import ar.edu.itba.paw.webapp.dto.EmailDTO;
import ar.edu.itba.paw.webapp.dto.ErrorDTO;
import ar.edu.itba.paw.webapp.dto.MessageDTO;
import ar.edu.itba.paw.webapp.dto.UserCreationDTO;
import ar.edu.itba.paw.webapp.dto.ResetPasswordDTO;
import ar.edu.itba.paw.webapp.dto.LocaleDTO;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import ar.edu.itba.paw.webapp.mediaType.VendorType;
import ar.edu.itba.paw.webapp.validators.interfaces.FileMustBeImageConstraint;
import ar.edu.itba.paw.webapp.validators.interfaces.MaxFileSizeConstraint;

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

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listUsers(@Context UriInfo uriInfo, @QueryParam("page") @DefaultValue("1") final int page) {
        if (page < 0)
            return Response.status(Response.Status.BAD_REQUEST).build();
        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setPageNumber(page);
        try {
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
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @GET
    @Path("/{id}")
    @Produces(value = { VendorType.APPLICATION_USER, })
    public Response getById(@PathParam("id") final long id) {
        final Optional<User> maybeUser = us.findById(id);
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();
            return Response.ok(UserDTO.fromUser(uriInfo, user)).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    @Path("/reset-password-token")
    @Consumes(value = { VendorType.APPLICATION_EMAIL })
    public Response resetPasswordToken(@Valid final EmailDTO emailDTO) {
        try {
            us.startResetPassword(emailDTO.getEmail());
        } catch (UserNotFoundException e) {

        }
        return Response.ok().entity(new MessageDTO("Email sent")).build();
    }

    @PATCH
    @Path("/{id}")
    @Consumes(value = { VendorType.APPLICATION_PASSWORD_RESET })
    public Response resetPassword(@PathParam("id") final long id, @Valid final ResetPasswordDTO resetPasswordDTO) {
        try {
            us.resetPassword(resetPasswordDTO.getToken(), resetPasswordDTO.getPassword());
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.ok().entity(new MessageDTO("Password reset")).build();
    }


    @PATCH
    @Path("/{id}")
    @Consumes(value = { VendorType.APPLICATION_LOCALE })
    public Response updateLocale(@PathParam("id") final long id,
        @Valid final LocaleDTO localeDTO) throws NoLoggedUserException {
         final Optional<User> maybeUser = us.findById(id);
        if (maybeUser.isEmpty()) {
            LOGGER.atError().setMessage("User not found").log();
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        final User user = maybeUser.get();
        us.updateProfile(localeDTO.getLocale(),null);
        LOGGER.info("PUT /{}: User {} locale updated", uriInfo.getPath(), user.getUsername());
        return Response.ok().entity(new MessageDTO("Locale updated")).build();

    }

    @POST
    @Path("/{id}/verification-token")
    public Response sendVerificationToken(@PathParam("id") final long id) {
        try {
            us.resendVerification();

        } catch (NoLoggedUserException e) {
            LOGGER.atError().setMessage("No user logged in the resend verification request").log();

            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (UserNotFoundException e) {
            LOGGER.atError().setMessage("No user found in the resend verification request").log();
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok().entity(new MessageDTO("Verification email sent")).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(value = { MediaType.MULTIPART_FORM_DATA })
    public Response updateProfileImage(@PathParam("id") final long id,
            @Size(max = MAX_FILE_SIZE, message = "{FileSize.image}") @FormDataParam("image") byte[] bytes,
            @FileMustBeImageConstraint(message = "{Image}") @FormDataParam("image") final FormDataBodyPart fileDetails)
            throws NoLoggedUserException {

        final Optional<User> maybeUser = us.findById(id);
        if (maybeUser.isEmpty()) {
            LOGGER.atError().setMessage("User not found").log();
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        final User user = maybeUser.get();
        us.updateProfile(null, bytes);

        LOGGER.info("PUT /{}: User {} image updated", uriInfo.getPath(), user.getUsername());
        File file = user.getImage();
        LOGGER.info("image is null? {}", Objects.isNull(file));
        URI uri = uriInfo.getBaseUriBuilder()
                .path("images")
                .path(String.valueOf(user.getImage().getImageId()))
                .build();
        return Response.ok()
                .contentLocation(uri)
                .build();
    }

    @DELETE
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response deleteById(@PathParam("id") final long id) {
        // TODO: implement method
        // us.deleteById(id);
        return Response.noContent().build();
    }

}

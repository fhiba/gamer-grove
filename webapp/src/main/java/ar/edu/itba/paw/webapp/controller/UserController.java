package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.exceptions.AlreadyModException;
import ar.edu.itba.paw.exceptions.IllegalPageException;
import ar.edu.itba.paw.exceptions.NoLoggedUserException;
import ar.edu.itba.paw.exceptions.NoSuchCommunityException;
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
            throws NoSuchTokenException {
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

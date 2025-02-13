package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.exceptions.AlreadyFollowedException;
import ar.edu.itba.paw.exceptions.AlreadyRatedCommunityException;
import ar.edu.itba.paw.exceptions.CommunityNotFollowedException;
import ar.edu.itba.paw.exceptions.IllegalPageException;
import ar.edu.itba.paw.exceptions.NoLoggedUserException;
import ar.edu.itba.paw.exceptions.NoSuchCommunityException;
import ar.edu.itba.paw.exceptions.NoSuchImageException;
import ar.edu.itba.paw.exceptions.NoSuchRatingException;
import ar.edu.itba.paw.exceptions.NotRatedCommunityException;
import ar.edu.itba.paw.exceptions.PageNotFoundException;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.pagination.PaginatedDataWrapper;
import ar.edu.itba.paw.models.pagination.PaginationRequest;
import ar.edu.itba.paw.webapp.dto.CategoryDTO;
import ar.edu.itba.paw.webapp.dto.CommunityDTO;
import ar.edu.itba.paw.webapp.dto.CommunityInfoDTO;
import ar.edu.itba.paw.webapp.dto.GiveRatingDTO;
import ar.edu.itba.paw.webapp.dto.RatingDTO;
import ar.edu.itba.paw.webapp.validators.interfaces.FileMustBeImageConstraint;
import ar.edu.itba.paw.services.*;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.net.URI;

@Component
@Path("/api/communities")
public class CommunityController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommunityController.class);
    private static final List<String> categories = new ArrayList<>(
            Arrays.stream(CommunityCategories.values()).map(CommunityCategories::getCategory)
                    .collect(Collectors.toList()));

    @Autowired
    private CommunityService cs;
    @Autowired
    private PostService ps;
    @Autowired
    private FileService fs;
    @Autowired
    private ModderService ms;
    @Autowired
    private UserService us;
    @Autowired
    private RatingService rs;

    private final static int MAX_FILE_SIZE = (int) 5 * 1000 * 1000;

    @Context
    private UriInfo uriInfo;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listCommunities(@Context UriInfo uriInfo, @QueryParam("page") @DefaultValue("1") final int page,
            @QueryParam("query") @DefaultValue("") final String query,
            @QueryParam("categories") @DefaultValue("") final String categories,
            @QueryParam("followedBy") final int followedBy)
            throws PageNotFoundException, IllegalPageException {
        LOGGER.info("GET /communities with followedBy: {}", followedBy);
        List<String> selectedCategories = Arrays.asList(categories.split(","));
        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setPageNumber(page);
        PaginatedDataWrapper<Community> communities = cs.find(paginationRequest, query, selectedCategories,
                Long.valueOf(followedBy));

        if (communities.getData().size() == 0) {
            return Response.noContent().build();
        }
        List<CommunityDTO> communityDTOs = communities.getData().stream()
                .map(CommunityDTO.mapper(uriInfo))
                .toList();

        return Response.ok(new GenericEntity<>(communityDTOs) {
        })
                .link(uriInfo.getAbsolutePathBuilder().queryParam("page", communities.getFirstPage()).build(), "first")
                .link(uriInfo.getAbsolutePathBuilder().queryParam("page", communities.getTotalPages()).build(), "last")
                .link(uriInfo.getAbsolutePathBuilder().queryParam("page", communities.getPreviousPage()).build(),
                        "prev")
                .link(uriInfo.getAbsolutePathBuilder().queryParam("page", communities.getNextPage()).build(), "next")
                .build();
    }

    @GET
    @Path("/{communityName}")
    public Response getCommunity(@PathParam("communityName") final String communityName)
            throws NoSuchCommunityException {
        final Community community = cs.findByName(communityName);
        LOGGER.info("GET /communities/{}", community.getName());
        return Response.ok(CommunityDTO.fromCommunity(uriInfo, community)).build();
    }

    @POST
    public Response createCommunity(
            @Size(max = MAX_FILE_SIZE, message = "{FileSize}") @FormDataParam("image") byte[] bytes,
            @FileMustBeImageConstraint(message = "{Image}") @FormDataParam("image") final FormDataBodyPart fileDetails,
            @NotBlank @Pattern(regexp = "^[a-zA-Z0-9_. -]*$") @FormDataParam("name") final String name,
            @FormDataParam("description") final String description,
            @FormDataParam("categories") final String categories,
            @FormDataParam("publisher") final String publisher,
            @FormDataParam("developer") final String developer) throws NoSuchCommunityException {
        LOGGER.info("POST /communities");
        Optional<Community> community = cs.createCommunity(name, description, categories, developer, publisher,
                LocalDateTime.now(), bytes);
        final URI uri = uriInfo.getAbsolutePathBuilder().path("communities")
                .path(String.valueOf(community.get().getId()))
                .build();

        return Response.created(uri).build();

    }

    @POST
    @Path("/{communityName}/followers")
    public Response followCommunity(@PathParam("communityName") final String communityName)
            throws NoLoggedUserException, NoSuchCommunityException, AlreadyFollowedException {
        cs.followCommunity(communityName);
        return Response.noContent().build();
    }

    @DELETE
    @Path("/{communityName}/followers/{userId}")
    public Response unfollowCommunity(@PathParam("communityName") final String communityName)
            throws NoLoggedUserException, NoSuchCommunityException, CommunityNotFollowedException {
        cs.unfollowCommunity(communityName);
        return Response.noContent().build();
    }

    @POST
    @Path("/{communityName}/ratings")
    public Response rateCommunity(@PathParam("communityName") final String communityName,
            @Valid @NotNull final GiveRatingDTO ratingDTO)
            throws NoSuchCommunityException, NoLoggedUserException, AlreadyRatedCommunityException {

        Rating rating = cs.giveRating(communityName, ratingDTO.getRating());

        URI uri = uriInfo.getAbsolutePathBuilder()
                .path(rating.getUser().getId().toString())
                .build();

        return Response.created(uri).build();

    }

    @PUT
    @Path("/{communityName}/ratings/{userId}")
    public Response updateRating(@PathParam("communityName") final String communityName,
            @PathParam("userId") final long userId, @Valid @NotNull final GiveRatingDTO ratingDTO)
            throws NoSuchCommunityException, NoLoggedUserException, NoSuchRatingException, NotRatedCommunityException {
        cs.updateRating(communityName, ratingDTO.getRating());
        return Response.noContent().build();
    }

    @DELETE
    @Path("/{communityName}/ratings/{userId}")
    public Response deleteRating(@PathParam("communityName") final String communityName,
            @PathParam("userId") final long userId)
            throws NoSuchCommunityException, NoLoggedUserException, NoSuchRatingException, NotRatedCommunityException {
        cs.deleteRating(communityName);
        return Response.noContent().build();
    }

    @GET
    @Path("/{communityName}/ratings/{userId}")
    public Response getUserRating(@PathParam("communityName") final String communityName,
            @PathParam("userId") final long userId)
            throws NoSuchCommunityException, NoSuchRatingException, NoLoggedUserException {
        Rating rating = cs.getRatingFromLoggedUser(communityName);
        return Response.ok(RatingDTO.fromRating(uriInfo, rating)).build();
    }

    @GET
    @Path("/{communityName}/portrait")
    @Produces(value = { "images/jpeg", "images/png", "images/jpg", "images/gif" })
    public Response getPortrait(@PathParam("communityName") final String communityName)
            throws NoSuchCommunityException, NoSuchImageException {
        final Community c = cs.findByName(communityName);
        final File portrait = c.getPortrait();
        if (portrait == null) {
            throw new NoSuchImageException();
        }
        return Response.ok(portrait.getFile()).build();
    }

    @POST
    @Path("/{communityName}/portrait")
    @Consumes(value = { MediaType.MULTIPART_FORM_DATA })
    public Response createImage(@Size(max = MAX_FILE_SIZE, message = "{FileSize}") @FormDataParam("image") byte[] bytes,
            @FileMustBeImageConstraint(message = "{Image}") @FormDataParam("image") final FormDataBodyPart fileDetails,
            @PathParam("communityName") final String communityName)
            throws NoSuchCommunityException {

        cs.updatePortrait(communityName, bytes);

        URI uri = uriInfo.getBaseUriBuilder()
                .path("api")
                .path("communities")
                .path(communityName)
                .path("portrait")
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
    @Path("/{communityName}/portrait")
    @Consumes(value = { MediaType.MULTIPART_FORM_DATA })
    public Response updateImage(@Size(max = MAX_FILE_SIZE, message = "{FileSize}") @FormDataParam("image") byte[] bytes,
            @FileMustBeImageConstraint(message = "{Image}") @FormDataParam("image") final FormDataBodyPart fileDetails,
            @PathParam("communityName") final String communityName)
            throws NoSuchCommunityException {
        cs.updatePortrait(communityName, bytes);

        URI uri = uriInfo.getBaseUriBuilder()
                .path("api")
                .path("communities")
                .path(communityName)
                .path("portrait")
                .build();
        return Response.ok()
                .contentLocation(uri)
                .build();
    }

    @PATCH
    @Path("/{communityName}")
    public Response editCommunityInfo(@PathParam("communityName") final String communityName,
            @Valid @NotNull(message = "{NotNull}") CommunityInfoDTO communityInfoDto) throws NoSuchCommunityException {
        cs.editCommunityInfo(communityName, communityInfoDto.getDescription(),
                communityInfoDto.getPublisher(), communityInfoDto.getDeveloper(), null,
                Objects.isNull(communityInfoDto.getCategories()) ? null
                        : String.join(",", communityInfoDto.getCategories()));
        return Response.noContent().build();
    }

    @GET
    @Path("/categories")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCategories() {
        List<CategoryDTO> categoriesDtos = categories.stream().map(CategoryDTO::fromCategory).toList();
        return Response.ok(new GenericEntity<>(categoriesDtos) {
        }).build();
    }
}

package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.exceptions.AlreadyFollowedException;
import ar.edu.itba.paw.exceptions.AlreadyRatedCommunityException;
import ar.edu.itba.paw.exceptions.CommunityNotFollowedException;
import ar.edu.itba.paw.exceptions.IllegalPageException;
import ar.edu.itba.paw.exceptions.NoLoggedUserException;
import ar.edu.itba.paw.exceptions.NoSuchCommunityException;
import ar.edu.itba.paw.exceptions.NoSuchRatingException;
import ar.edu.itba.paw.exceptions.NotRatedCommunityException;
import ar.edu.itba.paw.exceptions.PageNotFoundException;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.pagination.PaginatedDataWrapper;
import ar.edu.itba.paw.models.pagination.PaginationRequest;
import ar.edu.itba.paw.webapp.dto.CommunityDTO;
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

import java.net.URI;

@Component
@Path("/api/communities")
public class CommunityController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommunityController.class);

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

    // @RequestMapping(path = "/new-community", method = RequestMethod.GET)
    // public ModelAndView newCommunity(@ModelAttribute("newCommunityForm") final
    // NewCommunityForm newCommunityForm) {
    //
    // ModelAndView mav = new ModelAndView("community/newCommunity");
    // Optional<User> maybeUser = us.getLoggedUser();
    // List<Community> communities;
    // Boolean isAdmin = false;
    // if (maybeUser.isPresent()) {
    // User user = maybeUser.get();
    // communities = cs.getFollowedCommunities(user);
    // isAdmin = user.getOwner();
    // } else {
    // communities = cs.getAllCommunities();
    // }
    // mav.addObject("isAdmin", isAdmin);
    // mav.addObject("isLogged", maybeUser.isPresent());
    // mav.addObject("communities", communities);
    // mav.addObject("categories",
    // Arrays.stream(CommunityCategories.values()).map(CommunityCategories::getCategory)
    // .toArray(String[]::new));
    // return mav;
    // }
    //
    // @RequestMapping(path = "/new-community", method = RequestMethod.POST)
    // public ModelAndView createCommunity(
    // @Valid @ModelAttribute("newCommunityForm") final NewCommunityForm
    // newCommunityForm, BindingResult errors)
    // throws NoSuchCommunityException {
    // if (errors.hasErrors())
    // return newCommunity(newCommunityForm);
    // Optional<Community> newCom = cs.createCommunity(newCommunityForm.getName(),
    // newCommunityForm.getDescription(),
    // newCommunityForm.getCategories(), newCommunityForm.getDeveloper(),
    // newCommunityForm.getPublisher(),
    // LocalDateTime.now(), newCommunityForm.getImage());
    // return new ModelAndView("redirect:/community/" +
    // newCom.get().getEncodedName());
    // }
    //
    // @RequestMapping(path = "/community/{communityName}", method =
    // RequestMethod.GET)
    // public ModelAndView community(@RequestParam(required = false) Integer
    // pageNumber,
    // @PathVariable("communityName") final String communityName,
    // @ModelAttribute("newPostForm") final NewPostForm newPostForm,
    // @ModelAttribute("followCommunityForm") final FollowCommunityForm
    // followCommunityForm,
    // @ModelAttribute("newRatingForm") final NewRatingForm newRatingForm)
    // throws NoSuchCommunityException, NoLoggedUserException {
    // ModelAndView mav = new ModelAndView("community/community");
    // Community community = cs.findByName(communityName);
    // PaginatedDataWrapper<Post> posts;
    // PaginationRequest paginationRequest = new PaginationRequest(5);
    // if (Objects.nonNull(pageNumber))
    // paginationRequest.setPageNumber(pageNumber);
    // try {
    // posts = ps.getPostsByCommunityPaginated(communityName, paginationRequest);
    // } catch (IllegalArgumentException e) {
    // posts = null;
    // }
    // mav.addObject("posts", posts);
    // Boolean isAdmin = false;
    // Boolean isFollowing = false;
    // Optional<User> maybeUser = us.getLoggedUser();
    // List<Community> communities;
    // Optional<Rating> rating;
    // boolean canEdit = false;
    // if (maybeUser.isPresent()) {
    // User user = maybeUser.get();
    // isAdmin = user.getOwner();
    // isFollowing = cs.checkIfUserFollowsCommunity(community.getId().intValue());
    // communities = cs.getFollowedCommunities(user);
    // rating = rs.getRatingById(user, community);
    // canEdit = ms.isModderOfCommunity(user, community);
    // } else {
    // rating = Optional.empty();
    // communities = cs.getAllCommunities();
    // }
    // mav.addObject("rating", rating.orElse(null));
    // mav.addObject("isAdmin", isAdmin);
    // mav.addObject("isLogged", maybeUser.isPresent());
    // mav.addObject("communities", communities);
    // mav.addObject("isFollowing", isFollowing);
    // mav.addObject("categories",
    // Arrays.stream(PostCategories.values()).map(PostCategories::getCategory).toArray(String[]::new));
    // mav.addObject("community", community);
    // mav.addObject("posts", posts);
    // mav.addObject("canEdit", canEdit);
    // mav.addObject("format", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    // return mav;
    // }
    //
    // @RequestMapping(path = "/community/{communityName}", method =
    // RequestMethod.POST)
    // public ModelAndView createPostOnCommunity(@PathVariable("communityName")
    // final String communityName,
    // @ModelAttribute("newRatingForm") final NewRatingForm newRatingForm,
    // @ModelAttribute("followCommunityForm") final FollowCommunityForm
    // followCommunityForm,;
    // @Valid @ModelAttribute("newPostForm") final NewPostForm newPostForm,
    // BindingResult errors)
    // throws NoLoggedUserException, NoSuchCommunityException {
    //
    // if (errors.hasErrors())
    // return community(null, communityName, newPostForm, followCommunityForm,
    // newRatingForm);
    //
    // Post post = ps.createPost(newPostForm.getTitle(), newPostForm.getBody(),
    // communityName,
    // newPostForm.getCategory(), newPostForm.getFiles());
    // return new ModelAndView("redirect:/post/" + post.getId());
    // }
    //
    // @RequestMapping(path = "/communities", method = RequestMethod.GET)
    // public ModelAndView communities(@RequestParam(required = false) Integer
    // pageNumber,
    // @ModelAttribute("searchTerms") final String searchTerms,
    // @ModelAttribute("categories") final String categories) {
    // ModelAndView mav = new ModelAndView("community/communities");
    // List<String> selectedCategories = Arrays.asList(categories.split(","));
    // PaginatedDataWrapper<Community> communities;
    // PaginationRequest paginationRequest = new PaginationRequest(6);
    // if (Objects.nonNull(pageNumber))
    // paginationRequest.setPageNumber(pageNumber);
    // try {
    // communities = cs.find(paginationRequest, searchTerms, selectedCategories);
    // } catch (IllegalArgumentException e) {
    // communities = null;
    // }
    // Boolean isAdmin = false;
    // Optional<User> maybeUser = us.getLoggedUser();
    // List<Community> followedCommunities;
    // if (maybeUser.isPresent()) {
    // User user = maybeUser.get();
    // followedCommunities = cs.getFollowedCommunities(user);
    // isAdmin = user.getOwner();
    // } else {
    // followedCommunities = cs.getAllCommunities();
    // }
    // mav.addObject("isAdmin", isAdmin);
    // mav.addObject("isLogged", maybeUser.isPresent());
    // mav.addObject("categories",
    // Arrays.stream(CommunityCategories.values()).map(CommunityCategories::getCategory)
    // .toArray(String[]::new));
    // mav.addObject("selectedCategories", selectedCategories);
    // mav.addObject("searchTerms", searchTerms);
    // mav.addObject("communitiesPaginated", communities);
    // mav.addObject("followedCommunities", followedCommunities);
    // mav.addObject("isVerified", maybeUser.isPresent() &&
    // maybeUser.get().isVerified());
    // mav.addObject("noTerms", searchTerms.isEmpty());
    // return mav;
    // }
    //
    // @RequestMapping(path = "/community/{communityName}/new", method =
    // RequestMethod.POST)
    // public ModelAndView newCommunityPost(@PathVariable("communityName") final
    // String communityName,
    // @ModelAttribute("newRatingForm") final NewRatingForm newRatingForm,
    // @ModelAttribute("followCommunityForm") final FollowCommunityForm
    // followCommunityForm,
    // @Valid @ModelAttribute("newPostForm") final NewPostForm newPostForm,
    // BindingResult errors)
    // throws NoSuchCommunityException, NoLoggedUserException {
    // if (errors.hasErrors())
    // return community(null, communityName, newPostForm, followCommunityForm,
    // newRatingForm);
    // // chequeo de que exista la community
    // Community community = cs.findByName(communityName);
    // Post post = ps.createPost(newPostForm.getTitle(), newPostForm.getBody(),
    // community.getName(),
    // newPostForm.getCategory(), newPostForm.getFiles());
    // return new ModelAndView("redirect:/post/" + post.getId());
    // }
    //
    // @RequestMapping(path = "/community/{communityName}/info", method =
    // RequestMethod.GET)
    // public ModelAndView communityImage(@PathVariable("communityName") final
    // String communityName,
    // @ModelAttribute("EditCommunityForm") final EditCommunityInfoForm
    // editCommunityInfoForm)
    // throws NoSuchCommunityException {
    // ModelAndView mav = new ModelAndView("community/communityInfo");
    // Community community = cs.findByName(communityName);
    // Boolean isLogged = false;
    // Optional<User> maybeUser = us.getLoggedUser();
    // if (maybeUser.isPresent()) {
    // User user = maybeUser.get();
    // mav.addObject("isAdmin", user.getOwner());
    // mav.addObject("communities", cs.getFollowedCommunities(user));
    // isLogged = true;
    // } else {
    // mav.addObject("communities", cs.getAllCommunities());
    // }
    // mav.addObject("isLogged", isLogged);
    // mav.addObject("categories",
    // Arrays.stream(CommunityCategories.values()).map(CommunityCategories::getCategory)
    // .toArray(String[]::new));
    // mav.addObject("community", community);
    // return mav;
    // }
    //
    // @RequestMapping(path = "/community/{communityName}/info", method =
    // RequestMethod.POST)
    // public ModelAndView uploadCommunityImage(@PathVariable("communityName") final
    // String communityName,
    // @Valid @ModelAttribute("EditCommunityForm") final EditCommunityInfoForm
    // editCommunityInfoForm,
    // BindingResult errors) throws NoSuchCommunityException {
    // if (errors.hasErrors())
    // return communityImage(communityName, editCommunityInfoForm);
    // cs.editCommunityInfo(communityName, editCommunityInfoForm.getDescription(),
    // editCommunityInfoForm.getPublisher(), editCommunityInfoForm.getDeveloper(),
    // editCommunityInfoForm.getImage(), editCommunityInfoForm.getCategories());
    // return new ModelAndView("redirect:/community/" + communityName);
    // }
    //
    // @RequestMapping(value = "/image/{imageId}", method = RequestMethod.GET,
    // produces = { MediaType.IMAGE_JPEG_VALUE,
    // MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_GIF_VALUE })
    // @ResponseBody
    // public byte[] getImage(@PathVariable Integer imageId) {
    // return fs.getFile(imageId).map(File::getFile).orElse(null);
    // }
    //
    // @RequestMapping(path = "/community/{communityName}/follow", method =
    // RequestMethod.POST)
    // public ModelAndView followCommunity(@PathVariable("communityName") final
    // String communityName,
    // @ModelAttribute("newRatingForm") final NewRatingForm newRatingForm,
    // @Valid @ModelAttribute("followCommunityForm") final FollowCommunityForm
    // followCommunityForm,
    // BindingResult errors) throws NoSuchCommunityException, NoLoggedUserException
    // {
    // if (errors.hasErrors())
    // return community(null, communityName, new NewPostForm(), followCommunityForm,
    // newRatingForm);
    // cs.modifyUserOnCommunity(followCommunityForm.getCommunityId(),
    // followCommunityForm.getCommunityName());
    // return new ModelAndView("redirect:/community/" + communityName);
    // }
    //
    // @RequestMapping(path = "/community/{communityName}/rate", method =
    // RequestMethod.POST)
    // public ModelAndView rateCommunity(@PathVariable("communityName") final String
    // communityName,
    // @ModelAttribute("newPostForm") final NewPostForm newPostForm,
    // @ModelAttribute("followCommunityForm") final FollowCommunityForm
    // followCommunityForm,
    // @Valid @ModelAttribute("newRatingForm") final NewRatingForm newRatingForm,
    // BindingResult errors)
    // throws NoSuchCommunityException, NoLoggedUserException {
    // if (errors.hasErrors())
    // return community(null, communityName, newPostForm, followCommunityForm,
    // newRatingForm);
    // cs.updateRating(newRatingForm.getCommunityId(), newRatingForm.getRating());
    // return new ModelAndView("redirect:/community/" + communityName);
    // }
    //
    // @RequestMapping(path = "/community/{communityName}/deleteRating", method =
    // RequestMethod.POST)
    // public ModelAndView deleteRating(@PathVariable("communityName") final String
    // communityName,
    // @ModelAttribute("newPostForm") final NewPostForm newPostForm,
    // @ModelAttribute("followCommunityForm") final FollowCommunityForm
    // followCommunityForm,
    // @Valid @ModelAttribute("newRatingForm") final NewRatingForm newRatingForm,
    // final BindingResult errors)
    // throws NoSuchCommunityException, NoLoggedUserException {
    // if (errors.hasErrors())
    // return community(null, communityName, newPostForm, followCommunityForm,
    // newRatingForm);
    // cs.discountRating(communityName, newRatingForm.getRating());
    // return new ModelAndView("redirect:/community/" + communityName);
    // }
}

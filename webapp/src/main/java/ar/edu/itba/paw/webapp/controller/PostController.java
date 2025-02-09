package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.exceptions.*;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.pagination.PaginatedDataWrapper;
import ar.edu.itba.paw.models.pagination.PaginationRequest;
import ar.edu.itba.paw.services.*;
import ar.edu.itba.paw.webapp.validators.interfaces.FilesMustBeImagesConstraint;
import ar.edu.itba.paw.webapp.validators.interfaces.ValidCommunityConstraint;
import ar.edu.itba.paw.webapp.validators.interfaces.ValidPostCategoryConstraint;

import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.ContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.*;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;

import ar.edu.itba.paw.webapp.dto.CommentCreationDTO;
import ar.edu.itba.paw.webapp.dto.CommentDTO;
import ar.edu.itba.paw.webapp.dto.GrooveDTO;
import ar.edu.itba.paw.webapp.dto.GroovyCommentHistoryDTO;
import ar.edu.itba.paw.webapp.dto.GroovyPostHistoryDTO;
import ar.edu.itba.paw.webapp.dto.PostDTO;

@Path("/api/posts")
@Component
public class PostController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostController.class);

    @Autowired
    private PostService ps;
    @Autowired
    private CommunityService cs;
    @Autowired
    private UserService us;

    @Autowired
    private CommentService commentService;

    @Autowired
    private ModderService ms;

    @Context
    private UriInfo uriInfo;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listPosts(@Context UriInfo uriInfo,
            @QueryParam("page") @DefaultValue("1") final int page,
            @QueryParam("community") final String community,
            @QueryParam("likedBy") final Long likerId,
            @QueryParam("author") final Long authorId,
            @QueryParam("orderBy") @DefaultValue("default") final String orderBy,
            @QueryParam("category") final String category,
            @QueryParam("followedCommunitiesPosts") @DefaultValue("false") final Boolean followedCommunities)
            throws IllegalPageException, PageNotFoundException, NoLoggedUserException {
        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setPageNumber(page);
        LOGGER.info("followedCommunitiesPosts: {}", followedCommunities);
        PaginatedDataWrapper<Post> posts = ps.getAllPostsPaginated(category, orderBy, paginationRequest, likerId,
                authorId, community, followedCommunities);
        if (posts.getData().size() == 0) {
            return Response.noContent().build();
        }
        List<PostDTO> postsDTOs = posts.getData().stream()
                .map(PostDTO.mapper(uriInfo))
                .toList();

        return Response.ok(new GenericEntity<>(postsDTOs) {
        })
                .link(uriInfo.getAbsolutePathBuilder().queryParam("page", posts.getFirstPage()).build(), "first")
                .link(uriInfo.getAbsolutePathBuilder().queryParam("page", posts.getTotalPages()).build(), "last")
                .link(uriInfo.getAbsolutePathBuilder().queryParam("page", posts.getPreviousPage()).build(), "prev")
                .link(uriInfo.getAbsolutePathBuilder().queryParam("page", posts.getNextPage()).build(), "next")
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPostById(@Context UriInfo uriInfo, @PathParam("id") final long id) throws NoSuchPostException {
        Post post = ps.getPostById(id);
        return Response.ok(PostDTO.mapper(uriInfo).apply(post)).build();
    }

    // @GET
    // @Path("/{userName}")
    // @Produces(MediaType.APPLICATION_JSON)
    // public Response getPostByName(@Context UriInfo uriInfo, @PathParam("userName") final String userName)
    //         throws NoSuchPostException {
    //     List<Post> post = ps.getPostsByUserName(userName);
    //     return Response.ok(PostDTO.mapper(uriInfo).apply(post)).build();
    // }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response createPost(
            @FilesMustBeImagesConstraint(message = "{Images}") @FormDataParam("images") FormDataBodyPart imageDetails,
            @NotBlank @Size(min = 1, max = 150) @FormDataParam("title") String title,
            @NotBlank @Size(min = 1) @FormDataParam("body") String body,
            @NotBlank @ValidCommunityConstraint @FormDataParam("community") String community,
            @NotBlank @ValidPostCategoryConstraint @FormDataParam("category") String category)
            throws NoSuchCommunityException, IOException {
        LOGGER.info("images {}", imageDetails);
        List<byte[]> files = new ArrayList<>();
        if (imageDetails != null && imageDetails.getParent() != null) {
            for (BodyPart part : imageDetails.getParent().getBodyParts()) {
                LOGGER.info("imageDetail {}", part.getContentDisposition().getType());
                ContentDisposition meta = part.getContentDisposition();
                LOGGER.info("imageDetail {}", meta.getFileName());
                if (meta.getFileName() != null) {
                    InputStream is = part.getEntityAs(InputStream.class);
                    files.add(is.readAllBytes());

                }
            }

        }
        Post post = ps.createPost(title, body, community, category, files);
        URI uri = uriInfo.getAbsolutePathBuilder().path("posts").path(String.valueOf(post.getId())).build();
        return Response.created(uri).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deletePost(@PathParam("id") Long postId) throws NoSuchPostException, NoLoggedUserException {
        ps.removePost(postId);
        return Response.ok().build();
    }

    @POST
    @Path("/{id}/groovyness")
    public Response groovePost(@PathParam("id") Long postId, @Valid @NotNull final GrooveDTO payload)
            throws NoLoggedUserException, NoSuchPostException, PostAlreadyGroovedException, PostIsDeletedException {

        ps.createGrooviness(GroovyEnum.fromValue(payload.getGroovy()), postId);

        Optional<User> maybeUser = us.getLoggedUser();

        URI uri = uriInfo.getAbsolutePathBuilder()
                .path("posts")
                .path(String.valueOf(postId))
                .path("groovyness")
                .path(String.valueOf(maybeUser
                        .map(User::getId)
                        .orElseThrow(NoLoggedUserException::new)))
                .build();
        return Response.created(uri).build();
    }

    // TODO: Only accessible for MODS of given community/ Admin/Same User as userId
    @GET
    @Path("/{id}/groovyness/{userId}")
    public Response getGroove(@PathParam("id") Long postId, @PathParam("userId") Long userId)
            throws NoSuchPostException, UserNotFoundException, NoSuchGroovyPostHistory {

        Optional<GroovyEnum> groovy = ps.checkGrooviness(postId);

        return Response.ok()
                .entity(GroovyPostHistoryDTO.fromRating(uriInfo, groovy.orElseThrow(NoSuchGroovyPostHistory::new)))
                .build();
    }

    @PUT
    @Path("/{id}/groovyness/{userId}")
    public Response updateGroove(@PathParam("id") Long postId, @PathParam("userId") Long userId,
            @Valid @NotNull final GrooveDTO payload)
            throws NoSuchPostException, UserNotFoundException, NoSuchGroovyPostHistory, PostIsDeletedException {
        ps.editGrooviness(GroovyEnum.fromValue(payload.getGroovy()), postId);

        return Response.ok().build();
    }

    @DELETE
    @Path("/{id}/groovyness/{userId}")
    public Response deleteGroove(@PathParam("id") Long postId, @PathParam("userId") Long userId)
            throws NoSuchPostException, UserNotFoundException, NoSuchGroovyPostHistory, PostIsDeletedException {
        ps.deleteGrooviness(postId);

        return Response.ok().build();
    }

    @GET
    @Path("/{id}/comments")
    public Response getComments(@Context UriInfo uriInfo, @PathParam("id") Long postId,
            @QueryParam("page") @DefaultValue("1") int pageNumber)
            throws NoSuchPostException, NoSuchCommentException {
        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setPageNumber(pageNumber);

        PaginatedDataWrapper<Comment> comments = commentService.getPostCommentsPaginated(postId, paginationRequest);

        if (comments.getData().size() == 0) {
            return Response.noContent().build();
        }

        List<CommentDTO> commentsDTOs = comments.getData().stream()
                .map(CommentDTO.mapper(uriInfo))
                .toList();

        return Response.ok(new GenericEntity<>(commentsDTOs) {
        })
                .link(uriInfo.getAbsolutePathBuilder().queryParam("page", comments.getFirstPage()).build(), "first")
                .link(uriInfo.getAbsolutePathBuilder().queryParam("page", comments.getTotalPages()).build(), "last")
                .link(uriInfo.getAbsolutePathBuilder().queryParam("page", comments.getPreviousPage()).build(), "prev")
                .link(uriInfo.getAbsolutePathBuilder().queryParam("page", comments.getNextPage()).build(), "next")
                .build();

    }

    @POST
    @Path("{id}/comments")
    public Response createComment(@PathParam("id") Long postId, @Valid @NotNull final CommentCreationDTO payload)
            throws NoSuchPostException, NoLoggedUserException, UserNotFoundException, PostIsDeletedException {
        Comment comment = commentService.createComment(postId, payload.getBody());
        URI uri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(comment.getId()))
                .build();
        return Response.created(uri).build();
    }

    @POST
    @Path("{id}/comments/{commentId}/groovyness")
    public Response giveGroovyToComment(@PathParam("id") Long postId, @PathParam("commentId") long commentId,
            @Valid @NotNull final GrooveDTO payload)
            throws NoLoggedUserException, NoSuchPostException, NoSuchCommentException, CommentAlreadyGroovedException,
            CommentIsDeletedException {
        GroovyCommentHistory groovy = commentService.giveGrooviness(commentId,
                GroovyEnum.fromValue(payload.getGroovy()), postId);
        URI uri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(groovy.getUser().getId()))
                .build();

        return Response.created(uri).build();
    }

    @PUT
    @Path("{id}/comments/{commentId}/groovyness/{userId}")
    public Response updateGroovyFromComment(@PathParam("id") Long postId, @PathParam("commentId") long commentId,
            @PathParam("userId") long userId, @Valid @NotNull final GrooveDTO payload)
            throws NoSuchCommentException, NoSuchPostException, UserNotFoundException, NoSuchGroovyCommentHistory,
            CommentIsDeletedException, NoLoggedUserException {
        commentService.editGroovyness(commentId, postId,
                GroovyEnum.fromValue(payload.getGroovy()));
        return Response.ok().build();
    }

    @DELETE
    @Path("{id}/comments/{commentId}/groovyness/{userId}")
    public Response deleteGroovyFromComment(@PathParam("id") Long postId, @PathParam("commentId") long commentId,
            @PathParam("userId") long userId)
            throws NoSuchCommentException, NoSuchGroovyCommentHistory, NoSuchPostException, NoLoggedUserException,
            CommentIsDeletedException {
        commentService.deleteGroovyCommentHistory(commentId, postId);
        return Response.ok().build();
    }

    @GET
    @Path("{id}/comments/{commentId}/groovyness/{userId}")
    public Response getGroove(@Context UriInfo uriInfo, @PathParam("id") Long postId, @PathParam("userId") Long userId,
            @PathParam("commentId") Long commentId)
            throws NoSuchPostException, UserNotFoundException, NoSuchGroovyCommentHistory, NoSuchCommentException {

        GroovyCommentHistory gch = commentService.findGroovyCommentHistory(commentId, postId)
                .orElseThrow(NoSuchGroovyCommentHistory::new);
        return Response.ok()
                .entity(GroovyCommentHistoryDTO.fromRating(uriInfo, gch))
                .build();
    }

    @DELETE
    @Path("{id}/comments/{commentId}")
    public Response deleteComment(@PathParam("id") Long postId, @PathParam("commentId") Long commentId)
            throws NoSuchCommentException, PostIsDeletedException, NoSuchPostException, CommentIsDeletedException {
        commentService.deleteComment(commentId, postId);
        return Response.ok().build();
    }
}

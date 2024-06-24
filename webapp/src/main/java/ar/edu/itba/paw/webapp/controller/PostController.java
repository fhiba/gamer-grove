package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.exceptions.*;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.pagination.PaginatedDataWrapper;
import ar.edu.itba.paw.models.pagination.PaginationRequest;
import ar.edu.itba.paw.services.*;
import ar.edu.itba.paw.services.CommentService;
import ar.edu.itba.paw.services.CommunityService;
import ar.edu.itba.paw.services.PostService;
import ar.edu.itba.paw.services.UserService;
import ar.edu.itba.paw.webapp.form.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;


import javax.validation.Valid;
import java.time.format.DateTimeFormatter;
import java.util.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
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

    @RequestMapping(path = "/post", method = RequestMethod.POST)
    public ModelAndView newPost(@Valid @ModelAttribute("newPostForm") final NewPostForm newPostForm, final BindingResult errors) throws NoLoggedUserException, NoSuchCommunityException {
        Post post;
        if (errors.hasErrors()) {
            return getNewPost(newPostForm);
        }
        try {
            post = ps.createPost(newPostForm.getTitle(), newPostForm.getBody(), newPostForm.getCommunity(), newPostForm.getCategory(),newPostForm.getFiles());
        } catch (NoLoggedUserException | NoSuchCommunityException e) {
            LOGGER.atError().setMessage("Error creating new post: {}").addArgument(() -> e.getMessage()).log();
            throw e;
        }
        return new ModelAndView("redirect:/post/"+ post.getId());
    }

    @RequestMapping(path = "/post", method = RequestMethod.GET)
    public ModelAndView getNewPost(@ModelAttribute("newPostForm") final NewPostForm newPostForm) {
        ModelAndView mav = new ModelAndView("post/newPost");
        Optional<User> maybeUser = us.getLoggedUser();
        List<Community> followedCommunities;
        Boolean isAdmin = false;
        List<String> categories = Arrays.stream(PostCategories.values()).map(PostCategories::getCategory).toList();
        boolean isLogged = false;
        if(maybeUser.isPresent()) {
            User user = maybeUser.get();
            followedCommunities = cs.getFollowedCommunities(user);
            isAdmin = user.getOwner();
            isLogged = true;
        }
        else{
            followedCommunities = cs.getAllCommunities();
        }
        mav.addObject("isAdmin",isAdmin);
        mav.addObject("followedCommunities",followedCommunities);
        mav.addObject("isLogged", isLogged);
        mav.addObject("allCommunities", cs.getAllCommunities());
        mav.addObject("categories", categories);
        mav.addObject("news", ps.getByCategory(PostCategories.NEWS.getCategory()));

        return mav;
    }

    @RequestMapping(path = "/post/{postId}/up", method = RequestMethod.POST)
    public ModelAndView groovyPost(@Valid @ModelAttribute("newPostGroovyForm") NewPostGroovyForm newPostGroovyForm, final BindingResult errors) throws NoSuchPostException, UserNotFoundException, NoLoggedUserException {
        if (!errors.hasErrors())
            ps.editGrooviness( newPostGroovyForm.isGroovyType()? 1 : -1,newPostGroovyForm.getPostId());
        return new ModelAndView("redirect:/post/" + newPostGroovyForm.getPostId());
    }


    @RequestMapping(path = {"/home"}, method = RequestMethod.GET)
    public ModelAndView getHomePosts(@RequestParam(required = false) Integer pageNumber,@RequestParam(value = "category", required = false) final String category, @RequestParam(value="order", required = false) String order) {
        ModelAndView mav = new ModelAndView("/home");
        List<Community> communities;
        Boolean isAdmin;
        List<String> categories = ps.getUsedCategories();
        Optional<User > userOptional = us.getLoggedUser();
        PaginatedDataWrapper<Post> posts;
        PaginationRequest paginationRequest = new PaginationRequest();
        if(Objects.nonNull(pageNumber))
            paginationRequest.setPageNumber(pageNumber);

        if(userOptional.isPresent()) {
            User user = userOptional.get();
            communities = cs.getFollowedCommunities(user);
            isAdmin = user.getOwner();

            try {
                posts = ps.getUserFollowedPostsPaginated(category, order, user.getId(),paginationRequest);
            } catch (IllegalArgumentException e) {
                posts = null;
            }
        }else{
           return new ModelAndView("redirect:/all/");
        }
        mav.addObject("isAdmin",isAdmin);
        mav.addObject("isLogged", true);
        mav.addObject("posts",posts);
        mav.addObject("format", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        return homeAndAllConfig(category, order, mav, communities, categories, userOptional);
    }

    @NotNull
    private ModelAndView homeAndAllConfig(@RequestParam(value = "category", required = false) String category, @RequestParam(value="order", required = false) String order, ModelAndView mav, List<Community> communities, List<String> categories, Optional<User> userOptional) {
        mav.addObject("communities", communities);
        mav.addObject("topPost",ps.topFivePosts());
        mav.addObject("news", ps.getNewsLimited(5));
        mav.addObject("categories", categories);
        mav.addObject("isVerified", userOptional.isPresent() && userOptional.get().isVerified());
        mav.addObject("category",category);
        mav.addObject("order",order);
        mav.addObject("orders", Arrays.stream(PostOrders.values()).filter(o -> !o.equals(PostOrders.DEFAULT)).map(PostOrders::getOrder).toList());
        return mav;
    }


    @RequestMapping(path = {"/","/all"}, method = RequestMethod.GET)
    public ModelAndView getAllPosts(@RequestParam(required = false) Integer pageNumber,@RequestParam(value = "category", required = false) final String category, @RequestParam(value="order", required = false) String order) {
        ModelAndView mav = new ModelAndView("/home");
        PaginatedDataWrapper<Post> posts;
        List<Community> communities;
        Boolean isAdmin = false;
        List<String> categories = ps.getUsedCategories();
        PaginationRequest paginationRequest = new PaginationRequest();
        Optional<User> optionalUser = us.getLoggedUser();
        if(Objects.nonNull(pageNumber))
            paginationRequest.setPageNumber(pageNumber);


        if(optionalUser.isPresent()) {
            User user = optionalUser.get();
            communities = cs.getFollowedCommunities(user);
            isAdmin = user.getOwner();
        }else{
            communities = cs.getAllCommunities();
        }

        try {
            posts = ps.getAllPostsPaginated(category, order, paginationRequest);
        } catch (IllegalArgumentException e) {
            posts = null;
        }

        mav.addObject("isAdmin",isAdmin);
        mav.addObject("isLogged", optionalUser.isPresent());
        mav.addObject("format", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        mav.addObject("posts",posts);
        return homeAndAllConfig(category, order, mav, communities, categories, optionalUser);
    }

    @RequestMapping(path = "/post/{postId}", method = RequestMethod.GET)
    public ModelAndView singlePost(@PathVariable("postId") final long postId,@RequestParam(required = false) Integer pageNumber, @ModelAttribute("newPostGroovyForm") final NewPostGroovyForm newPostGroovyForm, @ModelAttribute("newCommentForm") final NewCommentForm newCommentForm, @ModelAttribute("newCommentGroovyForm") final NewCommentGroovyForm newCommentGroovyForm, @ModelAttribute("postDeleteForm") final PostDeleteForm postDeleteForm, @ModelAttribute("commentDeleteForm") final CommentDeleteForm commentDeleteForm,@ModelAttribute("followCommunityForm") final FollowCommunityForm followCommunityForm) throws UserNotFoundException, NoSuchPostException, NoSuchCommunityException, NoLoggedUserException {
        ModelAndView mav = new ModelAndView("/post/post");
        mav.addObject("format", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        Post post;
        Community community;
        List<Community> communities;
        PaginationRequest paginationRequest = new PaginationRequest(5);
        if(!Objects.isNull(pageNumber))
            paginationRequest.setPageNumber(pageNumber);

        List<Comment> grooviedComments = Collections.emptyList();
        List<Comment> negativeGrooviedComments = Collections.emptyList();
        Boolean isFollowing = false;
        Boolean isAdmin = false;
        boolean canDelete = false;
        int isGrooved = 0;

        try {
            post = ps.getPostByIdWithImage(postId);
            mav.addObject("post", post);
            community = cs.findByName(post.getcommunity().getName());
        } catch (NoSuchPostException | NoSuchCommunityException e ) {
            LOGGER.atError().setMessage("Error getting post with id {}").addArgument(postId).log();
            throw e;
        }
        PaginatedDataWrapper<Comment> comments;
        try {
            comments = commentService.getPostCommentsPaginated(postId,paginationRequest);
        }catch (IllegalArgumentException e){
            comments = null;
        }
        Optional<User> maybeUser = us.getLoggedUser();
        if(maybeUser.isPresent()) {
            User user = maybeUser.get();
            communities = cs.getFollowedCommunities(user);
            grooviedComments = commentService.getUpGroovedComments(postId);
            negativeGrooviedComments = commentService.getDownGroovedComments(postId);
            isGrooved = ps.checkGrooviness(postId);
            canDelete = ms.canRemovePost(user, postId);
            isAdmin = user.getOwner();
            isFollowing = cs.checkIfUserFollowsCommunity(community.getId().intValue());
        } else {
            communities = cs.getAllCommunities();
        }
        mav.addObject("isAdmin",isAdmin);
        mav.addObject("isFollowing",isFollowing);
        mav.addObject("community",community);
        mav.addObject("isLogged", maybeUser.isPresent());
        mav.addObject("isGrooved", isGrooved);
        mav.addObject("newPostGroovyForm", newPostGroovyForm);
        mav.addObject("upComments", grooviedComments);
        mav.addObject("downComments", negativeGrooviedComments);
        mav.addObject("newCommentForm", newCommentForm);
        mav.addObject("comments", comments);
        Optional<User> author = us.findById(post.getAuthor().getId());
        mav.addObject("author", author.isPresent() ? author.get().getUsername() : "[deleted]");
        mav.addObject("communities", communities);
        mav.addObject("canDelete", canDelete);

        return mav;
    }

    @RequestMapping(path="/comment", method = RequestMethod.POST)
    public ModelAndView newComment(@RequestParam(required = false) Integer pageNumber, @ModelAttribute("newPostGroovyForm") final NewPostGroovyForm newPostGroovyForm,  @ModelAttribute("newCommentGroovyForm") final NewCommentGroovyForm newCommentGroovyForm, @ModelAttribute("postDeleteForm") final PostDeleteForm postDeleteForm, @ModelAttribute("commentDeleteForm") final CommentDeleteForm commentDeleteForm,@ModelAttribute("followCommunityForm") final FollowCommunityForm followCommunityForm, @Valid @ModelAttribute("newCommentForm") final NewCommentForm newCommentForm, final BindingResult errors) throws NoLoggedUserException, NoSuchPostException, PostIsDeletedException, UserNotFoundException, NoSuchCommunityException {
        if(errors.hasErrors()) {
            return singlePost( newCommentForm.getPostId(),pageNumber,newPostGroovyForm,newCommentForm,newCommentGroovyForm,postDeleteForm,commentDeleteForm,followCommunityForm);
        }

        commentService.createComment(newCommentForm.getPostId(), newCommentForm.getBody());
        return new ModelAndView("redirect:/post/"+newCommentForm.getPostId());
    }

    @RequestMapping(path = "/post/{postId}/delete", method = RequestMethod.POST)
    public ModelAndView deletePost(@Valid @ModelAttribute("postDeleteForm") final PostDeleteForm postDeleteForm,final BindingResult errors) throws NoSuchPostException {
        if (errors.hasErrors()) {
            return new ModelAndView("redirect:/post/" + postDeleteForm.getPostId());
        }
            ps.removePost(postDeleteForm.getPostId());
        return new ModelAndView("redirect:/post/" + postDeleteForm.getPostId());
    }


}

package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.exceptions.NoLoggedUserException;
import ar.edu.itba.paw.exceptions.NoSuchCommunityException;
import ar.edu.itba.paw.exceptions.NoSuchPostException;
import ar.edu.itba.paw.exceptions.UserNotFoundException;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.services.*;
import ar.edu.itba.paw.services.CommentService;
import ar.edu.itba.paw.services.CommunityService;
import ar.edu.itba.paw.services.PostService;
import ar.edu.itba.paw.services.UserService;
import ar.edu.itba.paw.webapp.form.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
public class PostController {

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

        if (errors.hasErrors()) {
            return getNewPost(newPostForm);
        }
        try {
            ps.createPost(newPostForm.getTitle(), newPostForm.getBody(), newPostForm.getCommunity(), newPostForm.getCategory());
        } catch (NoLoggedUserException | NoSuchCommunityException e) {
            //TODO: log later
            throw e;
        }
        return new ModelAndView("redirect:/home");
    }

    @RequestMapping(path = "/post", method = RequestMethod.GET)
    public ModelAndView getNewPost(@ModelAttribute("newPostForm") final NewPostForm newPostForm) {
        ModelAndView mav = new ModelAndView("post/newPost");
        User user = null;
        List<Community> communities = null;
        try{
            user = us.getLoggedUserChecked();
        }catch (Exception ignored){

        }
        if(user != null)
            communities = cs.getFollowedCommunities(user);
        else{
            communities = cs.getAllCommunitiesNoCat();
        }
        mav.addObject("isLogged", user != null);
        //TODO: SHOULD BE THE ONES THAT ARE CURRENTLY BEING FOLLOWED BY USER OR A FEW RANDOMLY SELECTED
        mav.addObject("communities", communities);
        mav.addObject("categories", Arrays.stream(PostCategories.values()).map(PostCategories::getCategory).toArray(String[]::new));
        mav.addObject("news", ps.getByCategory(PostCategories.NEWS.getCategory()));

        return mav;
    }

    @RequestMapping(path = "/post/{postId}/up", method = RequestMethod.POST)
    public ModelAndView groovyPost(@Valid @ModelAttribute("newPostGroovyForm") NewPostGroovyForm newPostGroovyForm, final BindingResult errors) throws NoSuchPostException, UserNotFoundException {
        if (!errors.hasErrors())
            ps.editGrooviness( newPostGroovyForm.isGroovyType()? 1 : -1,newPostGroovyForm.getPostId());
        return new ModelAndView("redirect:/post/" + newPostGroovyForm.getPostId());
    }

//    @RequestMapping(path = "/post/{postId}/up", method = RequestMethod.GET)
//    public ModelAndView testtest(){
//        System.out.println("ENTRE");
//        return new ModelAndView("redirect:/home");
//    }

    @RequestMapping(path = {"/all", "/"}, method = RequestMethod.GET)
    public ModelAndView getHomePosts(@RequestParam(value = "category", required = false) final String category) {
        ModelAndView mav = new ModelAndView("/home");
        List<Post> posts;
        User user = null;
        List<Community> communities;
        try{
            user = us.getLoggedUserChecked();
        }catch (Exception ignored){

        }

        if(user != null)
            communities = cs.getFollowedCommunities(user);
        else{
            communities = cs.getAllCommunitiesNoCat();
        }

        if (category != null && !category.isEmpty() && !category.equals("all")) {
            posts = ps.getByCategory(category);
        } else {
            posts = ps.getAllPosts();
        }

        mav.addObject("isLogged", user != null);
        mav.addObject("posts",posts);
        //TODO: SHOULD BE THE ONES THAT ARE CURRENTLY BEING FOLLOWED BY USER OR A FEW RANDOMLY SELECTED
        mav.addObject("format", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        mav.addObject("communities", communities);
        mav.addObject("news", ps.getByCategory(PostCategories.NEWS.getCategory()));
        mav.addObject("categories", Arrays.stream(PostCategories.values()).map(PostCategories::getCategory).toArray(String[]::new));
        return mav;
    }

    @RequestMapping(path = {"/home"}, method = RequestMethod.GET)
    public ModelAndView getAllPosts(@RequestParam(value = "category", required = false) final String category) throws NoLoggedUserException {
        ModelAndView mav = new ModelAndView("/home");
        List<Post> posts = ps.getAllPosts();
        List<Community> communities;
        User user = null;
        try{
            user = us.getLoggedUserChecked();
        }catch (Exception ignored){

        }
        if(user != null) {
            communities = cs.getFollowedCommunities(user);
            if (category != null && !category.isEmpty() && !category.equals("all")) {
                posts = ps.getMyFollowedPostsByCategory(category,user);
            } else {
                posts = ps.getMyFollowedPosts(user);
            }
        }
        else{
            communities = cs.getAllCommunitiesNoCat();
        }
        mav.addObject("isLogged", user != null);
        mav.addObject("format", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        mav.addObject("posts",posts);
        mav.addObject("communities", communities);
        mav.addObject("news", ps.getByCategory(PostCategories.NEWS.getCategory()));
        mav.addObject("categories", Arrays.stream(PostCategories.values()).map(PostCategories::getCategory).toArray(String[]::new));
        return mav;
    }

    @RequestMapping(path = "/post/{postId}", method = RequestMethod.GET)
    public ModelAndView singlePost(@PathVariable("postId") final long postId, @ModelAttribute("newPostGroovyForm") final NewPostGroovyForm newPostGroovyForm, @ModelAttribute("newCommentForm") final NewCommentForm newCommentForm, @ModelAttribute("newCommentGroovyForm") final NewCommentGroovyForm newCommentGroovyForm, @ModelAttribute("postDeleteForm") final PostDeleteForm postDeleteForm, @ModelAttribute("commentDeleteForm") final CommentDeleteForm commentDeleteForm) throws UserNotFoundException, NoSuchPostException, NoSuchCommunityException {
        ModelAndView mav = new ModelAndView("/post/post");
        mav.addObject("format", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        Post post;
        List<Community> communities;
        User user = null;
        List<Comment> comments = commentService.getPostComments((postId));
        List<Comment> grooviedComments = Collections.emptyList();
        List<Comment> negativeGrooviedComments = Collections.emptyList();
        boolean canDelete = false;
        int isGrooved = 0;
        try{
            user = us.getLoggedUserChecked();
        }catch (Exception ignored){

        }
        if(user != null) {
            communities = cs.getFollowedCommunities(user);
            grooviedComments = commentService.getUpGroovedComments(postId);
            negativeGrooviedComments = commentService.getDownGroovedComments(postId);
            isGrooved = ps.checkGrooviness(postId);
            canDelete = ms.canRemovePost(us.getLoggedUser().get().getId(), postId);
        } else {
            communities = cs.getAllCommunitiesNoCat();
        }
        try {
            post = ps.getPostById(postId);
            mav.addObject("post", post);
        } catch (NoSuchPostException e) {
            //TODO: Should log
            throw e;
        }

        mav.addObject("isLogged", user != null);
        mav.addObject("isGrooved", isGrooved);
        mav.addObject("newPostGroovyForm", newPostGroovyForm);
        mav.addObject("upComments", grooviedComments);
        mav.addObject("downComments", negativeGrooviedComments);
        mav.addObject("newCommentForm", newCommentForm);
        mav.addObject("comments", comments);
        Optional<User> author = us.findById(post.getAuthor_id());
        mav.addObject("author", author.isPresent() ? author.get().getUsername() : "[deleted]");
        //TODO: SHOULD BE THE ONES THAT ARE CURRENTLY BEING FOLLOWED BY USER OR A FEW RANDOMLY SELECTED
        mav.addObject("communities", communities);
        mav.addObject("posts", ps.getPostsByCommunity(post.getCommunity_name()));
        mav.addObject("canDelete", canDelete);

        return mav;
    }

    @RequestMapping(path = "/post/{postId}/delete", method = RequestMethod.POST)
    public ModelAndView deletePost(@Valid @ModelAttribute("postDeleteForm") final PostDeleteForm postDeleteForm,final BindingResult errors) throws NoSuchPostException, NoLoggedUserException {
        if (errors.hasErrors()) {
            return new ModelAndView("redirect:/post/" + postDeleteForm.getPostId());
        }
        ms.removePost(postDeleteForm.getPostId());
        return new ModelAndView("redirect:/post/" + postDeleteForm.getPostId());
    }




//    @RequestMapping(path = "/post/{postId}/{grooviness}", method = RequestMethod.POST)
//    public ModelAndView moreGroovy(@PathVariable("postId") final long postId, @PathVariable("grooviness") final int grooviness) throws NoLoggedUserException, NoSuchPostException {
//        try {
//            ps.editGrooviness(postId, grooviness);
//        } catch (NoLoggedUserException e) {
//            //TODO: Log
//            throw e;
//        } catch (NoSuchPostException e) {
//            //TODO: Log
//            throw e;
//        }
//        return new ModelAndView("redirect:/post/" + postId);
//    }
}

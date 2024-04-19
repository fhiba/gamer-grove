package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.exceptions.NoLoggedUserException;
import ar.edu.itba.paw.exceptions.NoSuchCommunityException;
import ar.edu.itba.paw.exceptions.NoSuchPostException;
import ar.edu.itba.paw.exceptions.UserNotFoundException;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.services.CommentService;
import ar.edu.itba.paw.services.CommunityService;
import ar.edu.itba.paw.services.PostService;
import ar.edu.itba.paw.services.UserService;
import ar.edu.itba.paw.webapp.form.NewCommentForm;
import ar.edu.itba.paw.webapp.form.NewCommentGroovyForm;
import ar.edu.itba.paw.webapp.form.NewPostForm;
import ar.edu.itba.paw.webapp.form.NewPostGroovyForm;
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
        //TODO: SHOULD BE THE ONES THAT ARE CURRENTLY BEING FOLLOWED BY USER OR A FEW RANDOMLY SELECTED
        mav.addObject("communities", cs.getAllCommunities());
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
        if (category != null && !category.isEmpty() && !category.equals("all")) {
            posts = ps.getByCategory(category);
        } else {
            posts = ps.getAllPosts();
        }
        mav.addObject("posts",posts);
        //TODO: SHOULD BE THE ONES THAT ARE CURRENTLY BEING FOLLOWED BY USER OR A FEW RANDOMLY SELECTED
        mav.addObject("format", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        mav.addObject("communities", cs.getAllCommunities());
        mav.addObject("news", ps.getByCategory(PostCategories.NEWS.getCategory()));
        mav.addObject("categories", Arrays.stream(PostCategories.values()).map(PostCategories::getCategory).toArray(String[]::new));
        return mav;
    }

    @RequestMapping(path = {"/home"}, method = RequestMethod.GET)
    public ModelAndView getAllPosts(@RequestParam(value = "category", required = false) final String category) throws NoLoggedUserException {
        ModelAndView mav = new ModelAndView("/home");
        List<Post> posts = ps.getAllPosts();
        User user = null;
        List<Community> followedCommunities = Collections.emptyList();
        try{
            user = us.getLoggedUserChecked();
        }catch (Exception ignored){

        }
        if(user != null) {
            followedCommunities = cs.getFollowedCommunities();
            if (category != null && !category.isEmpty() && !category.equals("all")) {
                posts = ps.getMyFollowedPostsByCategory(category,user);
            } else {
                posts = ps.getMyFollowedPosts(user);
            }
        }
        mav.addObject("myFollowedCommunities",followedCommunities);
        mav.addObject("posts",posts);
        mav.addObject("communities", cs.getAllCommunities());
        mav.addObject("news", ps.getByCategory(PostCategories.NEWS.getCategory()));
        mav.addObject("categories", Arrays.stream(PostCategories.values()).map(PostCategories::getCategory).toArray(String[]::new));
        return mav;
    }

    @RequestMapping(path = "/post/{postId}", method = RequestMethod.GET)
    public ModelAndView singlePost(@PathVariable("postId") final long postId, @ModelAttribute("newPostGroovyForm") final NewPostGroovyForm newPostGroovyForm, @ModelAttribute("newCommentForm") final NewCommentForm newCommentForm, @ModelAttribute("newCommentGroovyForm") final NewCommentGroovyForm newCommentGroovyForm) throws UserNotFoundException, NoSuchPostException {
        ModelAndView mav = new ModelAndView("/post/post");
        mav.addObject("format", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        Post post;
        try {
            post = ps.getPostById(postId);
            mav.addObject("post", post);
        } catch (NoSuchPostException e) {
            //TODO: Should log
            throw e;
        }

        List<Comment> comments = commentService.getPostComments((postId));
        List<Comment> grooviedComments = Collections.emptyList();
        List<Comment> negativeGrooviedComments = Collections.emptyList();
        int isGrooved = 0;
        if (us.getLoggedUser().isPresent()) {
            grooviedComments = commentService.getUpGroovedComments(postId);
            negativeGrooviedComments = commentService.getDownGroovedComments(postId);
            isGrooved = ps.checkGrooviness(postId);
        }
        mav.addObject("isGrooved", isGrooved);
        mav.addObject("isGrooved", ps.checkGrooviness(postId));
        mav.addObject("newPostGroovyForm", newPostGroovyForm);
        mav.addObject("upComments", grooviedComments);
        mav.addObject("downComments", negativeGrooviedComments);
        mav.addObject("newCommentForm", newCommentForm);
        mav.addObject("comments", comments);
        Optional<User> author = us.findById(post.getAuthor_id());
        mav.addObject("author", author.isPresent() ? author.get().getUsername() : "[deleted]");
        //TODO: SHOULD BE THE ONES THAT ARE CURRENTLY BEING FOLLOWED BY USER OR A FEW RANDOMLY SELECTED
        mav.addObject("communities", cs.getAllCommunities());
        mav.addObject("posts", ps.getPostsByCommunity(post.getCommunity_name()));

        return mav;
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

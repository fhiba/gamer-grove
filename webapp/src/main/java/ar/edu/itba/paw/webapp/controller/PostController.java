package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.exceptions.NoLoggedUserException;
import ar.edu.itba.paw.exceptions.NoSuchCommunityException;
import ar.edu.itba.paw.exceptions.NoSuchPostException;
import ar.edu.itba.paw.exceptions.UserNotFoundException;
import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.PostCategories;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.services.*;
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
        } catch (NoLoggedUserException e) {
            //TODO: log later
            throw e;
        } catch (NoSuchCommunityException e) {
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

    @RequestMapping(path = {"/home", "/"}, method = RequestMethod.GET)
    public ModelAndView getAllPosts(@RequestParam(value = "category", required = false) final String category) {
        ModelAndView mav = new ModelAndView("/home");
        if (category != null && !category.isEmpty() && !category.equals("all")) {
            mav.addObject("posts", ps.getByCategory(category));
        } else {
            mav.addObject("posts", ps.getAllPosts());
        }
        //TODO: SHOULD BE THE ONES THAT ARE CURRENTLY BEING FOLLOWED BY USER OR A FEW RANDOMLY SELECTED
        mav.addObject("communities", cs.getAllCommunities());
        mav.addObject("news", ps.getByCategory(PostCategories.NEWS.getCategory()));
        mav.addObject("categories", Arrays.stream(PostCategories.values()).map(PostCategories::getCategory).toArray(String[]::new));
        return mav;
    }

    @RequestMapping(path = "/post/{postId}", method = RequestMethod.GET)
    public ModelAndView singlePost(@PathVariable("postId") final long postId, @ModelAttribute("newPostGroovyForm") final NewPostGroovyForm newPostGroovyForm, @ModelAttribute("newCommentForm") final NewCommentForm newCommentForm, @ModelAttribute("newCommentGroovyForm") final NewCommentGroovyForm newCommentGroovyForm,@ModelAttribute("postDeleteForm") final PostDeleteForm postDeleteForm, @ModelAttribute("commentDeleteForm") final CommentDeleteForm commentDeleteForm) throws UserNotFoundException, NoSuchPostException, NoSuchCommunityException {
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
        boolean canDelete = false;
        int isGrooved = 0;
        Optional<User> possibleUser = us.getLoggedUser();
        if (possibleUser.isPresent()) {
            grooviedComments = commentService.getUpGroovedComments(postId);
            negativeGrooviedComments = commentService.getDownGroovedComments(postId);
            isGrooved = ps.checkGrooviness(postId);
            canDelete = ms.canRemovePost(us.getLoggedUser().get().getId(), postId);
        }
        mav.addObject("isGrooved", isGrooved);
        //mav.addObject("isGrooved", ps.checkGrooviness(postId));
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

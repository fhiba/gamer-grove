package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.exceptions.NoSuchPostException;
import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.PostCategories;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.services.CommentService;
import ar.edu.itba.paw.services.CommunityService;
import ar.edu.itba.paw.services.PostService;
import ar.edu.itba.paw.services.UserService;
import ar.edu.itba.paw.webapp.form.NewCommentForm;
import ar.edu.itba.paw.webapp.form.NewPostForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
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

    @RequestMapping(path="/post", method = RequestMethod.POST)
    public ModelAndView newPost(@Valid @ModelAttribute("newPostForm") final NewPostForm newPostForm, final BindingResult errors) {
        if(errors.hasErrors()) {
            return getNewPost(newPostForm);
        }
        ps.createPost(newPostForm.getTitle(), newPostForm.getBody(), newPostForm.getCommunity(), newPostForm.getCategory());
        return new ModelAndView("redirect:/home");
    }

    @RequestMapping(path="/post", method = RequestMethod.GET)
    public ModelAndView getNewPost(@ModelAttribute("newPostForm") final NewPostForm newPostForm) {
        ModelAndView mav = new ModelAndView("post/newPost");
        mav.addObject("communities",cs.getAllCommunities());
        mav.addObject("categories", Arrays.stream(PostCategories.values()).map(PostCategories::getCategory).toArray(String[]::new));
        mav.addObject("news", ps.getByCategory(PostCategories.NEWS.getCategory()));
        return mav;
    }

    @RequestMapping(path = {"/home", "/"}, method = RequestMethod.GET)
    public ModelAndView getAllPosts(@RequestParam(value = "category", required = false) final String category) {
        ModelAndView mav = new ModelAndView("/home");
        if(category != null && !category.isEmpty() && !category.equals("all")) {
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

    @RequestMapping(path="/post/{postId}", method = RequestMethod.GET)
    public ModelAndView singlePost(@PathVariable("postId") final long postId, @ModelAttribute("newCommentForm") final NewCommentForm newCommentForm) {
        ModelAndView mav = new ModelAndView("/post/post");
        mav.addObject("format", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        Post post;
        try {
            post = ps.getPostById(postId);
            mav.addObject("post", post);
        } catch (NoSuchPostException e) {
            return new ModelAndView("/error/404");

        }
//        try{
            List<Comment> comments = commentService.getPostComments((postId));
            mav.addObject("comments",comments);
//        }catch (RuntimeException e){
//            //TODO: Catch it
//        }
        Optional<User> author = us.findById(post.getAuthor_id());
        mav.addObject("author", author.isPresent()?author.get().getUsername():"[deleted]");
        //TODO: SHOULD BE THE ONES THAT ARE CURRENTLY BEING FOLLOWED BY USER OR A FEW RANDOMLY SELECTED
        mav.addObject("communities", cs.getAllCommunities());
        mav.addObject("posts", ps.getPostsByCommunity(post.getCommunity_name()));

        return mav;
    }

    @RequestMapping(path="/post/{postId}/{grooviness}", method = RequestMethod.POST)
    public ModelAndView moreGroovy(@PathVariable("postId") final long postId,@PathVariable("grooviness") final int grooviness) {
        try {
            ps.editGrooviness(postId,grooviness);
        } catch (Exception e) {
            return new ModelAndView("/error/404");
        }
        return new ModelAndView("redirect:/post/" + postId);
    }
}

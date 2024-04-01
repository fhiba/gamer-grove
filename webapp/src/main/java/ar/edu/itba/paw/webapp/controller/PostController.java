package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.models.PostCategories;
import ar.edu.itba.paw.services.CommunityService;
import ar.edu.itba.paw.services.PostService;
import ar.edu.itba.paw.webapp.form.NewPostForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.Arrays;

@Controller
public class PostController {

    @Autowired
    private PostService ps;
    @Autowired
    private CommunityService cs;

    @RequestMapping(path="/post", method = RequestMethod.POST)
    public ModelAndView newPost(@Valid @ModelAttribute("newPostForm") final NewPostForm newPostForm, final BindingResult errors) {
        if(errors.hasErrors()) {
            return getNewPost(newPostForm);
        }
        ps.createPost(newPostForm.getTitle(), newPostForm.getBody(), newPostForm.getCommunity(), newPostForm.getCategory());
        return new ModelAndView("redirect:/all-posts");
    }

    @RequestMapping(path="/post", method = RequestMethod.GET)
    public ModelAndView getNewPost(@ModelAttribute("newPostForm") final NewPostForm newPostForm) {
        ModelAndView mav = new ModelAndView("post/newPost");
        mav.addObject("communities",cs.getAllCommunities());
        mav.addObject("categories", Arrays.stream(PostCategories.values()).map(PostCategories::getCategory).toArray(String[]::new));
        return mav;
    }

    @RequestMapping(path = "/all-posts", method = RequestMethod.GET)
    public ModelAndView getAllPosts(@RequestParam(value = "category", required = false) final String category) {
        ModelAndView mav = new ModelAndView("post/allPosts");
        if(category != null && !category.isEmpty() && !category.equals("all")) {
            mav.addObject("posts", ps.getByCategory(category));
        } else {
            mav.addObject("posts", ps.getAllPosts());
        }
        mav.addObject("categories", Arrays.stream(PostCategories.values()).map(PostCategories::getCategory).toArray(String[]::new));
        return mav;
    }

    @RequestMapping(path="/post/{postId}", method = RequestMethod.GET)
    public ModelAndView singlePost(@PathVariable("postId") final long postId) {
        ModelAndView mav = new ModelAndView("post/singlePost");
        try {
            mav.addObject("post", ps.getPostById(postId));
        } catch (Exception e) {
           //falta mandar a un 404
        }
        return mav;
    }
}

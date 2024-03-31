package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.services.CommunityService;
import ar.edu.itba.paw.services.PostService;
import ar.edu.itba.paw.webapp.form.NewPostForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
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
        ps.createPost(newPostForm.getTitle(), newPostForm.getBody(), "pedro",newPostForm.getCommunity());
        return new ModelAndView("redirect:/all-posts");
    }

    @RequestMapping(path="/post", method = RequestMethod.GET)
    public ModelAndView getNewPost(@ModelAttribute("newPostForm") final NewPostForm newPostForm) {
        ModelAndView mav = new ModelAndView("post/newPost");
        mav.addObject("communities",cs.getAllCommunities());
        return mav;
    }

    @RequestMapping(path = "/all-posts", method = RequestMethod.GET)
    public ModelAndView getAllPosts() {
        ModelAndView mav = new ModelAndView("post/allPosts");
        mav.addObject("posts", ps.getAllPosts());
        return mav;
    }
}

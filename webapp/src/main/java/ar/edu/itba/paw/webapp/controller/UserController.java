package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.services.PostService;
import ar.edu.itba.paw.services.UserService;
import ar.edu.itba.paw.webapp.form.LogInForm;
import ar.edu.itba.paw.webapp.form.NewPostForm;
import ar.edu.itba.paw.webapp.form.RegisterUserForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.validation.BindingResult;

import javax.validation.Valid;

@Controller
public class UserController {

    @Autowired
    private UserService us;

    @Autowired
    private PostService ps;


    @RequestMapping(path = "/login")
    public ModelAndView getLogIn(@ModelAttribute("loginForm") final LogInForm loginForm) {
        return new ModelAndView("user/login");
    }

    @RequestMapping(path = "/register", method = RequestMethod.GET)
    public ModelAndView getRegister(@ModelAttribute("registerForm") final RegisterUserForm registerUserForm) {

        return new ModelAndView("user/register");
    }

    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public ModelAndView postRegister(@Valid @ModelAttribute("registerForm") final RegisterUserForm registerUserForm, final BindingResult errors) {
        if(errors.hasErrors()) {
            return getRegister(registerUserForm);

        }
        us.create(registerUserForm.getUsername(), registerUserForm.getEmail(), registerUserForm.getPassword());
        return new ModelAndView("redirect:/");
    }

    @RequestMapping(path="/post", method = RequestMethod.POST)
    public ModelAndView newPost(@Valid @ModelAttribute("newPostForm") final NewPostForm newPostForm, final BindingResult errors) {
        if(errors.hasErrors()) {
            return getNewPost(newPostForm);
        }
        ps.createPost(newPostForm.getTitle(), newPostForm.getBody(), "shuiregay");
        return new ModelAndView("redirect:/all-posts");
    }

    @RequestMapping(path="/post", method = RequestMethod.GET)
    public ModelAndView getNewPost(@ModelAttribute("newPostForm") final NewPostForm newPostForm) {
        return new ModelAndView("newPost");
    }

    @RequestMapping(path = "/all-posts", method = RequestMethod.GET)
    public ModelAndView getAllPosts() {
        ModelAndView mav = new ModelAndView("allPosts");
        mav.addObject("posts", ps.getAllPosts());
        return mav;
    }
}

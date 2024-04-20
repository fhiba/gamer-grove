package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.exceptions.NoLoggedUserException;
import ar.edu.itba.paw.exceptions.NoSuchCommunityException;
import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.services.CommentService;
import ar.edu.itba.paw.services.CommunityService;
import ar.edu.itba.paw.services.PostService;
import ar.edu.itba.paw.services.UserService;
import ar.edu.itba.paw.webapp.excpetion.UserNotFoundException;
import ar.edu.itba.paw.webapp.form.EditProfileForm;
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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@Controller
public class UserController {

    @Autowired
    private UserService us;

    @Autowired
    private CommunityService cs;

    @Autowired
    private PostService ps;

    @Autowired
    private CommentService cms;


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


    @RequestMapping(path = "/profile", method = RequestMethod.GET)
    public ModelAndView getProfile(@ModelAttribute("editProfileForm") final EditProfileForm editProfileForm) {
        ModelAndView mav = new ModelAndView("user/profile");
        User user = us.getLoggedUser().orElseThrow(UserNotFoundException::new);
        mav.addObject("user",user);
        mav.addObject("posts",ps.getPostsByUser(user.getId()));
        mav.addObject("likedPosts",ps.getUserLikedPosts(user.getId()));
        mav.addObject("format", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        mav.addObject("communities",cs.getAllCommunities());
        return mav;
    }

    @RequestMapping(path = "/profile", method = RequestMethod.POST)
    public ModelAndView updateProfile(@Valid @ModelAttribute("editProfileForm") final EditProfileForm editProfileForm, final BindingResult errors) {
        us.updateProfile(editProfileForm.getUsername());
        return new ModelAndView("redirect:/profile");
    }



}

package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.exceptions.AlreadyModException;
import ar.edu.itba.paw.exceptions.NoLoggedUserException;
import ar.edu.itba.paw.exceptions.NoSuchCommunityException;
import ar.edu.itba.paw.exceptions.UserNotFoundException;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.services.CommunityService;
import ar.edu.itba.paw.services.FileService;
import ar.edu.itba.paw.services.ModderService;
import ar.edu.itba.paw.exceptions.NoLoggedUserException;
import ar.edu.itba.paw.exceptions.NoSuchCommunityException;
import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.services.CommentService;
import ar.edu.itba.paw.services.CommunityService;
import ar.edu.itba.paw.services.PostService;
import ar.edu.itba.paw.services.UserService;
//import ar.edu.itba.paw.webapp.form.EditProfileForm;
import ar.edu.itba.paw.webapp.form.LogInForm;
import ar.edu.itba.paw.webapp.form.NewModForm;
import ar.edu.itba.paw.webapp.form.NewPostForm;
import ar.edu.itba.paw.webapp.form.RegisterUserForm;
import ar.edu.itba.paw.webapp.form.RemoveModForm;
import ar.edu.itba.paw.webapp.form.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@Controller
public class UserController {

    @Autowired
    private UserService us;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private ModderService md;
    @Autowired
    private CommunityService cs;
    @Autowired
    private FileService fs;


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

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(registerUserForm.getUsername(), registerUserForm.getPassword());
        Authentication auth = authenticationManager.authenticate(authToken);
        SecurityContextHolder.getContext().setAuthentication(auth);
        return new ModelAndView("redirect:/").addObject("registerSuccess", true);
    }

    @RequestMapping(path="/addMod", method = RequestMethod.GET)
    public ModelAndView getAddMod(@ModelAttribute("newModForm") final NewModForm newModForm,@ModelAttribute("removeModForm") final RemoveModForm removeModForm) {
        ModelAndView mav = new ModelAndView("user/addMod");
        Boolean isAdmin = false;
        isAdmin = us.isUserAdmin(us.getLoggedUser().get().getId());
        mav.addObject("isAdmin",isAdmin);
        mav.addObject("sidebarcommunities", cs.getFollowedCommunities(us.getLoggedUser().get()));
        mav.addObject("communities", cs.getAllCommunities());
        mav.addObject("isLogged",true);
        return mav;
    }

    @RequestMapping(path="/addMod", method = RequestMethod.POST)
    public ModelAndView postAddMod(@ModelAttribute("removeModForm") final RemoveModForm removeModForm,@Valid @ModelAttribute("newModForm") final NewModForm newModForm, final BindingResult errors) throws UserNotFoundException, NoSuchCommunityException {

        if(errors.hasErrors()) {
            return  getAddMod(newModForm,removeModForm);
        }

        try {
            md.addModder(newModForm.getUsername(), newModForm.getCommunityId());
        }catch (AlreadyModException e) {
            return getAddMod(newModForm,removeModForm).addObject("isAlreadyMod", true);
        }
        return new ModelAndView("redirect:/addMod");
    }

    @RequestMapping(path="/removeMod", method = RequestMethod.POST)
    public ModelAndView postRemoveMod(@ModelAttribute("newModForm") final NewModForm newModForm,@Valid @ModelAttribute("removeModForm") final RemoveModForm removeModForm, final BindingResult errors) throws UserNotFoundException {

        if(errors.hasErrors()) {
            return getAddMod(newModForm,removeModForm);
        }
        int mod = md.removeModder(removeModForm.getRemoveUsername(), removeModForm.getFromCommunityId());
        if(mod == 0) {
            return getAddMod(newModForm,removeModForm).addObject("notAMod", true);
        }
        return new ModelAndView("redirect:/addMod");
    }

    @RequestMapping("/loginFailed")
    public void loginFailed(HttpServletRequest request) {
        AuthenticationException authenticationException = (AuthenticationException) request.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        if (authenticationException != null) {
            if(authenticationException.getCause() != null) {
                throw (AuthenticationException) authenticationException.getCause();
            } else {
                throw authenticationException;
            }
        }
    }

    @RequestMapping(path = "/user/update", method = RequestMethod.POST)
    public ModelAndView updateUser(@Valid @ModelAttribute("userPfpForm") final UserPfpForm userPfpForm, final BindingResult errors) throws NoLoggedUserException {
        if(errors.hasErrors()) {
            return getProfile(userPfpForm);
        }
        fs.uploadUserImage(userPfpForm.getFile());
        return new ModelAndView("redirect:/profile");
    }



    @RequestMapping(path = "/profile", method = RequestMethod.GET)
    public ModelAndView getProfile(@ModelAttribute("userPfpForm") final UserPfpForm userPfpForm) {
        ModelAndView mav = new ModelAndView("user/profile");
        User user = us.getLoggedUser().orElseThrow();
        Boolean isAdmin = us.isUserAdmin(user.getId());
        mav.addObject("isAdmin",isAdmin);
        mav.addObject("user",user);
        mav.addObject("posts",ps.getPostsByUser(user.getId()));
        mav.addObject("likedPosts",ps.getUserLikedPosts(user.getId()));
        mav.addObject("format", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        mav.addObject("communities",cs.getFollowedCommunities(user));
        mav.addObject("isVerified",user.isVerified());
        return mav;
    }




}

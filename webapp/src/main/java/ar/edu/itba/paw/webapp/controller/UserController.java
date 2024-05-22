package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.exceptions.AlreadyModException;
import ar.edu.itba.paw.exceptions.NoLoggedUserException;
import ar.edu.itba.paw.exceptions.NoSuchCommunityException;
import ar.edu.itba.paw.exceptions.UserNotFoundException;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.pagination.PaginatedDataWrapper;
import ar.edu.itba.paw.models.pagination.PaginationRequest;
import ar.edu.itba.paw.services.CommunityService;
import ar.edu.itba.paw.services.FileService;
import ar.edu.itba.paw.services.ModderService;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.services.CommentService;
import ar.edu.itba.paw.services.PostService;
import ar.edu.itba.paw.services.UserService;
//import ar.edu.itba.paw.webapp.form.EditProfileForm;
import ar.edu.itba.paw.webapp.form.LogInForm;
import ar.edu.itba.paw.webapp.form.NewModForm;
import ar.edu.itba.paw.webapp.form.RegisterUserForm;
import ar.edu.itba.paw.webapp.form.RemoveModForm;
import ar.edu.itba.paw.webapp.form.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Controller
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

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
    public ModelAndView postRegister(@Valid @ModelAttribute("registerForm") final RegisterUserForm registerUserForm, final BindingResult errors) throws UserNotFoundException {
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
        Boolean isAdmin;
        //El user esta necesariamente logueado para entrar en esta vista entonces no hace falta chequear si esta presente

        mav.addObject("isAdmin",true);
        mav.addObject("sidebarcommunities", us.getLoggedUser().get().getFollowedCommunities());
        mav.addObject("allCommunities", cs.getAllCommunities());
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
            LOGGER.debug("User is already a mod");
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
            return getProfileUserPosts(null,userPfpForm);
        }
        us.updateProfile(userPfpForm.getLocale(),userPfpForm.getFile());
        return new ModelAndView("redirect:/profile");
    }

    @RequestMapping(path = {"/profile/userPosts","/profile"}, method = RequestMethod.GET)
    public ModelAndView getProfileUserPosts(@RequestParam(required = false) Integer pageNumber, @ModelAttribute("userPfpForm") final UserPfpForm userPfpForm) {
        ModelAndView mav = new ModelAndView("user/profile/userPosts");
        User user = us.getLoggedUser().orElseThrow();
        mav.addObject("isAdmin",user.getOwner());
        mav.addObject("user",user);
        mav.addObject("format", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        mav.addObject("communities",user.getFollowedCommunities());
        mav.addObject("isVerified",user.isVerified());

        PaginatedDataWrapper<Post> posts;
        PaginationRequest paginationRequest = new PaginationRequest(5);
        if(Objects.nonNull(pageNumber))
            paginationRequest.setPageNumber(pageNumber);
        try {
            posts = ps.getPostsByUserPaginated(user.getId(),paginationRequest);
        }catch (IllegalArgumentException e){
            posts = null;
        }
        mav.addObject("posts",posts);
        return mav;
    }

    @RequestMapping(path = "/profile/likedPosts", method = RequestMethod.GET)
    public ModelAndView getProfileLikedPosts(@RequestParam(required = false) Integer pageNumber, @ModelAttribute("userPfpForm") final UserPfpForm userPfpForm) {
        ModelAndView mav = new ModelAndView("user/profile/likedPosts");
        User user = us.getLoggedUser().orElseThrow();
        Boolean isAdmin = us.getLoggedUser().get().getOwner();
        mav.addObject("isAdmin",isAdmin);
        mav.addObject("user",user);
        mav.addObject("format", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        mav.addObject("communities",user.getFollowedCommunities());
        mav.addObject("isVerified",user.isVerified());
        PaginatedDataWrapper<Post> likedPosts;
        PaginationRequest paginationRequestLikedPosts = new PaginationRequest(5);
        if(Objects.nonNull(pageNumber))
            paginationRequestLikedPosts.setPageNumber(pageNumber);
        try {
            likedPosts = ps.getUserLikedPostsPaginated(user.getId(),paginationRequestLikedPosts);
        }catch (IllegalArgumentException e){
            likedPosts = null;
        }
        mav.addObject("posts",likedPosts);
        return mav;
    }



}

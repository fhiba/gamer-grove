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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
    public ModelAndView postRegister(@Valid @ModelAttribute("registerForm") final RegisterUserForm registerUserForm, final BindingResult errors) throws UserNotFoundException {
        if (errors.hasErrors()) {
            return getRegister(registerUserForm);

        }
        us.create(registerUserForm.getUsername(), registerUserForm.getEmail(), registerUserForm.getPassword());

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(registerUserForm.getUsername(), registerUserForm.getPassword());
        Authentication auth = authenticationManager.authenticate(authToken);
        SecurityContextHolder.getContext().setAuthentication(auth);
        return new ModelAndView("redirect:/communities").addObject("registerSuccess", true);
    }

    @RequestMapping(path = "/manageMods", method = RequestMethod.GET)
    public ModelAndView manageMods(@RequestParam(value = "username", required = false) final String username,@RequestParam(value = "community", required = false) final String community, @RequestParam(required = false) Integer pageNumber, @ModelAttribute("newModForm") final NewModForm newModForm, @ModelAttribute("removeModForm") final RemoveModForm removeModForm) throws NoLoggedUserException {
        ModelAndView mav = new ModelAndView("/user/manageMods");
        PaginatedDataWrapper<Mod> modders;
        List<Community> allCommunities = cs.getAllCommunities();
        PaginationRequest paginationRequest = new PaginationRequest();
        if (Objects.nonNull(pageNumber))
            paginationRequest.setPageNumber(pageNumber);
        if(Objects.nonNull(community) && !community.isEmpty() && Objects.isNull(username)){
            try {
                modders = md.getModsByCommunityPaginated(community, paginationRequest);
            } catch (NoSuchCommunityException e) {
                LOGGER.atError().setMessage("Error getting moderators filter by community {}").addArgument(community).log();
                modders = null;
            }
        } else if (Objects.nonNull(username) && !username.isEmpty() && Objects.isNull(community)) {
            try {
                modders = md.getModsByUsernamePaginated(username, paginationRequest);
            } catch (IllegalArgumentException e) {
                LOGGER.atError().setMessage("Error getting moderators filter by username {}").addArgument(username).log();
                modders = null;
            } catch (UserNotFoundException e) {
                modders = null;
            }
        } else if (Objects.nonNull(username) && !username.isEmpty() && !community.isEmpty()) {
            try {
                Optional<Mod> optMod = md.findMod(username,community);
                mav.addObject("moderator",optMod.orElseThrow());
                modders = null;
            } catch (IllegalArgumentException e) {
                LOGGER.atError().setMessage("Error getting moderators filter by username {} and community").addArgument(username).addArgument(community).log();
                modders = null;
            } catch (UserNotFoundException | NoSuchCommunityException e) {
                modders = null;
            }
        } else{
            try {
                modders = md.getAllModPaginated(paginationRequest);
            } catch (IllegalArgumentException e) {
                LOGGER.atError().setMessage("Error getting all moderators").log();
                modders = null;
            }
        }
        mav.addObject("modders", modders);
        mav.addObject("format", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        //El user esta necesariamente logueado para entrar en esta vista entonces no hace falta chequear si esta presente
        mav.addObject("isAdmin", true);
        mav.addObject("sidebarcommunities", cs.getFollowedCommunities(us.getLoggedUserChecked()));
        mav.addObject("allCommunities", allCommunities);
        mav.addObject("isLogged", true);
        return mav;
    }

    @RequestMapping(path = "/addMod", method = RequestMethod.POST)
    public ModelAndView postAddMod(@ModelAttribute("removeModForm") final RemoveModForm removeModForm, @Valid @ModelAttribute("newModForm") final NewModForm newModForm, final BindingResult errors) throws UserNotFoundException, NoSuchCommunityException, NoLoggedUserException {

        if (errors.hasErrors()) {
            return manageMods(null,null,null, newModForm, removeModForm);
        }

        try {
            md.addModder(newModForm.getUsername(), newModForm.getCommunityId());
        } catch (AlreadyModException e) {
            LOGGER.atError().setMessage("User {} is already a mod").addArgument(()-> newModForm.getUsername()).log();
            return manageMods(null,null,null, newModForm, removeModForm).addObject("isAlreadyMod", true);
        }
        return new ModelAndView("redirect:/manageMods");
    }

    @RequestMapping(path = "/removeMod", method = RequestMethod.POST)
    public ModelAndView postRemoveMod(@ModelAttribute("newModForm") final NewModForm newModForm, @Valid @ModelAttribute("removeModForm") final RemoveModForm removeModForm, final BindingResult errors) throws UserNotFoundException, NoLoggedUserException, NoSuchCommunityException {

        if (errors.hasErrors()) {
            return manageMods(null,null,null, newModForm, removeModForm);
        }
        Boolean removedSuccess = md.removeModder(removeModForm.getRemoveUsername(), removeModForm.getFromCommunityId());
        if (!removedSuccess) {
            return manageMods(null,null,null, newModForm, removeModForm).addObject("notAMod", true);
        }
        return new ModelAndView("redirect:/manageMods?username="+removeModForm.getRemoveUsername());
    }

    @RequestMapping("/loginFailed")
    public void loginFailed(HttpServletRequest request) {
        AuthenticationException authenticationException = (AuthenticationException) request.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        if (authenticationException != null) {
            if (authenticationException.getCause() != null) {
                throw (AuthenticationException) authenticationException.getCause();
            } else {
                throw authenticationException;
            }
        }
    }

    @RequestMapping(path = "/user/update", method = RequestMethod.POST)
    public ModelAndView updateUser(@Valid @ModelAttribute("userPfpForm") final UserPfpForm userPfpForm, final BindingResult errors) throws NoLoggedUserException, UserNotFoundException {
        if (errors.hasErrors()) {
            return getProfileUserPosts(null, userPfpForm);
        }
        us.updateProfile(userPfpForm.getLocale(), userPfpForm.getFile());
        return new ModelAndView("redirect:/profile");
    }

    @RequestMapping(path = {"/profile/userPosts", "/profile"}, method = RequestMethod.GET)
    public ModelAndView getProfileUserPosts(@RequestParam(required = false) Integer pageNumber, @ModelAttribute("userPfpForm") final UserPfpForm userPfpForm) throws UserNotFoundException {
        ModelAndView mav = new ModelAndView("user/profile/userPosts");
        Optional<User> maybeUser = us.getLoggedUser();
        if(maybeUser.isEmpty()) {
            LOGGER.atError().setMessage("User logged not found").log();
            throw new UserNotFoundException("This user does not exists");
        }
        User user = maybeUser.get();
        mav.addObject("isAdmin", user.getOwner());
        mav.addObject("user", user);
        mav.addObject("format", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        mav.addObject("communities", cs.getFollowedCommunities(user));
        mav.addObject("isVerified", user.isVerified());

        PaginatedDataWrapper<Post> posts;
        PaginationRequest paginationRequest = new PaginationRequest(5);
        if (Objects.nonNull(pageNumber))
            paginationRequest.setPageNumber(pageNumber);
        try {
            posts = ps.getPostsByUserPaginated(user.getId(), paginationRequest);
        } catch (IllegalArgumentException e) {
            LOGGER.atError().setMessage("Error getting posts of user {}").addArgument(()-> user.getId()).log();
            posts = null;
        }
        mav.addObject("posts", posts);
        return mav;
    }

    @RequestMapping(path = "/profile/likedPosts", method = RequestMethod.GET)
    public ModelAndView getProfileLikedPosts(@RequestParam(required = false) Integer pageNumber, @ModelAttribute("userPfpForm") final UserPfpForm userPfpForm) throws UserNotFoundException {
        ModelAndView mav = new ModelAndView("user/profile/likedPosts");
        Optional<User> maybeUser = us.getLoggedUser();
        if(maybeUser.isEmpty()) {
            LOGGER.atError().setMessage("User logged not found").log();
            throw new UserNotFoundException("This user does not exists");
        }
        User user = maybeUser.get();
        Boolean isAdmin = us.getLoggedUser().get().getOwner();
        mav.addObject("isAdmin", isAdmin);
        mav.addObject("user", user);
        mav.addObject("format", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        mav.addObject("communities", cs.getFollowedCommunities(user));
        mav.addObject("isVerified", user.isVerified());
        PaginatedDataWrapper<Post> likedPosts;
        PaginationRequest paginationRequestLikedPosts = new PaginationRequest(5);
        if (Objects.nonNull(pageNumber))
            paginationRequestLikedPosts.setPageNumber(pageNumber);
        try {
            likedPosts = ps.getUserLikedPostsPaginated(user.getId(), paginationRequestLikedPosts);
        } catch (IllegalArgumentException e) {
            LOGGER.atError().setMessage("Error getting liked posts of user {}").addArgument(()-> user.getId()).log();
            likedPosts = null;
        }
        mav.addObject("posts", likedPosts);
        return mav;
    }

    @RequestMapping(path = "/profile/followed", method = RequestMethod.GET)
    public ModelAndView getFollowedCommunities(@RequestParam(required = false) Integer pageNumber, @ModelAttribute("categories") final String categories, @ModelAttribute("userPfpForm") final UserPfpForm userPfpForm) throws NoLoggedUserException, UserNotFoundException {
        ModelAndView mav = new ModelAndView("user/profile/followedCommunities");
        PaginatedDataWrapper<Community> communities;

        List<String> selectedCategories = Arrays.asList(categories.split(","));
        PaginationRequest paginationRequest = new PaginationRequest(8);
        if (Objects.nonNull(pageNumber))
            paginationRequest.setPageNumber(pageNumber);

        try {
            communities = cs.findFollowedCommunities(paginationRequest, selectedCategories);
        } catch (IllegalArgumentException e) {
            communities = null;
        }
        Optional<User> maybeUser = us.getLoggedUser();
        if(maybeUser.isEmpty()) {
            LOGGER.atError().setMessage("User logged not found").log();
            throw new UserNotFoundException("This user does not exists");
        }
        User user = maybeUser.get();
        mav.addObject("isAdmin", user.getOwner());
        mav.addObject("user", user);
        mav.addObject("followedCommunities", communities);
        mav.addObject("selectedCategories", selectedCategories);
        mav.addObject("categories", Arrays.stream(CommunityCategories.values()).map(CommunityCategories::getCategory).toArray(String[]::new));
        mav.addObject("communities", cs.getFollowedCommunities(user));
        mav.addObject("isVerified", user.isVerified());

        return mav;

    }

    @RequestMapping(path = {"/user/{id}/userPosts"}, method = RequestMethod.GET)
    public ModelAndView getPublicProfileUserPosts(@PathVariable("id") final long id,@RequestParam(required = false) Integer pageNumber) throws UserNotFoundException {
        ModelAndView mav = new ModelAndView("user/public_profile/userPosts");
        Optional<User> maybeUser = us.findById(id);
        if(maybeUser.isEmpty()) {
            LOGGER.atError().setMessage("The user with id {} does not exists").addArgument(id).log();
            throw new UserNotFoundException("This user does not exists");
        }
        User user = maybeUser.get();
        mav.addObject("isAdmin", user.getOwner());
        mav.addObject("user", user);
        mav.addObject("format", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        mav.addObject("communities", cs.getFollowedCommunities(user));
        mav.addObject("isVerified", user.isVerified());

        PaginatedDataWrapper<Post> posts;
        PaginationRequest paginationRequest = new PaginationRequest(5);
        if (Objects.nonNull(pageNumber))
            paginationRequest.setPageNumber(pageNumber);
        try {
            posts = ps.getPostsByUserPaginated(user.getId(), paginationRequest);
        } catch (IllegalArgumentException e) {
            posts = null;
        }
        mav.addObject("posts", posts);
        return mav;
    }

    @RequestMapping(path = "/user/{id}/likedPosts", method = RequestMethod.GET)
    public ModelAndView getProfileLikedPosts(@PathVariable("id") final long id,@RequestParam(required = false) Integer pageNumber) throws UserNotFoundException {
        ModelAndView mav = new ModelAndView("user/public_profile/likedPosts");
        Optional<User> maybeUser = us.findById(id);
        if(maybeUser.isEmpty()) {
            LOGGER.atError().setMessage("The user with id {} does not exists").addArgument(id).log();
            throw new UserNotFoundException("This user does not exists");
        }
        User user = maybeUser.get();

        mav.addObject("isAdmin", false);
        mav.addObject("user", user);
        mav.addObject("format", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        mav.addObject("communities", cs.getFollowedCommunities(user));
        mav.addObject("isVerified", user.isVerified());
        PaginatedDataWrapper<Post> likedPosts;
        PaginationRequest paginationRequestLikedPosts = new PaginationRequest(5);
        if (Objects.nonNull(pageNumber))
            paginationRequestLikedPosts.setPageNumber(pageNumber);
        try {
            likedPosts = ps.getUserLikedPostsPaginated(user.getId(), paginationRequestLikedPosts);
        } catch (IllegalArgumentException e) {
            likedPosts = null;
        }
        mav.addObject("posts", likedPosts);
        return mav;
    }

    @RequestMapping(path = "/user/{id}/followed", method = RequestMethod.GET)
    public ModelAndView getFollowedCommunities(@PathVariable("id") final long id,@RequestParam(required = false) Integer pageNumber, @ModelAttribute("categories") final String categories) throws NoLoggedUserException, UserNotFoundException {
        ModelAndView mav = new ModelAndView("user/public_profile/followedCommunities");
        PaginatedDataWrapper<Community> communities;

        List<String> selectedCategories = Arrays.asList(categories.split(","));
        PaginationRequest paginationRequest = new PaginationRequest(8);
        if (Objects.nonNull(pageNumber))
            paginationRequest.setPageNumber(pageNumber);
        try {
            communities = cs.findFollowedCommunities(paginationRequest, selectedCategories);
        } catch (IllegalArgumentException e) {
            communities = null;
        }
        Optional<User> maybeUser = us.findById(id);
        if(maybeUser.isEmpty()) {
            LOGGER.atError().setMessage("The user with id {} does not exists").addArgument(id).log();
            throw new UserNotFoundException("This user does not exists");
        }
        User user = maybeUser.get();
        mav.addObject("isAdmin", user.getOwner());
        mav.addObject("user", user);
        mav.addObject("followedCommunities", communities);
        mav.addObject("selectedCategories", selectedCategories);
        mav.addObject("categories", Arrays.stream(CommunityCategories.values()).map(CommunityCategories::getCategory).toArray(String[]::new));
        mav.addObject("communities", cs.getFollowedCommunities(user));
        mav.addObject("isVerified", user.isVerified());
        return mav;
    }


}

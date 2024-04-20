package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.exceptions.AlreadyModException;
import ar.edu.itba.paw.exceptions.NoSuchCommunityException;
import ar.edu.itba.paw.exceptions.UserNotFoundException;
import ar.edu.itba.paw.services.CommunityService;
import ar.edu.itba.paw.services.ModderService;
import ar.edu.itba.paw.services.UserService;
import ar.edu.itba.paw.webapp.form.LogInForm;
import ar.edu.itba.paw.webapp.form.NewModForm;
import ar.edu.itba.paw.webapp.form.RegisterUserForm;
import ar.edu.itba.paw.webapp.form.RemoveModForm;
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
        return new ModelAndView("redirect:/");
    }

    @RequestMapping(path="/addMod", method = RequestMethod.GET)
    public ModelAndView getAddMod(@ModelAttribute("newModForm") final NewModForm newModForm,@ModelAttribute("removeModForm") final RemoveModForm removeModForm) {
        ModelAndView mav =new ModelAndView("user/addMod");
        mav.addObject("communities", cs.getAllCommunities());
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


}

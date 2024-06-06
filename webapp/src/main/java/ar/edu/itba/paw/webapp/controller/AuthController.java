package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.exceptions.NoLoggedUserException;
import ar.edu.itba.paw.exceptions.UserNotFoundException;
import ar.edu.itba.paw.exceptions.NoSuchTokenException;
import ar.edu.itba.paw.services.TokenService;
import ar.edu.itba.paw.services.UserService;
import ar.edu.itba.paw.webapp.form.EmailForm;
import ar.edu.itba.paw.webapp.form.ResetPasswordForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;



import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller
public class AuthController {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserService userService;

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);


    @RequestMapping(value = "/auth/resend-verification", method = RequestMethod.GET)
    public ModelAndView resendVerification() throws NoLoggedUserException, UserNotFoundException {
        userService.resendVerification();
        return new ModelAndView("redirect:/communities").addObject("resendVerification", true);
    }

    @RequestMapping(value = "/verify", method = RequestMethod.GET)
    public ModelAndView validateAccount(@RequestParam("token") final String token) throws NoSuchTokenException, UserNotFoundException {
        if(token == null || token.isEmpty())
            throw new NoSuchTokenException("token is empty or null");

        ModelAndView mav = new ModelAndView("redirect:/communities");

        try {
            userService.verifyUser(token);
        } catch (NoSuchTokenException e) {
            return mav.addObject("verifySuccess", false);
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<GrantedAuthority> updatedAuthorities = new ArrayList<>(auth.getAuthorities());
        updatedAuthorities.add(new SimpleGrantedAuthority("ROLE_VERIFIED"));
        Authentication newAuth = new UsernamePasswordAuthenticationToken(auth.getPrincipal(), auth.getCredentials(), updatedAuthorities);
        SecurityContextHolder.getContext().setAuthentication(newAuth);

        return mav.addObject("verifySuccess", true);
    }

    @RequestMapping(value = "/auth/resetPassword", method = RequestMethod.GET)
    public ModelAndView resetPassword(@ModelAttribute("token") final String token, @ModelAttribute("resetPasswordForm") ResetPasswordForm resetPasswordForm) throws NoSuchTokenException, UserNotFoundException {
        if(token == null || token.isEmpty())
            throw new NoSuchTokenException("token is empty or null");
        Boolean tokenExists = tokenService.verifyResetToken(token);
        if(!tokenExists)
            throw new NoSuchTokenException("token is invalid");
        ModelAndView mav = new ModelAndView("user/resetPassword");
        mav.addObject("token",token);
        return mav;
    }

    @RequestMapping(value = "/auth/resetPassword", method = RequestMethod.POST)
    public ModelAndView resetPassword( @Valid @ModelAttribute("resetPasswordForm") ResetPasswordForm resetPasswordForm, final BindingResult errors) throws NoSuchTokenException, UserNotFoundException {
        if(errors.hasErrors()) {
            return resetPassword(resetPasswordForm.getToken(), resetPasswordForm);
        }
        ModelAndView mav = new ModelAndView("redirect:/login");
        mav.clear();
        mav.setViewName("redirect:/login");
        try {
            userService.resetPassword(resetPasswordForm.getToken(), resetPasswordForm.getPassword());
        } catch (NoSuchTokenException e) {

            return mav.addObject("resetSuccess", false);
        }

        return mav.addObject("resetSuccess", true);
    }

    @RequestMapping(value="/auth/forgotCredentials", method = RequestMethod.GET)
    public ModelAndView forgotCredentials(@ModelAttribute("emailForm") final EmailForm emailForm) {
        return new ModelAndView("user/forgotCredentials");
    }

    @RequestMapping(value="/auth/forgotCredentials", method = RequestMethod.POST)
    public ModelAndView forgotCredentials(@Valid @ModelAttribute("emailForm") final EmailForm emailForm, final BindingResult errors) throws NoSuchTokenException, UserNotFoundException{
        if(errors.hasErrors()) {
            return forgotCredentials(emailForm);
        }
        Boolean success = userService.startResetPassword(emailForm.getEmail());
        return new ModelAndView("redirect:/login").addObject("resetPassword", success);
    }
}

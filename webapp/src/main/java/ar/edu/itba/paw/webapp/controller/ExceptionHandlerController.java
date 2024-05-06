package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import java.util.Locale;

@ControllerAdvice
public class ExceptionHandlerController {

    @Autowired
    private MessageSource messageSource;


    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = {
            NoSuchPostException.class,
            NoSuchCommunityException.class,
            UserNotFoundException.class,
            NoSuchTokenException.class
    })
    public ModelAndView notFoundException() {
        ModelAndView mav = new ModelAndView("errors/error");
        mav.addObject("error_title", messageSource.getMessage("404", null, Locale.getDefault()));
        mav.addObject("error_message", messageSource.getMessage("404.message", null, Locale.getDefault()));
        return mav;
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(value = {
            NoLoggedUserException.class
    })
    public ModelAndView unauthorizedException() {
        ModelAndView mav = new ModelAndView("errors/error");
        mav.addObject("error_title", messageSource.getMessage("401", null, Locale.getDefault()));
        mav.addObject("error_message", messageSource.getMessage("401.message", null, Locale.getDefault()));
        return mav;
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({BadCredentialsException.class})
    public ModelAndView unregisteredUserException() {
        return new ModelAndView("user/login").addObject("error", messageSource.getMessage("Login.Incorrect", null, Locale.getDefault()));
    }

}

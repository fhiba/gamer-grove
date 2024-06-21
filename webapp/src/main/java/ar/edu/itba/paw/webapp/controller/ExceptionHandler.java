package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

@ControllerAdvice
public class ExceptionHandler {

    @Autowired
    private MessageSource messageSource;
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandler.class);

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @org.springframework.web.bind.annotation.ExceptionHandler(value = {
            NoSuchPostException.class,
            NoSuchCommunityException.class,
            UserNotFoundException.class,
            NoSuchTokenException.class
    })
    public ModelAndView notFoundException() {
        ModelAndView mav = new ModelAndView("errors/error");
        mav.addObject("error_title", messageSource.getMessage("404", null, Locale.getDefault()));
        mav.addObject("error_message", messageSource.getMessage("404.message", null, Locale.getDefault()));
        LOGGER.atError().setMessage("404 NOT FOUND").log();
        return mav;
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @org.springframework.web.bind.annotation.ExceptionHandler(value = {
            NoLoggedUserException.class
    })
    public ModelAndView unauthorizedException() {
        ModelAndView mav = new ModelAndView("errors/error");
        mav.addObject("error_title", messageSource.getMessage("401", null, Locale.getDefault()));
        mav.addObject("error_message", messageSource.getMessage("401.message", null, Locale.getDefault()));
        LOGGER.atError().setMessage("401 UNAUTHORIZED").log();

        return mav;
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @org.springframework.web.bind.annotation.ExceptionHandler({BadCredentialsException.class})
    public ModelAndView unregisteredUserException(HttpServletRequest request) {
        LOGGER.atError().setMessage("401 UNAUTHORIZED").log();
        return new ModelAndView("user/login").addObject("error", messageSource.getMessage("Login.Invalid", null, request.getLocale()));
    }

}

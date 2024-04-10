package ar.edu.itba.paw.webapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import java.util.Locale;

@Controller
public class ErrorController {

    @Autowired
    private MessageSource messageSource;

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @RequestMapping("/403")
    public ModelAndView forbiddenException() {
        ModelAndView mav = new ModelAndView("errors/error");
        mav.addObject("error_title", messageSource.getMessage("403", null, Locale.getDefault()));
        mav.addObject("error_message", messageSource.getMessage("403.message", null, Locale.getDefault()));
        return mav;
    }
}

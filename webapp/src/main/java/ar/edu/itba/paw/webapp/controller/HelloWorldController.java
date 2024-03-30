package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ar.edu.itba.paw.webapp.excpetion.UserNotFoundException;
@Controller
public class HelloWorldController {
    private final UserService us;

    @Autowired
    public HelloWorldController(UserService us) {
        this.us = us;
    }


    @RequestMapping(method = RequestMethod.GET,path = "/{userId:\\d+}")
    public ModelAndView userProfile(@PathVariable("userId") final long userId) {
        final ModelAndView mav = new ModelAndView("user/profile");
        mav.addObject("user",us.findById(userId).orElseThrow(UserNotFoundException::new));
        return mav;
    }



    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView helloWorld() {
        final ModelAndView mav = new ModelAndView("index");
        mav.addObject("message", "Gamer groove");
        return mav;
    }
}

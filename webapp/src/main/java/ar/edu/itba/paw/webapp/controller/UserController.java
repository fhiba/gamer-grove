package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.services.UserService;
import ar.edu.itba.paw.webapp.form.LogInForm;
import ar.edu.itba.paw.webapp.form.RegisterUserForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.validation.BindingResult;

import javax.validation.Valid;

@Controller
public class UserController {

    @Autowired
    private UserService us;



    @RequestMapping(path = "/login")
    public ModelAndView getLogIn(@ModelAttribute("loginForm") final LogInForm loginForm) {
        ModelAndView mav = new ModelAndView("user/login");
        return mav;
    }

    @RequestMapping(path = "/register", method = RequestMethod.GET)
    public ModelAndView getRegister(@ModelAttribute("registerForm") final RegisterUserForm registerUserForm) {

        ModelAndView mav = new ModelAndView("user/register");
        return mav;
    }

    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public ModelAndView postRegister(@Valid @ModelAttribute("registerForm") final RegisterUserForm registerUserForm, final BindingResult errors) {
        if(errors.hasErrors()) {
            return getRegister(registerUserForm);

        }
        us.create(registerUserForm.getUsername(), registerUserForm.getEmail(), registerUserForm.getPassword());
        return new ModelAndView("redirect:/");
    }
}

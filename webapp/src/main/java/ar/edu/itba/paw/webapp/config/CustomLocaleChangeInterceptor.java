package ar.edu.itba.paw.webapp.config;


import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

public class CustomLocaleChangeInterceptor extends HandlerInterceptorAdapter {

    private String languageAttribute;
    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String preferredLanguage;
        Optional<User> user;
        try {
            user = userService.getLoggedUser();
        }
        catch (Exception e){
            user = Optional.empty();
        }
        if (user.isEmpty()) {
            preferredLanguage = request.getLocale().getLanguage();
        }else {
            preferredLanguage = user.get().getLocale();
        }
        Locale preferredLocale = new Locale(preferredLanguage);
        request.getSession().setAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, preferredLocale);

        return true;
    }
}
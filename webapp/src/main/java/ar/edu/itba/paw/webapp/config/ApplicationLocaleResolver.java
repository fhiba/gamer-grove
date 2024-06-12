package ar.edu.itba.paw.webapp.config;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

@Configuration
public class ApplicationLocaleResolver extends SessionLocaleResolver {
    @Autowired
    private UserService userService;

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        String localeOption;
        Optional<User> maybeUser;
        try {
            maybeUser = userService.getLoggedUser();
        }
        catch (Exception e){
            maybeUser = Optional.empty();
        }
        if(maybeUser.isEmpty() )
            localeOption = request.getLocale().getLanguage();
        else {
            localeOption = maybeUser.get().getLocale();
        }
        return new Locale(localeOption);
    }

}

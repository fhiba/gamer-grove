package ar.edu.itba.paw.webapp.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.services.UserService;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Provider
public class AcceptLanguageFilter implements ContainerRequestFilter {

    private static final String ES_LOCALE = "es";
    private static final String[] ACCEPTED_LOCALES = { Locale.ENGLISH.toString(), ES_LOCALE };

    @Autowired
    private UserService userService;

    private static final Logger LOGGER = LoggerFactory.getLogger(AcceptLanguageFilter.class);

    @Override
    public void filter(ContainerRequestContext requestContext) {
        String defaultLocale = Locale.ENGLISH.toString();
        final Optional<User> maybeUser = userService.getLoggedUser();

        if (maybeUser.isPresent()) {
            LOGGER.info("User is present");
            defaultLocale = maybeUser.get().getLocale();
        }

        final List<Locale> acceptableLanguages = requestContext.getAcceptableLanguages();
        LOGGER.info("Acceptable languages: {}", acceptableLanguages);
        final Locale selectedOrDefaultLocale = acceptableLanguages.stream()
                .filter(l -> Arrays.asList(ACCEPTED_LOCALES).contains(l.getLanguage()))
                .findAny()
                .orElse(Locale.of(defaultLocale));
        LOGGER.info("Selected or default locale: {}", selectedOrDefaultLocale);
        LocaleContextHolder.setLocale(selectedOrDefaultLocale);
    }
}

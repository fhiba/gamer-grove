package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.webapp.controller.CommunityController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomAccessDeniedHandler.class);


    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException exc) throws IOException, ServletException {
        String url = request.getRequestURI();
        if(url.contains("/delete") || url.contains("/new-community") || url.contains("/addMod") || url.contains("/info")) {
            response.sendRedirect(request.getContextPath() + "/403");
            return;
        }

    response.sendRedirect(request.getContextPath() + "/profile");
    }
}

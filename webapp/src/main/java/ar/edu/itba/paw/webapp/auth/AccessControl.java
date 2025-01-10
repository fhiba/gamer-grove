package ar.edu.itba.paw.webapp.auth;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.paw.services.UserService;
import ar.edu.itba.paw.models.User;

@Component
public class AccessControl {
    @Autowired
    private UserService us;

    @Transactional(readOnly = true)
    public boolean checkUser(HttpServletRequest request, long userId) {
        Optional<User> user = us.getLoggedUser();
        return user.isPresent() ? user.get().getId().equals(userId) : false;
    }

    @Transactional
    public boolean userHasImage(HttpServletRequest request){
        Optional<User> user = us.getLoggedUser();
        return user.filter(value -> value.getImage() != null).isPresent();
    }

    @Transactional
    public boolean imageIsUserImage(HttpServletRequest request, long imageId){
        Optional<User> user = us.getLoggedUser();
        return user.filter(value -> value.getImage() != null && value.getImage().getImageId() == imageId).isPresent();
    }

}

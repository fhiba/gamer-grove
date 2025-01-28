package ar.edu.itba.paw.webapp.auth;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.paw.services.PostService;
import ar.edu.itba.paw.services.UserService;
import ar.edu.itba.paw.webapp.mapper.ExceptionMapper;
import ar.edu.itba.paw.exceptions.NoLoggedUserException;
import ar.edu.itba.paw.exceptions.NoSuchPostException;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.User;

@Component
public class AccessControl {

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(AccessControl.class);

    @Autowired
    private UserService us;

    @Autowired
    private PostService ps;

    @Transactional(readOnly = true)
    public boolean checkUser(HttpServletRequest request, Long userId) {

        Optional<User> user = us.getLoggedUser();
        return user.isPresent() ? user.get().getId().equals(userId) : false;
    }

    @Transactional(readOnly = true)
    public boolean userHasImage(HttpServletRequest request) {
        Optional<User> user = us.getLoggedUser();
        return user.filter(value -> value.getImage() != null).isPresent();
    }

    @Transactional(readOnly = true)
    public boolean imageIsUserImage(HttpServletRequest request, Long imageId) {
        Optional<User> user = us.getLoggedUser();
        return user.filter(value -> value.getImage() != null && value.getImage().getImageId() == imageId).isPresent();
    }

    @Transactional(readOnly = true)
    public boolean followedByIsUser(HttpServletRequest request) {
        String param = request.getParameter("followedBy");
        if (param == null || param.isEmpty() || param.length() == 0)
            return true;
        Long id;
        try {
            id = Long.valueOf(param);
        } catch (Exception e) {
            LOGGER.error("Error parsing followedBy parameter");
            return false;

        }
        Optional<User> user = us.getLoggedUser();
        return user.filter(value -> value.getId().equals(Long.valueOf(id)) && value.getVerified()).isPresent();
    }

    @Transactional(readOnly = true)
    public boolean isModPost(HttpServletRequest request, Long postId) {
        Post post;
        try {
            post = ps.getPostById(postId);
        } catch (NoSuchPostException e) {
            return true;
        }
        Optional<User> user = us.getLoggedUser();
        if (!user.isPresent()) {
            return false;
        }
        return post.getcommunity().getModders().contains(user.get());
    }
}

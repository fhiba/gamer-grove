package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.NoLoggedUserException;
import ar.edu.itba.paw.exceptions.NoSuchTokenException;
import ar.edu.itba.paw.exceptions.UserNotFoundException;
import ar.edu.itba.paw.models.File;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.pagination.PaginatedDataWrapper;
import ar.edu.itba.paw.models.pagination.PaginationRequest;
import org.springframework.web.multipart.MultipartFile;
import ar.edu.itba.paw.exceptions.IllegalPageException;
import ar.edu.itba.paw.exceptions.PageNotFoundException;
import java.util.Arrays;
import java.util.Optional;
import java.util.List;

public interface UserService {

    Optional<User> findById(final long id);

    Optional<User> findByEmail(final String email);

    Optional<User> findByUsername(final String username);

    User create(String username, final String email, final String password) throws UserNotFoundException;

    Optional<User> getLoggedUser();

    void resetPassword(String token, String password) throws NoSuchTokenException;

    User verifyUser(String token) throws NoSuchTokenException;

    Boolean startResetPassword(String email) throws UserNotFoundException;

    void resendVerification() throws NoLoggedUserException, UserNotFoundException;

    void updateProfile(String locale, byte[] profilePic) throws NoLoggedUserException;

    User updateImage(User user, File value);

    List<User> getFollowersOfCommunity(long communityId);

    PaginatedDataWrapper<User> listUsers(PaginationRequest request) throws IllegalPageException, PageNotFoundException;
}

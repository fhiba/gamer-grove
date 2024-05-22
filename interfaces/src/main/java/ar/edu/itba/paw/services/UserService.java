package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.NoLoggedUserException;
import ar.edu.itba.paw.exceptions.NoSuchTokenException;
import ar.edu.itba.paw.exceptions.UserNotFoundException;
import ar.edu.itba.paw.models.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.List;
public interface UserService {

    Optional<User> findById(final long id);
    Optional<User> findByEmail(final String email);

    Optional<User> findByUsername(final String username);
    User create(String username, final String email, final String password) throws UserNotFoundException;

    Optional<User> getLoggedUser();


    User getLoggedUserChecked() throws NoLoggedUserException;

    List<User> findAll();

    void updateImageId(long id, long imageId);


    void resetPassword(String token, String password) throws NoSuchTokenException;

    Optional<User> verifyUser(String token) throws NoSuchTokenException;

    Boolean startResetPassword(String email) throws UserNotFoundException;

    void resendVerification() throws NoLoggedUserException, UserNotFoundException;

    void updateProfile(String locale, MultipartFile profilePic) throws NoLoggedUserException;
}

package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.IllegalPageException;
import ar.edu.itba.paw.exceptions.NoLoggedUserException;
import ar.edu.itba.paw.exceptions.NoSuchTokenException;
import ar.edu.itba.paw.exceptions.PageNotFoundException;
import ar.edu.itba.paw.exceptions.UserNotFoundException;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.pagination.PaginatedDataWrapper;
import ar.edu.itba.paw.models.pagination.PaginationRequest;
import ar.edu.itba.paw.persistance.UserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Transactional(readOnly = true)
@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private MailingService mailingService;

    @Autowired
    private FileService fs;

    public Optional<User> findById(long id) {
        return userDao.findById(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userDao.findByEmail(email);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    @Transactional
    @Override
    public User create(final String username, final String email, final String password) throws UserNotFoundException {
        User user = userDao.create(username, email, passwordEncoder.encode(password));
        String token = tokenService.generateValidationToken(user.getId());
        mailingService.sendValidationEmail(email, username, token, Locale.getDefault());
        LOGGER.atInfo().setMessage("Created new user {} sucessfully").addArgument(token)
                .addArgument(() -> user.getUsername()).log();
        return user;
    }

    @Override
    public Optional<User> getLoggedUser() {
        return SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal() instanceof java.lang.String
                        ? findByUsername((java.lang.String) SecurityContextHolder.getContext().getAuthentication()
                                .getPrincipal())
                        : Optional.empty();
    }

    @Transactional
    @Override
    public User updateImage(User user, File image) {
        User updatedUser = userDao.updateImage(user, image);
        LOGGER.atInfo().setMessage("Profile image {} of user updated successfully")
                .addArgument(() -> user.getImage().getImageId()).addArgument(() -> user.getUsername()).log();
        return updatedUser;
    }

    @Override
    public List<User> getFollowersOfCommunity(long communityId) {
        return userDao.getFollowersOfCommunity(communityId);
    }

    @Override
    public PaginatedDataWrapper<User> listUsers(PaginationRequest request)
            throws IllegalPageException, PageNotFoundException {
        if (request.getPageSize() < 1) {
            throw new IllegalArgumentException("Invalid Page size");
        }
        if (request.getPageNumber() < 1) {
            throw new IllegalPageException();
        }
        int totalCount = userDao.getUsersCount();

        if (totalCount == 0) {
            return new PaginatedDataWrapper<>(Collections.emptyList(), request.getPageNumber(), totalCount,
                    request.getPageSize());
        }
        int offset = (request.getPageNumber() - 1) * request.getPageSize();
        List<User> data = userDao.getUsers(request.getPageSize(), offset);
        PaginatedDataWrapper<User> dataWrapper = new PaginatedDataWrapper<>(data, request.getPageNumber(), totalCount,
                request.getPageSize());
        if (request.getPageNumber() > dataWrapper.getTotalPages() && dataWrapper.getTotalPages() != 0) {
            throw new PageNotFoundException();
        }
        return dataWrapper;
    }

    @Transactional
    @Override
    public void resetPassword(String token, String password) throws NoSuchTokenException, UserNotFoundException {
        Optional<Long> maybeId = tokenService.getUserIdFromToken(token, "ResetPass");
        if (maybeId.isEmpty()) {
            LOGGER.atError().setMessage("Token {} does not exist when trying to reset password").addArgument(token)
                    .log();
            throw new NoSuchTokenException();
        }
        User user = userDao.findById(maybeId.get())
                .orElseThrow(UserNotFoundException::new);
        userDao.updatePassword(user, passwordEncoder.encode(password));
        tokenService.deleteResetTokens(maybeId.get());
        LOGGER.atInfo().setMessage("Password of user {} updated with token {}").addArgument(() -> user.getUsername())
                .addArgument(token).log();
    }

    @Transactional
    @Override
    public User verifyUser(String token) throws NoSuchTokenException {

        if (token == null || token.isEmpty()) {
            LOGGER.atError().setMessage("Empty token provided when reseting password").log();
            throw new NoSuchTokenException();
        }

        Optional<User> maybeUser = tokenService.getUserFromToken(token, "Validation");
        if (maybeUser.isEmpty()) {
            LOGGER.atError().setMessage("Token {} does not exist").addArgument(token).log();
            throw new NoSuchTokenException();
        } else {
            User user = maybeUser.get();
            userDao.verifyUser(user);
            tokenService.deleteVerifyTokens(user.getId());
            LOGGER.atInfo().setMessage("User {} has been verified").addArgument(() -> user.getUsername()).log();
            return user;
        }
    }

    @Transactional
    @Override
    public Boolean startResetPassword(String email) throws UserNotFoundException {
        Optional<User> maybeUser = findByEmail(email);
        if (maybeUser.isEmpty()) {
            LOGGER.atWarn().setMessage("Cannot reset password of not existing user: {}").addArgument(email).log();
            return false;
        }
        User user = maybeUser.get();
        String token = tokenService.generateResetToken(user.getId());
        mailingService.sendResetPasswordEmail(user.getEmail(), user.getUsername(), token, Locale.of(user.getLocale()));
        LOGGER.atInfo().setMessage("Email to reset password of user {} has been sent").addArgument(email).log();
        return true;
    }

    @Transactional
    @Override
    public void resendVerification() throws NoLoggedUserException, UserNotFoundException {
        Optional<User> maybeUser = getLoggedUser();
        if (maybeUser.isPresent()) {
            User loggedUser = maybeUser.get();
            String token = tokenService.generateValidationToken(loggedUser.getId());
            mailingService.sendValidationEmail(loggedUser.getEmail(), loggedUser.getUsername(), token,
                    Locale.of(loggedUser.getLocale()));
            LOGGER.atInfo().setMessage("Email to reset password of user {} has been re-sent")
                    .addArgument(() -> loggedUser.getEmail()).log();
        } else {
            LOGGER.atError().setMessage("No user logged when trying to resend verification email").log();
            throw new NoLoggedUserException();
        }
    }

    @Transactional
    @Override
    public void updateProfile(String locale, byte[] profilePic) throws NoLoggedUserException {
        Optional<User> maybeUser = getLoggedUser();
        if (maybeUser.isPresent()) {
            LOGGER.atInfo().setMessage("Updating profile picture/locale of user {}")
                    .addArgument(() -> maybeUser.get().getUsername()).log();
            if (profilePic != null && profilePic.length > 0) {

                fs.uploadUserImage(profilePic);
                LOGGER.atInfo().setMessage("Updated profile picture of user {} successfully")
                        .addArgument(() -> maybeUser.get().getUsername()).log();

            }
            if (locale != null && !locale.isEmpty()) {
                userDao.updateLocale(maybeUser.get(), locale);
                LOGGER.atInfo().setMessage("Updated locale of user {} successfully")
                        .addArgument(() -> maybeUser.get().getUsername()).log();

            }

        } else {
            LOGGER.atError().setMessage("No user logged when trying to update profile picture/locale").log();
            throw new NoLoggedUserException();
        }
    }
}

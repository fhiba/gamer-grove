package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.NoLoggedUserException;
import ar.edu.itba.paw.exceptions.NoSuchTokenException;
import ar.edu.itba.paw.exceptions.UserNotFoundException;
import ar.edu.itba.paw.models.File;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistance.UserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

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
        User user =  userDao.create(username, email, passwordEncoder.encode(password));
        String token = tokenService.generateValidationToken(user.getId());
        mailingService.sendValidationEmail(email, username, token, Locale.getDefault());
        return user;
    }

    @Override
    public Optional<User> getLoggedUser() {
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof org.springframework.security.core.userdetails.User ?
                findByUsername(((org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername())
                : Optional.empty();
    }


    @Override
    public List<User> findAll() {
        return userDao.findAll();
    }

    @Transactional
    @Override
    public User updateImage(User user, File image) {
        return userDao.updateImage(user, image);
    }

    @Override
    public List<User> getFollowersOfCommunity(long communityId) {
        return userDao.getFollowersOfCommunity(communityId);
    }

    @Override
    public User getLoggedUserChecked() throws NoLoggedUserException {
        Optional<User> maybeUser = getLoggedUser();
        if(maybeUser.isEmpty())
            throw new NoLoggedUserException();
        return maybeUser.get();
    }


    @Transactional
    @Override
    public void resetPassword(String token, String password) throws NoSuchTokenException {
        Optional<Long> maybeId = tokenService.getUserIdFromToken(token, "ResetPass");
        if (maybeId.isEmpty()) {
            LOGGER.atError().setMessage("Token {} does not exist when trying to reset password").addArgument(token).log();
            throw new NoSuchTokenException("Token " + token + " does not exist");
        }

        userDao.updatePassword(userDao.findById(maybeId.orElseThrow()).orElseThrow(), passwordEncoder.encode(password));
        tokenService.deleteResetTokens(maybeId.get());
    }

    @Transactional
    @Override
    public User verifyUser(String token) throws NoSuchTokenException{
        Optional<User> maybeUser = tokenService.getUserFromToken(token, "Validation");
        if (maybeUser.isEmpty()) {
            LOGGER.atError().setMessage("Token {} does not exist").addArgument(token).log();
            throw new NoSuchTokenException("Token " + token + " does not exist");
        }else{
            User user = maybeUser.get();
            userDao.verifyUser(user);
            tokenService.deleteVerifyTokens(user.getId());
            return user;
        }
    }

    @Transactional
    @Override
    public Boolean startResetPassword(String email) throws UserNotFoundException {
        Optional<User> maybeUser = findByEmail(email);
        if(maybeUser.isEmpty()) {
            LOGGER.atWarn().setMessage("Cannot reset password of not existing user: {}").addArgument(email).log();
            return false;
        }
        User user = maybeUser.get();
        String token = tokenService.generateResetToken(user.getId());
        mailingService.sendResetPasswordEmail(user.getEmail(), user.getUsername(), token, Locale.of(user.getLocale()));
        return true;
    }


    @Transactional
    @Override
    public void resendVerification() throws NoLoggedUserException, UserNotFoundException {
        User loggedUSer = getLoggedUserChecked();
        String token = tokenService.generateValidationToken(loggedUSer.getId());
        mailingService.sendValidationEmail(loggedUSer.getEmail(), loggedUSer.getUsername(), token, Locale.of(loggedUSer.getLocale()));
    }

    @Transactional
    @Override
    public void updateProfile(String locale, MultipartFile profilePic) throws NoLoggedUserException {
        if(!profilePic.isEmpty() && !Objects.isNull(profilePic)) {
            fs.uploadUserImage(profilePic);
        }
        userDao.updateLocale(getLoggedUserChecked(), locale);
    }
}

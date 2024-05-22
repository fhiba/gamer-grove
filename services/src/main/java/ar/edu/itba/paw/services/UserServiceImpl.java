package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.NoLoggedUserException;
import ar.edu.itba.paw.exceptions.NoSuchTokenException;
import ar.edu.itba.paw.exceptions.UserNotFoundException;
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
        mailingService.sendValidationEmail(email, username, token);
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
    public void updateImageId(long id, long imageId) {
        userDao.updateImageId(id, imageId);
    }

    @Override
    public User getLoggedUserChecked() throws NoLoggedUserException {
        Optional<User> maybeUser = getLoggedUser();
        if(maybeUser.isEmpty())
            throw new NoLoggedUserException("User not logged");
        return maybeUser.get();
    }


    @Transactional
    @Override
    public void resetPassword(String token, String password) throws NoSuchTokenException {
        Optional<Long> maybeId = tokenService.getUserIdFromToken(token, "ResetPass");
        if (maybeId.isEmpty()) {
            LOGGER.debug("Token {} does not exist",token);
            throw new NoSuchTokenException("Token " + token + " does not exist");
        }

        userDao.updatePassword(maybeId.get(), passwordEncoder.encode(password));
        tokenService.deleteResetTokens(maybeId.get());
    }

    @Transactional
    @Override
    public Optional<User> verifyUser(String token) throws NoSuchTokenException{
        Optional<Long> maybeId = tokenService.getUserIdFromToken(token, "Validation");
        if (maybeId.isEmpty()) {
            LOGGER.debug("Token {} does not exist",token);
            throw new NoSuchTokenException("Token " + token + " does not exist");
        }
        userDao.verifyUser(maybeId.get());
        return userDao.findById(maybeId.get());
    }

    @Transactional
    @Override
    public Boolean startResetPassword(String email) throws UserNotFoundException {
        Optional<User> maybeUser = findByEmail(email);
        if(maybeUser.isEmpty())
            return false;
        String token = tokenService.generateResetToken(maybeUser.get().getId());
        mailingService.sendResetPasswordEmail(maybeUser.get().getEmail(), maybeUser.get().getUsername(), token);
        return true;
    }


    @Transactional
    @Override
    public void resendVerification() throws NoLoggedUserException, UserNotFoundException {
        User loggedUSer = getLoggedUserChecked();
        String token = tokenService.generateValidationToken(loggedUSer.getId());
        mailingService.sendValidationEmail(loggedUSer.getEmail(), loggedUSer.getUsername(), token);
    }

    @Transactional
    @Override
    public void updateProfile(String locale, MultipartFile profilePic) throws NoLoggedUserException {
        if(!profilePic.isEmpty() && !Objects.isNull(profilePic)) {
            fs.uploadUserImage(profilePic);
        }
        userDao.updateLocale(getLoggedUserChecked().getId(), locale);
    }
}

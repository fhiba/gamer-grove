package ar.edu.itba.paw.services;


import ar.edu.itba.paw.exceptions.NoSuchTokenException;
import ar.edu.itba.paw.exceptions.UserNotFoundException;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistance.TokenDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;


@Transactional(readOnly = true)
@Service
public class TokenServiceImpl implements TokenService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenServiceImpl.class);


    @Autowired
    private TokenDao tokenDao;

    @Autowired
    private UserService userService;



    @Override
    public Optional<Long> getUserIdFromToken(String token, String type) throws NoSuchTokenException {
        return tokenDao.getIdFromToken(token, type);
    }
    @Override
    public Optional<User> getUserFromToken(String token, String type) throws NoSuchTokenException {
        return tokenDao.getUserFromToken(token, type);
    }

    @Override
    public Boolean verifyResetToken(String token) {
        return tokenDao.verifyResetToken(token);
    }

    @Transactional
    @Override
    public void deleteVerifyTokens(Long userId) {
        tokenDao.deleteValidationTokens(userId);
    }

    @Transactional
    @Override
    public void deleteResetTokens(Long userId) {
        tokenDao.deleteResetTokens(userId);
    }

    @Transactional
    @Override
    public String generateValidationToken(Long userId) throws UserNotFoundException {
         UUID uuid = UUID.randomUUID();
        String token = uuid.toString();
        Optional<User> maybeUser = userService.findById(userId);
        if(maybeUser.isEmpty()) {
            LOGGER.atError().setMessage("Trying to generate validation token of unexsiting user with id {}").addArgument(userId).log();
            throw new UserNotFoundException("User with id " + userId + " does not exist");
        }
        User user = maybeUser.get();
        tokenDao.createValidationToken(user, token);
        return token;
    }


    @Transactional
    @Override
    public String generateResetToken(Long userId) throws UserNotFoundException {
        UUID uuid = UUID.randomUUID();
        String token = uuid.toString();
        Optional<User> maybeUser = userService.findById(userId);
        if(maybeUser.isEmpty()) {
            LOGGER.atError().setMessage("Trying to reset token of unexsiting user with id {}").addArgument(userId).log();
            throw new UserNotFoundException("User with id " + userId + " does not exist");
        }
        tokenDao.createResetToken(maybeUser.get(), token);
        return token;
    }

}

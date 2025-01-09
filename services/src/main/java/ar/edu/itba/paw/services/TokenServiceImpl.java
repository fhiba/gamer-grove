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
    public Boolean verifyResetToken(String token) throws NoSuchTokenException {
        if (token == null || token.isEmpty()) {
            LOGGER.atError().setMessage("Empty token provided when reseting password").log();
            throw new NoSuchTokenException();
        }
        return tokenDao.verifyResetToken(token);
    }

    @Override
    public Boolean verifyVerifyToken(String token) throws NoSuchTokenException {
        if (token == null || token.isEmpty()) {
            LOGGER.atError().setMessage("Empty token provided when verifying user").log();
            throw new NoSuchTokenException();
        }
        return tokenDao.verifyValidationToken(token);
    }

    @Transactional
    @Override
    public void deleteVerifyTokens(Long userId) {
        tokenDao.deleteValidationTokens(userId);
        LOGGER.atInfo().setMessage("Deleted verification token of user {} sucessfully").addArgument(userId).log();
    }

    @Transactional
    @Override
    public void deleteResetTokens(Long userId) {
        tokenDao.deleteResetTokens(userId);
        LOGGER.atInfo().setMessage("Deleted reset token of user {} sucessfully").addArgument(userId).log();
    }

    @Transactional
    @Override
    public String generateValidationToken(Long userId) throws UserNotFoundException {
        UUID uuid = UUID.randomUUID();
        String token = uuid.toString();
        Optional<User> maybeUser = userService.findById(userId);
        if (maybeUser.isEmpty()) {
            LOGGER.atError().setMessage("Trying to generate validation token of unexsiting user with id {}")
                    .addArgument(userId).log();
            throw new UserNotFoundException();
        }
        User user = maybeUser.get();
        tokenDao.createValidationToken(user, token);
        LOGGER.atInfo().setMessage("Generated verification token {} of user {} sucessfully").addArgument(token)
                .addArgument(userId).log();
        return token;
    }

    @Transactional
    @Override
    public String generateResetToken(Long userId) throws UserNotFoundException {
        UUID uuid = UUID.randomUUID();
        String token = uuid.toString();
        Optional<User> maybeUser = userService.findById(userId);
        if (maybeUser.isEmpty()) {
            LOGGER.atError().setMessage("Trying to reset token of unexsiting user with id {}").addArgument(userId)
                    .log();
            throw new UserNotFoundException();
        }
        tokenDao.createResetToken(maybeUser.get(), token);
        LOGGER.atInfo().setMessage("Generated reset token {} of user {} sucessfully").addArgument(token)
                .addArgument(userId).log();
        return token;
    }

}

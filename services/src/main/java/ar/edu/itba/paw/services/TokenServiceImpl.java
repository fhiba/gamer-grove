package ar.edu.itba.paw.services;


import ar.edu.itba.paw.exceptions.NoSuchTokenException;
import ar.edu.itba.paw.exceptions.UserNotFoundException;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistance.TokenDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;


@Transactional(readOnly = true)
@Service
public class TokenServiceImpl implements TokenService {

    @Autowired
    TokenDao tokenDao;

    @Transactional
    @Override
    public void verifyUser(String token) throws NoSuchTokenException, UserNotFoundException {
      /*  Optional<Long> userId = getUserIdFromToken(token);
        if(userId.isEmpty())
            throw new NoSuchTokenException("Token " + token + " does not exist");
        //userService.verifyUser(userId.get());
        deleteVerifyTokens(userId.get());*/
    }


    @Override
    public Optional<Long> getUserIdFromToken(String token, String type) throws NoSuchTokenException {
        return tokenDao.getIdFromToken(token, type);
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
    public String generateValidationToken(Long userId) {
         UUID uuid = UUID.randomUUID();
        String token = uuid.toString();
        tokenDao.createValidationToken(userId, token);
        return token;
    }


    @Transactional
    @Override
    public String generateResetToken(Long userId) {
        UUID uuid = UUID.randomUUID();
        String token = uuid.toString();
        tokenDao.createResetToken(userId, token);
        return token;
    }

}

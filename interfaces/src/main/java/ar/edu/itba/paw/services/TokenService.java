package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.NoSuchTokenException;
import ar.edu.itba.paw.exceptions.UserNotFoundException;

import java.util.Optional;

public interface TokenService {

    void verifyUser(String token) throws NoSuchTokenException, UserNotFoundException;


    Optional<Long> getUserIdFromToken(String token, String type)throws NoSuchTokenException;

    Boolean verifyResetToken(String token);

    void deleteVerifyTokens(Long userId);

    void deleteResetTokens(Long userId);

    String generateValidationToken(Long userId);
    String generateResetToken(Long userId);
}

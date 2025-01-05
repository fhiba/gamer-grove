package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.NoSuchTokenException;
import ar.edu.itba.paw.exceptions.UserNotFoundException;
import ar.edu.itba.paw.models.User;

import java.util.Optional;

public interface TokenService {

    Optional<Long> getUserIdFromToken(String token, String type) throws NoSuchTokenException;

    Optional<User> getUserFromToken(String token, String type) throws NoSuchTokenException;

    Boolean verifyResetToken(String token) throws NoSuchTokenException;

    Boolean verifyVerifyToken(String token) throws NoSuchTokenException;

    void deleteVerifyTokens(Long userId);

    void deleteResetTokens(Long userId);

    String generateValidationToken(Long userId) throws UserNotFoundException;

    String generateResetToken(Long userId) throws UserNotFoundException;
}

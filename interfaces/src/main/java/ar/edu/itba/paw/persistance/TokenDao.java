package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.models.Token;
import ar.edu.itba.paw.models.User;

import java.util.Optional;

public interface TokenDao {
    Boolean verifyValidationToken(String token);
    Boolean verifyResetToken(String token);
    Optional<Long> getIdFromToken(String token, String type);

    Optional<User> getUserFromToken(String token, String type);

    Token createValidationToken(User user, String token);
    Token createResetToken(User userId, String token);

    void deleteValidationTokens(Long userId);

    void deleteResetTokens(Long userId);
}

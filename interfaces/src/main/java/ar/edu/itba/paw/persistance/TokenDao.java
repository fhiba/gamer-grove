package ar.edu.itba.paw.persistance;

import java.util.Optional;

public interface TokenDao {
    Boolean verifyValidationToken(String token);
    Boolean verifyResetToken(String token);
    Optional<Long> getIdFromToken(String token, String type);

    Boolean createValidationToken(Long userId, String token);
    Boolean createResetToken(Long userId, String token);

    void deleteValidationTokens(Long userId);

    void deleteResetTokens(Long userId);
}

package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.models.Token;
import ar.edu.itba.paw.models.User;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Optional;

@Repository
@Primary
public class TokenDaoJpa implements TokenDao{
    @PersistenceContext
    EntityManager em;

    @Override
    public Boolean verifyValidationToken(String token) {
        TypedQuery<Token> query = em.createQuery("from Token where value = :value and type = 'Validation'",Token.class);
        query.setParameter("value",token);
        return query.getResultList().stream().findFirst().isPresent();
    }

    @Override
    public Boolean verifyResetToken(String token) {
        TypedQuery<Token> query = em.createQuery("from Token where value = :value and type = 'ReserPass'",Token.class);
        query.setParameter("value",token);
        return query.getResultList().stream().findFirst().isPresent();
    }

    @Override
    public Optional<Long> getIdFromToken(String token, String type) {
        return Optional.ofNullable(em.find(Token.class,token).getUser().getId());
    }

    @Override
    public Token createValidationToken(User user, String token) {
        Token out = new Token(token,user,"Validation");
        em.persist(out);
        return out;
    }

    @Override
    public Token createResetToken(User user, String token) {
        Token out = new Token(token,user,"ResetPass");
        em.persist(out);
        return out;
    }

    @Override
    public void deleteValidationTokens(Long userId) {
        em.createNativeQuery("DELETE FROM token WHERE user_id = ? AND type = ?")
                .setParameter(1,userId)
                .setParameter(2,"Validation")
                .executeUpdate();
    }

    @Override
    public void deleteResetTokens(Long userId) {
        em.createNativeQuery("DELETE FROM token WHERE user_id = ? AND type = ?")
                .setParameter(1,userId)
                .setParameter(2,"ResetPass")
                .executeUpdate();
    }
}

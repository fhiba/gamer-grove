import ar.edu.itba.paw.models.Token;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistance.TokenDaoJpa;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
@Rollback
public class TokenDaoTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    TokenDaoJpa tokenDao;

    @Test
    @Rollback
    public void testVerifyValidationToken() {
        Boolean result = tokenDao.verifyValidationToken("vaaa");
        assertTrue(result);
    }
    @Test
    @Rollback
    public void testVerifyResetToken() {
        Boolean result = tokenDao.verifyResetToken("raaa");
        assertTrue(result);
    }

    @Test
    @Rollback
    public void testGetIdFromToken() {
        Long result = tokenDao.getIdFromToken("vaaa","Validation").get();
        assertEquals(10L,result.longValue());
    }

    @Test
    @Rollback
    public void testCreateValidationToken() {
        User user = new User("Pedro", "curti", "pedro@curti.com",false,"en",false);
        user.setId(10L);
        Token token = tokenDao.createValidationToken(user,"valid");
        em.flush();
        assertEquals("valid",token.getValue());
        assertEquals(3,((Number)em.createNativeQuery("SELECT count(*) FROM token").getSingleResult()).intValue());
    }

    @Test
    @Rollback
    public void testCreateResetToken() {
        User user = new User("Pedro", "curti", "pedro@curti.com",false,"en",false);
        user.setId(10L);
        Token token = tokenDao.createResetToken(user,"reset");
        em.flush();
        assertEquals("reset",token.getValue());
        assertEquals(3,((Number)em.createNativeQuery("SELECT count(*) FROM token").getSingleResult()).intValue());
    }

    @Test
    @Rollback
    public void testDeleteValidationTokens() {
        tokenDao.deleteValidationTokens(10L);
        em.flush();
        assertEquals(1,((Number)em.createNativeQuery("SELECT count(*) FROM token").getSingleResult()).intValue());
    }

    @Test
    @Rollback
    public void testDeleteResetTokens() {
        tokenDao.deleteResetTokens(10L);
        em.flush();
        assertEquals(1,((Number)em.createNativeQuery("SELECT count(*) FROM token").getSingleResult()).intValue());
    }
}

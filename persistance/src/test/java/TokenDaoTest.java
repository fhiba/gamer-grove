import ar.edu.itba.paw.models.Token;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistance.TokenDaoJpa;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

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

    private static final String USERNAME = "Pedro";

    private static final String PASSWORD = "curti";

    private static final String EMAIL = "pedro@curti.com";

    private static final Boolean VERIFIED = false;

    private static final String LOCALE = "en";

    private static final Boolean OWNER = false;

    private static final Long USER_ID = 10L;

    private static final String TABLE = "token";

    @Autowired
    private DataSource ds;
    private JdbcTemplate jdbcTemplate;
    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
    }

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
    public void testGetUserFromToken() {
        User result = tokenDao.getUserFromToken("vaaa","Validation").get();
        assertEquals(10L,result.getId().longValue());
    }

    @Test
    @Rollback
    public void testCreateValidationToken() {
        User user = new User(USERNAME, PASSWORD, EMAIL,VERIFIED,LOCALE,OWNER);
        user.setId(USER_ID);
        Token token = tokenDao.createValidationToken(user,"valid");
        em.flush();
        assertEquals("valid",token.getValue());
        assertEquals(3, JdbcTestUtils.countRowsInTable(jdbcTemplate, TABLE));
    }

    @Test
    @Rollback
    public void testCreateResetToken() {
        User user = new User(USERNAME, PASSWORD, EMAIL,VERIFIED,LOCALE,OWNER);
        user.setId(USER_ID);
        Token token = tokenDao.createResetToken(user,"reset");
        em.flush();
        assertEquals("reset",token.getValue());
        assertEquals(3,JdbcTestUtils.countRowsInTable(jdbcTemplate, TABLE));
    }

    @Test
    @Rollback
    public void testDeleteValidationTokens() {
        tokenDao.deleteValidationTokens(10L);
        em.flush();
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, TABLE));
    }

    @Test
    @Rollback
    public void testDeleteResetTokens() {
        tokenDao.deleteResetTokens(10L);
        em.flush();
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, TABLE));
    }
}

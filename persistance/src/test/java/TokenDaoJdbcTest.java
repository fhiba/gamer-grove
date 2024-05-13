import ar.edu.itba.paw.persistance.TokenDaoJdbc;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;
import java.util.Optional;
import java.util.UUID;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class TokenDaoJdbcTest {
    @Autowired
    private DataSource ds;
    @Autowired
    private TokenDaoJdbc tokenDao;

    private JdbcTemplate jdbcTemplate;

    private final String TOKEN = UUID.randomUUID().toString();
    private final long POPULATED_USER_ID = 1L;
    private final String POPULATED_VALIDATION_TOKEN = "vaaa";
    private final String POPULATED_RESET_TOKEN = "raaa";
    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void testCreateValidationToken() {
        Assert.assertTrue(tokenDao.createValidationToken( POPULATED_USER_ID, TOKEN));
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void testCreateValidationTokenWrongUserId() {
        Assert.assertTrue(tokenDao.createValidationToken(99L, TOKEN));
    }


    @Test
    public void TestVerifyValidationToken() {
        Assert.assertTrue(tokenDao.verifyValidationToken(POPULATED_VALIDATION_TOKEN));
    }
    @Test
    public void TestVerifyValidationTokenWrong() {
        Assert.assertFalse(tokenDao.verifyValidationToken(POPULATED_RESET_TOKEN));
    }
    @Test
    public void TestVerifyResetPassToken() {
        Assert.assertTrue(tokenDao.verifyResetToken(POPULATED_RESET_TOKEN));
    }
    @Test
    public void TestGetIdFromToken() {
        Optional<Long> id = tokenDao.getIdFromToken(POPULATED_VALIDATION_TOKEN, "Validation");
        id.ifPresent(aLong -> Assert.assertEquals(POPULATED_USER_ID, (long) aLong));
    }


    @Test
    public void TestGetIdFromNotExistingToken() {
        Optional<Long> id = tokenDao.getIdFromToken(TOKEN, "Validation");
        Assert.assertFalse(id.isPresent());
    }

    @Test
    public void TestGetIdFromNotExistingType() {
        Optional<Long> id = tokenDao.getIdFromToken(POPULATED_VALIDATION_TOKEN, "NotExisting");
        Assert.assertFalse(id.isPresent());
    }
    @Test
    public void deleteValidationTokens() {
        tokenDao.deleteValidationTokens(POPULATED_USER_ID);
    }
}

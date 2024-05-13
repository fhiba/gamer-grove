import ar.edu.itba.paw.persistance.ModderDaoJdbc;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;

import javax.sql.DataSource;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class ModderDaoJdbcTest {

    private final static int MOD_USER_ID = 1;
    private final static int MOD_COMMUNITY_ID = 1;
    private final static int NON_MOD_USER_ID = 2;

    @Autowired
    private ModderDaoJdbc modderDao;

    @Autowired
    private DataSource ds;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void testAddModder() {
        modderDao.addModder(NON_MOD_USER_ID, MOD_COMMUNITY_ID);
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, "modders"));
    }

    @Test(expected = DuplicateKeyException.class)
    public void testAddModderThatAlreadyExist() {
        modderDao.addModder(MOD_USER_ID, MOD_COMMUNITY_ID);
    }

    @Test
    public void testIsModderOfCommunity() {
        assertTrue(modderDao.isModderOfCommunity(MOD_USER_ID, MOD_COMMUNITY_ID));
    }

    @Test
    public void testIsNotModderOfCommunity() {
        assertFalse(modderDao.isModderOfCommunity(NON_MOD_USER_ID, MOD_COMMUNITY_ID));
    }

    @Test
    public void testRemoveModder() {
        modderDao.removeModder(MOD_USER_ID, MOD_COMMUNITY_ID);
        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, "modders"));
    }
}

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistance.UserDaoJdbc;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;

import javax.sql.DataSource;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class UserJdbcDaoTest {
    private static final String PASSWORD = "Password";
    private static final String USERNAME = "Username";
    private static final String EMAIL = "Email";

    // 'Pedro', 'curti', ''
    private static final String EXISTING_USER = "Pedro";
    private static final String EXISTING_EMAIL = "pedro@curti.com";

    private static final long EXISTING_ID = 1L;

    @Autowired
    private DataSource ds;
    @Autowired
    private UserDaoJdbc userDao;
    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void testCreate() {
        final User user = userDao.create(USERNAME, EMAIL,PASSWORD);
        assertNotNull(user);
        assertEquals(USERNAME, user.getUsername());
        assertEquals(PASSWORD, user.getPassword());
        assertEquals(3, JdbcTestUtils.countRowsInTable(jdbcTemplate, "users"));
    }

    @Test
    public void testFindByUsername() {
        final User user = userDao.findByUsername(EXISTING_USER).get();
        assertNotNull(user);
        assertEquals(EXISTING_ID, user.getId());
        assertEquals(EXISTING_USER, user.getUsername());
        assertEquals(EXISTING_EMAIL, user.getEmail());
    }

    @Test
    public void testFindByCommunity(){
        final List<User> users = userDao.findByCommunity("test");
        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals(EXISTING_ID, users.get(0).getId());
        assertEquals(EXISTING_USER, users.get(0).getUsername());
        assertEquals(EXISTING_EMAIL, users.get(0).getEmail());
    }



}
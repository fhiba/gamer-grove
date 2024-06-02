import ar.edu.itba.paw.models.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import ar.edu.itba.paw.persistance.UserDao;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
public class UserDaoTest {

    @Autowired
    private UserDao userDao;

    @Autowired
    private DataSource ds;

    @PersistenceContext
    private EntityManager em;


    private static final String USERNAME = "Username";
    private static final String EMAIL = "mail@mail";
    private static final String PASSWORD = "Password";

    private static final byte[] byteBlob = {0, 1, 2, 3, 4};

    @Test
    @Rollback
    public void testCreate() {
        final User user = userDao.create(USERNAME, EMAIL, PASSWORD);

        em.flush();
        assertNotNull(user);
        assertEquals(USERNAME, user.getUsername());
        assertEquals(PASSWORD, user.getPassword());
        assertEquals(1,user.getId().intValue());
    }
    //Pedro', 'curti', 'pedro@curti.com'
    @Test
    public void testFindById() {
        final Optional<User> user = userDao.findById(10);
        assertNotNull(user);
        assertEquals("Pedro", user.get().getUsername());
        assertEquals("curti", user.get().getPassword());
        assertEquals("pedro@curti.com", user.get().getEmail());
    }

    @Test
    public void testFindByEmail() {
        final Optional<User> user = userDao.findByEmail("pedro@curti.com");
        assertNotNull(user);
        assertEquals("Pedro", user.get().getUsername());
        assertEquals("curti", user.get().getPassword());
        assertEquals("pedro@curti.com", user.get().getEmail());
    }

    @Test
    public void testFindByUsername() {
        final Optional<User> user = userDao.findByUsername("Pedro");
        assertNotNull(user);
        assertEquals("Pedro", user.get().getUsername());
        assertEquals("curti", user.get().getPassword());
        assertEquals("pedro@curti.com", user.get().getEmail());
    }



    @Test
    public void testGetFollowersOfCommunity() {
        List<User> followers = userDao.getFollowersOfCommunity(10);
        assertEquals(1,followers.size());
    }



}

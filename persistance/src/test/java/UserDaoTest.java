import ar.edu.itba.paw.models.File;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistance.UserDaoJPA;
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

import java.util.HexFormat;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
public class UserDaoTest {

    @Autowired
    private UserDaoJPA userDao;

    @Autowired
    private DataSource ds;

    @PersistenceContext
    private EntityManager em;


    private static final String NEW_USERNAME = "Username";
    private static final String NEW_EMAIL = "mail@mail";
    private static final String NEW_PASSWORD = "Password";

    private static final String USERNAME = "Pedro";

    private static final String PASSWORD = "curti";

    private static final String EMAIL = "pedro@curti.com";

    private static final Boolean VERIFIED = false;

    private static final String LOCALE = "en";

    private static final Boolean OWNER = false;

    private static final Long USER_ID = 10L;

    final static byte[] TEST = HexFormat.ofDelimiter(":")
            .parseHex("e0:4f:d0:20:ea:3a:69:10:a2:d8:08:00:2b:30:30:9d");

    @Test
    @Rollback
    public void testCreate() {
        final User user = userDao.create(NEW_USERNAME, NEW_EMAIL, NEW_PASSWORD);
        em.flush();
        assertNotNull(user);
        assertEquals(NEW_USERNAME, user.getUsername());
        assertEquals(NEW_PASSWORD, user.getPassword());
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
        List<User> followers = userDao.getFollowersOfCommunity(10L);
        assertEquals(1,followers.size());
    }

    @Test
    @Rollback
    public void testUpdateImage() {
        User user = new User(USERNAME, PASSWORD, EMAIL,VERIFIED,LOCALE,OWNER);
        user.setId(USER_ID);
        File byteBlob = new File(TEST);
        byteBlob.setImageId(10L);
        user = userDao.updateImage(user, byteBlob);
        assertEquals(byteBlob.getImageId(), user.getImage().getImageId());
    }

    @Test
    @Rollback
    public void testVerifyUser() {
        User user = new User(USERNAME, PASSWORD, EMAIL,VERIFIED,LOCALE,OWNER);
        user.setId(USER_ID);
        user = userDao.verifyUser(user);
        assertNotNull(user);
        assertEquals(true, user.isVerified());
    }

    @Test
    @Rollback
    public void testUpdateLocale() {
        User user = new User(USERNAME, PASSWORD, EMAIL,VERIFIED,LOCALE,OWNER);
        user.setId(USER_ID);
        user = userDao.updateLocale(user, "es");
        assertNotNull(user);
        assertEquals("es", user.getLocale());
    }



}

import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.Mod;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistance.ModderDaoJpa;
import ar.edu.itba.paw.persistance.PostDaoJpa;
import org.junit.Assert;
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
import java.time.LocalDateTime;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
public class ModderDaoTest {

    private static final LocalDateTime NOW = LocalDateTime.now();

    private static final LocalDateTime EXISTING_TIME = LocalDateTime.of(2024, 5, 5, 20, 30);

    private static final String USERNAME = "Pedro";

    private static final String PASSWORD = "curti";

    private static final String EMAIL = "pedro@curti.com";

    private static final Boolean VERIFIED = false;

    private static final String LOCALE = "en";

    private static final Boolean OWNER = false;

    private static final Long USER_ID = 10L;

    private static final String COMMUNITY_NAME = "test";
    private static final String COMMUNITY_DESCRIPTION = "This is a test community";
    private static final String COMMUNITY_PUBLISHER = "falsepub";
    private static final String COMMUNITY_DEVELOPER = "falsedeveloper";

    private static final Long COMMUNITY_ID = 10L;


    @PersistenceContext
    private EntityManager em;

    @Autowired
    private ModderDaoJpa modDao;

    @Autowired
    private DataSource ds;
    private JdbcTemplate jdbcTemplate;

    private static final String TABLE = "modders";
    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    @Rollback
    public void testAddMod(){
        User user = new User("notMod", "curti", "notmod@hotmail.com",false,"en",false);
        user.setId(11L);
        user = em.merge(user);
        em.flush();
        Community community = new Community("other", "This is a test community", null, "falsepub", "falsedeveloper", EXISTING_TIME);
        community.setId(11L);
        community = em.merge(community);
        em.flush();
        Mod mod = modDao.addModder(user, community);
        em.flush();
        Assert.assertNotNull(mod);
        Assert.assertEquals(2, JdbcTestUtils.countRowsInTable(jdbcTemplate, TABLE));

    }

    @Test
    public void testIsModderOfCommunity() {
        User user = new User(USERNAME, PASSWORD, EMAIL,VERIFIED,LOCALE,OWNER);
        user.setId(USER_ID);
        Community community = new Community(COMMUNITY_NAME,COMMUNITY_DESCRIPTION, null,COMMUNITY_PUBLISHER,COMMUNITY_DEVELOPER, NOW);
        community.setId(COMMUNITY_ID);

        Boolean isMod = modDao.isModderOfCommunity(user,community);
        Assert.assertTrue(isMod);

    }

    @Test
    public void testFindByid() {
        User user = new User(USERNAME, PASSWORD, EMAIL,VERIFIED,LOCALE,OWNER);
        user.setId(USER_ID);
        Community community = new Community(COMMUNITY_NAME,COMMUNITY_DESCRIPTION, null,COMMUNITY_PUBLISHER,COMMUNITY_DEVELOPER, NOW);
        community.setId(COMMUNITY_ID);
        Mod mod = modDao.findByid(user,community).get();
        Assert.assertNotNull(mod);
    }

    @Test
    @Rollback
    public void testRemoveModder() {
        User user = new User(USERNAME, PASSWORD, EMAIL,VERIFIED,LOCALE,OWNER);
        user.setId(USER_ID);
        user = em.merge(user);
        em.flush();
        Community community = new Community(COMMUNITY_NAME,COMMUNITY_DESCRIPTION, null,COMMUNITY_PUBLISHER,COMMUNITY_DEVELOPER, NOW);
        community.setId(COMMUNITY_ID);
        community = em.merge(community);
        em.flush();
        Mod mod = new Mod(user,community, NOW);
        mod = em.merge(mod);
        em.flush();
        modDao.removeModder(mod);
        em.flush();
        Assert.assertEquals(0,JdbcTestUtils.countRowsInTable(jdbcTemplate, TABLE));


    }

    @Test
    public void testGetAllModdersPaginated() {
        List<Mod> mods = modDao.getAllModdersPaginated(1,0);
        Assert.assertEquals(1, mods.size());
    }
    @Test
    public void testGetModdersPaginatedByCommunity() {
        List<Mod> mods = modDao.getModdersPaginatedByCommunity(10L,1,0);
        Assert.assertEquals(1, mods.size());
    }

    @Test
    public void testGetTotalModders() {
        Integer total = modDao.getTotalModders();
        Assert.assertEquals(1, total.intValue());
    }

    @Test
    public void testGetTotalModdersByCommunity(){
        Integer total = modDao.getTotalModdersByCommunity(10L);
        Assert.assertEquals(1, total.intValue());
    }

    @Test
    public void testGetTotalModdersByUserId(){
        Integer total = modDao.getTotalModdersByUserId(10L);
        Assert.assertEquals(1, total.intValue());
    }

    @Test
    public void testGetModdersPaginatedByUserId(){
        List<Mod> mods = modDao.getModdersPaginatedByUserId(10L,1,0);
        Assert.assertEquals(1, mods.size());
    }

}

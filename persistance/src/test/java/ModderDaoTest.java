import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.Mod;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistance.ModderDaoJpa;
import ar.edu.itba.paw.persistance.PostDaoJpa;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
public class ModderDaoTest {

    private static final LocalDateTime NOW = LocalDateTime.now();

    private static final LocalDateTime EXISTING_TIME = LocalDateTime.of(2024, 5, 5, 20, 30);


    @PersistenceContext
    private EntityManager em;

    @Autowired
    private ModderDaoJpa modDao;
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
        Assert.assertEquals(3,((Number) em.createNativeQuery("SELECT count(*) FROM modders").getSingleResult()).intValue());

    }

    @Test
    public void testIsModderOfCommunity() {
        User user = new User("Pedro", "curti", "pedro@curti.com", false, "en", false);
        user.setId(10L);
        user = em.merge(user);
        em.flush();
        Community community = new Community("test", "This is a test community", null, "falsepub", "falsedeveloper", EXISTING_TIME);
        community.setId(10L);
        community = em.merge(community);
        em.flush();
        Boolean isMod = modDao.isModderOfCommunity(user,community);
        Assert.assertEquals(true,isMod.booleanValue());

    }

    @Test
    public void testFindByid() {
        User user = new User("Pedro", "curti", "pedro@curti.com", false, "en", false);
        user.setId(10L);
        user = em.merge(user);
        Community community = new Community("other", "This is a test community", null, "falsepub", "falsedeveloper", EXISTING_TIME);
        community.setId(11L);
        community = em.merge(community);
        Mod mod = modDao.findByid(user,community).get();
        Assert.assertNotNull(mod);
    }

    @Test
    @Rollback
    public void testRemoveModder() {
        User user = new User("Pedro", "curti", "pedro@curti.com", false, "en", false);
        user.setId(10L);
        user = em.merge(user);
        em.flush();
        Community community = new Community("test", "This is a test community", null, "falsepub", "falsedeveloper", EXISTING_TIME);
        community.setId(10L);
        community = em.merge(community);
        em.flush();
        Mod mod = new Mod(user,community, NOW);
        modDao.removeModder(mod);
        Assert.assertEquals(1,((Number) em.createNativeQuery("SELECT count(*) FROM modders").getSingleResult()).intValue());


    }

}


import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.persistance.CommunityDaoJpa;
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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
public class CommunityDaoTest {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private CommunityDaoJpa communityDao;
    private static final LocalDateTime NOW = LocalDateTime.now();

    private static final LocalDateTime EXISTING_TIME = LocalDateTime.of(2024, 5, 5, 20, 30);

    @Test
    @Rollback
    public void testCreateCommunity() {
        Community community = communityDao.createCommunity("testcreate", "This is a test community", "falsedeveloper", "falsepub", EXISTING_TIME);
        em.flush();
        Assert.assertNotNull(community);
        Assert.assertEquals(4, ((Number) em.createNativeQuery("SELECT count(*) FROM community").getSingleResult()).intValue());
    }

    @Test
    public void testFindCommunityById() {
        Community community = communityDao.findById(10L).get();
        Assert.assertNotNull(community);
        Assert.assertEquals("test", community.getName());
    }

    @Test
    public void testFindCommunityByName() {
        Community community = communityDao.findByName("test").get();
        Assert.assertNotNull(community);
        Assert.assertEquals(10L, community.getId().longValue());
        Assert.assertEquals("This is a test community", community.getDescription());
        Assert.assertEquals("falsedeveloper", community.getDeveloper());
        Assert.assertEquals("falsepub", community.getPublisher());
    }

    @Test
    public void testFindAllCommunities(){
        Assert.assertEquals(3, communityDao.findAllCommunities().size());
    }

//    @Test
//    @Rollback
//    public void testUpdateRating(){
//        Community community = new Community("test", "This is a test community", null, "falsepub", "falsedeveloper", EXISTING_TIME);
//        community.setId(10L);
//        community = em.merge(community);
//        em.flush();
//        community = communityDao.updateRating(community, 5,1);
//        community = em.merge(community);
//        Assert.assertEquals(1, community.getRatingCount().intValue());
//
//    }

}

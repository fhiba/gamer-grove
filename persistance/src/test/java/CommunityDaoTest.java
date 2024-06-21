
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.CommunityCategories;
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

    @Test
    @Rollback
    public void testUpdateRating(){
        Community community = new Community("test", "This is a test community", null, "falsepub", "falsedeveloper", EXISTING_TIME);
        community.setId(10L);
        community.setRatingCount(0);
        community.setTotalRating(0F);
        community = communityDao.updateRating(community, 5,1);
        Assert.assertEquals(1, community.getRatingCount().intValue());
        Assert.assertEquals(5.0F,community.getTotalRating().floatValue(),0.1);

    }

    @Test
    @Rollback
    public void testAddCategory(){
        Boolean added = communityDao.addCategory(10L, "Action");
        Assert.assertTrue(added);
        Assert.assertEquals(5, ((Number) em.createNativeQuery("SELECT count(*) FROM communities_categories").getSingleResult()).intValue());
    }

    @Test
    @Rollback
    public void testRemoveCategory(){
        Boolean added = communityDao.removeCategory(10L, "RPG");
        Assert.assertTrue(added);
        Assert.assertEquals(3, ((Number) em.createNativeQuery("SELECT count(*) FROM communities_categories").getSingleResult()).intValue());
    }

    @Test
    public void testCheckIfUserFollowsCommunity(){
        Assert.assertTrue(communityDao.checkIfUserFollowsCommunity(10L, 10));
    }

    @Test
    @Rollback
    public void testUnfollowCommunity(){
        communityDao.unfollowCommunity(10L, 10);
        Assert.assertEquals(0, ((Number) em.createNativeQuery("SELECT count(*) FROM community_user").getSingleResult()).intValue());
    }

    @Test
    @Rollback
    public void testFollowCommunity(){
        communityDao.followCommunity(10L, 11,"other");
        Assert.assertEquals(2, ((Number) em.createNativeQuery("SELECT count(*) FROM community_user").getSingleResult()).intValue());
    }

    @Test
    @Rollback
    public void testUpdateCommunityImageId(){
        communityDao.updateCommunityImageId(10L, 10L);
        Assert.assertEquals(10L, ((Number) em.createNativeQuery("SELECT portrait_id FROM community WHERE id = 10").getSingleResult()).longValue());
    }



}

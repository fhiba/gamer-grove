
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.CommunityCategories;
import ar.edu.itba.paw.persistance.CommunityDaoJpa;
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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
public class CommunityDaoTest {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private CommunityDaoJpa communityDao;

    private static final String COMMUNITY_NAME = "test";
    private static final String COMMUNITY_DESCRIPTION = "This is a test community";
    private static final String COMMUNITY_PUBLISHER = "falsepub";
    private static final String COMMUNITY_DEVELOPER = "falsedeveloper";

    private static final Long COMMUNITY_ID = 10L;
    private static final LocalDateTime NOW = LocalDateTime.now();

    private static final LocalDateTime EXISTING_TIME = LocalDateTime.of(2024, 5, 5, 20, 30);


    @Autowired
    private DataSource ds;
    private JdbcTemplate jdbcTemplate;

    private static final String TABLE = "community";
    private static final String CC_TABLE = "communities_categories";
    private static final String CU_TABLE = "community_user";
    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
    }
    @Test
    @Rollback
    public void testCreateCommunity() {
        Community community = communityDao.createCommunity("testcreate", "This is a test community", "falsedeveloper", "falsepub", EXISTING_TIME);
        em.flush();
        Assert.assertNotNull(community);
        Assert.assertEquals(4, JdbcTestUtils.countRowsInTable(jdbcTemplate, TABLE));
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
        Community community = new Community(COMMUNITY_NAME,COMMUNITY_DESCRIPTION, null,COMMUNITY_PUBLISHER,COMMUNITY_DEVELOPER, NOW);
        community.setId(COMMUNITY_ID);
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
        Assert.assertEquals(5, JdbcTestUtils.countRowsInTable(jdbcTemplate,CC_TABLE));
    }

    @Test
    @Rollback
    public void testRemoveCategory(){
        Boolean added = communityDao.removeCategory(10L, "RPG");
        Assert.assertTrue(added);
        Assert.assertEquals(3, JdbcTestUtils.countRowsInTable(jdbcTemplate,CC_TABLE));
    }

    @Test
    public void testCheckIfUserFollowsCommunity(){
        Assert.assertTrue(communityDao.checkIfUserFollowsCommunity(10L, 10));
    }

    @Test
    @Rollback
    public void testUnfollowCommunity(){
        communityDao.unfollowCommunity(10L, 10);
        Assert.assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate,CU_TABLE));
    }

    @Test
    @Rollback
    public void testFollowCommunity(){
        communityDao.followCommunity(10L, 11,"other");
        Assert.assertEquals(2,  JdbcTestUtils.countRowsInTable(jdbcTemplate,CU_TABLE));
    }

    @Test
    @Rollback
    public void testUpdateCommunityImageId(){
        communityDao.updateCommunityImageId(10L, 10L);
        Assert.assertEquals(10L, ((Number) em.createNativeQuery("SELECT portrait_id FROM community WHERE id = 10").getSingleResult()).longValue());
    }



}

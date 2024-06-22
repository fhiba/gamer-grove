import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.Rating;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistance.RatingDaoJpa;
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
public class RatingDaoTest {
    @PersistenceContext
    private EntityManager em;

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

    private static final float NEW_FLOAT = 5.0f;

    private static final float DELTA = 0.001f;

    private static final LocalDateTime NOW = LocalDateTime.now();



    @Autowired
    private RatingDaoJpa ratingDao;

    private static final String TABLE = "ratings";

    @Autowired
    private DataSource ds;
    private JdbcTemplate jdbcTemplate;
    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    @Rollback
    public void testCreateRating() {
        User user = new User("notMod", "curti", "notmod@hotmail.com",false,"en",false);
        user.setId(11L);
        user = em.merge(user);
        Community community = new Community(COMMUNITY_NAME,COMMUNITY_DESCRIPTION, null,COMMUNITY_PUBLISHER,COMMUNITY_DEVELOPER, NOW);
        community.setId(COMMUNITY_ID);
        community = em.merge(community);
        Rating rating = ratingDao.createRating(user, community, NEW_FLOAT);
        em.flush();
        Assert.assertNotNull(rating);
        Assert.assertEquals(2, JdbcTestUtils.countRowsInTable(jdbcTemplate, TABLE));
    }

    @Test
    public void testGetRatingById() {
        User user = new User(USERNAME, PASSWORD, EMAIL,VERIFIED,LOCALE,OWNER);
        user.setId(USER_ID);
        user = em.merge(user);
        Community community = new Community(COMMUNITY_NAME,COMMUNITY_DESCRIPTION, null,COMMUNITY_PUBLISHER,COMMUNITY_DEVELOPER, NOW);
        community.setId(COMMUNITY_ID);
        community = em.merge(community);
        Rating rating = ratingDao.getRatingById(user, community).get();
        Assert.assertEquals(4.5f, rating.getRating().floatValue(), DELTA);
    }

    @Test
    @Rollback
    public void testDeleteRating() {
        User user = new User(USERNAME, PASSWORD, EMAIL,VERIFIED,LOCALE,OWNER);
        user.setId(USER_ID);
        user = em.merge(user);
        Community community = new Community(COMMUNITY_NAME,COMMUNITY_DESCRIPTION, null,COMMUNITY_PUBLISHER,COMMUNITY_DEVELOPER, NOW);
        community.setId(COMMUNITY_ID);
        community = em.merge(community);
        Rating rating = new Rating(user, community, 4.5f);
        rating = em.merge(rating);
        ratingDao.deleteRating(rating);
        em.flush();
        Assert.assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, TABLE));
    }

    @Test
    @Rollback
    public void testUpdateRating() {
        User user = new User(USERNAME, PASSWORD, EMAIL,VERIFIED,LOCALE,OWNER);
        user.setId(USER_ID);
        user = em.merge(user);
        Community community = new Community(COMMUNITY_NAME,COMMUNITY_DESCRIPTION, null,COMMUNITY_PUBLISHER,COMMUNITY_DEVELOPER, NOW);
        community.setId(COMMUNITY_ID);
        community = em.merge(community);
        Rating rating = new Rating(user, community, 4.5f);
        rating = em.merge(rating);
        rating = ratingDao.updateRating(rating, 4.0f);
        Assert.assertNotNull(rating);
        Assert.assertEquals(4.0f, rating.getRating().floatValue(), DELTA);

    }

}

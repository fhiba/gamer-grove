import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.Rating;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistance.RatingDaoJpa;
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
public class RatingDaoTest {
    @PersistenceContext
    private EntityManager em;

    private static final LocalDateTime EXISTING_TIME = LocalDateTime.of(2024, 5, 5, 20, 30);


    @Autowired
    private RatingDaoJpa ratingDao;

    @Test
    @Rollback
    public void testCreateRating() {
        User user = new User("notMod", "curti", "notmod@hotmail.com",false,"en",false);
        user.setId(11L);
        user = em.merge(user);
        Community community = new Community("test", "This is a test community", null, "falsepub", "falsedeveloper", EXISTING_TIME);
        community.setId(10L);
        community = em.merge(community);
        Rating rating = ratingDao.createRating(user, community, 5.0f);
        em.flush();
        Assert.assertNotNull(rating);
        Assert.assertEquals(2, ((Number) em.createNativeQuery("SELECT count(*) FROM ratings").getSingleResult()).intValue());
    }

    @Test
    public void testGetRatingById() {
        User user = new User("Pedro", "curti", "pedro@curti.com", false, "en", false);
        user.setId(10L);
        user = em.merge(user);
        Community community = new Community("test", "This is a test community", null, "falsepub", "falsedeveloper", EXISTING_TIME);
        community.setId(10L);
        community = em.merge(community);
        Rating rating = ratingDao.getRatingById(user, community).get();
        Assert.assertEquals(4.5f, rating.getRating().floatValue(), 0.1f);
    }

    @Test
    @Rollback
    public void testDeleteRating() {
        User user = new User("Pedro", "curti", "pedro@curti.com", false, "en", false);
        user.setId(10L);
        user = em.merge(user);
        Community community = new Community("test", "This is a test community", null, "falsepub", "falsedeveloper", EXISTING_TIME);
        community.setId(10L);
        community = em.merge(community);
        Rating rating = new Rating(user, community, 4.5f);
    }

}

import ar.edu.itba.paw.exceptions.NoSuchPostException;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistance.PostDaoJpa;
import ar.edu.itba.paw.persistance.UserDaoJPA;
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
import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
public class PostDaoTest {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private PostDaoJpa postDao;

    private static final LocalDateTime NOW = LocalDateTime.now();

    private static final LocalDateTime EXISTING_TIME = LocalDateTime.of(2024, 5, 5, 20, 30);

    private static final LocalDateTime POST_TIME = LocalDateTime.of(2020, 1, 1, 0, 00);

//2020-01-01 00:00:00

    @Test
    @Rollback
    public void testCreatePost() {
        User user = new User("Pedro", "curti", "pedro@curti.com", false, "en", false);
        user.setId(10L);
        user = em.merge(user);
        Community community = new Community("test", "This is a test community", null, "falsepub", "falsedeveloper", NOW);
        community.setId(10L);
        community = em.merge(community);
        Post post = postDao.createPost("title", "body", user, community, false, NOW,"Help");
        em.flush();
        Assert.assertNotNull(post);
        Assert.assertEquals(3,((Number) em.createNativeQuery("SELECT count(*) FROM post").getSingleResult()).intValue());

    }

    @Test
    public void testFindById() {
        Post post = postDao.findById(10L).get();
        Assert.assertNotNull(post);
        Assert.assertEquals("First post", post.getTitle());
        Assert.assertEquals("This is my first post", post.getBody());
        Assert.assertEquals("News", post.getCategory());
        Assert.assertEquals(10, post.getAuthor().getId().longValue());
        Assert.assertEquals(false, post.getDeleted());
        Assert.assertEquals(0, post.getGrooviness().longValue());
        Assert.assertEquals("test", post.getcommunity().getName());
    }

    @Test
    public void testFindAllPosts() {
        Assert.assertEquals(2, postDao.findAllPosts().size());
    }

    @Test
    public void testFindPostByCommunity() {
        Assert.assertEquals(2, postDao.findPostsByCommunity("test").size());
    }

    @Test
    public void testFindByCategory(){
        Assert.assertEquals(2, postDao.findByCategory("News").size());
    }

    @Test
    @Rollback
    public void testAddGroovy() {
        User user = new User("notMod", "curti", "notmod@hotmail.com",false,"en",false);
        user.setId(11L);
        user = em.merge(user);
        Community community = new Community("test", "This is a test community", null, "falsepub", "falsedeveloper", NOW);
        community.setId(10L);
        community = em.merge(community);
        Post post = new Post("First post", "This is my first post", user, community,false,null,POST_TIME, 0, false, "News");
        post.setId(11L);
        post = em.merge(post);
        postDao.addToGroovy(user,post, true);
        em.flush();
        Assert.assertEquals(2, ((Number) em.createNativeQuery("SELECT count(*) FROM groovy_post_history").getSingleResult()).intValue());
    }
    @Test
    public void testCheckGrooviness(){
        Optional<Boolean> value = postDao.checkGrooviness(1L, 2L);
        Assert.assertFalse(value.isPresent());
    }

    @Test
    @Rollback
    public void testDeleteGrooviness() {
        postDao.deleteGrooviness(10L,10L);
        Assert.assertEquals(0, ((Number) em.createNativeQuery("SELECT count(*) FROM groovy_post_history").getSingleResult()).intValue());
    }

    @Test
    public void testGetMyFollowedPosts(){
        Assert.assertEquals(2, postDao.getMyFollowedPosts(10L).size());
    }

    @Test
    public void testGetMyFollowedPostsByCategory(){
        Assert.assertEquals(2, postDao.getMyFollowedPostsByCategory("News",10L ).size());
    }

    @Test
    public void testFindPostsByUser(){
        Assert.assertEquals(2, postDao.findPostsByUser(10L).size());
    }
    @Test
    public void testGetUsedCategories(){
        Assert.assertEquals(1, postDao.getUsedCategories().size());
    }

    @Test
    public void testGetTotalPostCount(){
        Assert.assertEquals(2, postDao.getTotalPostCount());
    }

    @Test
    public void testEditGrooviness() {
        User user = new User("Pedro", "curti", "pedro@curti.com", false, "en", false);
        user.setId(10L);
        user = em.merge(user);
        Community community = new Community("test", "This is a test community", null, "falsepub", "falsedeveloper", NOW);
        community.setId(10L);
        community = em.merge(community);
        Post post = new Post("First post", "This is my first post", user, community,false,null,POST_TIME, 0, false, "News");
        post.setId(10L);
        post = em.merge(post);
        postDao.editGrooviness(post.getId(), -1);

    }

    @Test
    public void testEditGroovyHistory(){

    }
}

// falta editgrooviness,updateGroovyHistory, y todos los de paginacion
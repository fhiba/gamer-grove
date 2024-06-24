import ar.edu.itba.paw.exceptions.NoSuchPostException;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.GroovyEnum;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistance.PostDaoJpa;
import ar.edu.itba.paw.persistance.UserDaoJPA;
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

    private static final String POST_TITLE = "First post";

    private static final String POST_BODY = "This is my first post";

    private static final Boolean POST_MEDIA = false;

    private static final Integer POST_GROOVINESS = 0;

    private static  final String POST_CAT = "Help";

    private static final Long POST_ID = 10L;
    private static final Long POST_ALT_ID = 11L;

    private static final Boolean POST_DELETED = false;
    private static final LocalDateTime EXISTING_POST = LocalDateTime.of(2020,1,1,0,0);



    private static final String TABLE = "post";
    private static final String GPH_TABLE = "groovy_post_history";
    @Autowired
    private DataSource ds;
    private JdbcTemplate jdbcTemplate;
    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    @Rollback
    public void testCreatePost() {
        User user = new User(USERNAME, PASSWORD, EMAIL,VERIFIED,LOCALE,OWNER);
        user.setId(USER_ID);
        user = em.merge(user);
        Community community = new Community(COMMUNITY_NAME,COMMUNITY_DESCRIPTION, null,COMMUNITY_PUBLISHER,COMMUNITY_DEVELOPER, NOW);
        community.setId(COMMUNITY_ID);
        community = em.merge(community);
        Post post = postDao.createPost("title", "body", user, community, false, NOW,"Help");
        em.flush();
        Assert.assertNotNull(post);
        Assert.assertEquals(3, JdbcTestUtils.countRowsInTable(jdbcTemplate, TABLE));

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
    public void testFindByCategory(){
        Assert.assertEquals(2, postDao.findByCategory("News").size());
    }

    @Test
    @Rollback
    public void testAddGroovy() {
        User user = new User("notMod", "curti", "notmod@hotmail.com",false,"en",false);
        user.setId(11L);
        user = em.merge(user);
        Community community = new Community(COMMUNITY_NAME,COMMUNITY_DESCRIPTION, null,COMMUNITY_PUBLISHER,COMMUNITY_DEVELOPER, NOW);
        community.setId(COMMUNITY_ID);
        community = em.merge(community);
        Post post = new Post(POST_TITLE,POST_BODY, user, community,POST_MEDIA,null,EXISTING_POST,POST_GROOVINESS, POST_DELETED,POST_CAT);
        post.setId(POST_ALT_ID);
        post = em.merge(post);
        postDao.addToGroovy(user,post, GroovyEnum.UP);
        em.flush();
        Assert.assertEquals(2, JdbcTestUtils.countRowsInTable (jdbcTemplate, GPH_TABLE));
    }
    @Test
    public void testCheckGrooviness(){
        Optional<GroovyEnum> value = postDao.checkGrooviness(1L, 2L);
        Assert.assertFalse(value.isPresent());
    }

    @Test
    @Rollback
    public void testDeleteGrooviness() {
        postDao.deleteGrooviness(10L,10L);
        Assert.assertEquals(0, JdbcTestUtils.countRowsInTable (jdbcTemplate, GPH_TABLE));
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
        User user = new User(USERNAME, PASSWORD, EMAIL,VERIFIED,LOCALE,OWNER);
        user.setId(USER_ID);
        user = em.merge(user);
        Community community = new Community(COMMUNITY_NAME,COMMUNITY_DESCRIPTION, null,COMMUNITY_PUBLISHER,COMMUNITY_DEVELOPER, NOW);
        community.setId(COMMUNITY_ID);
        community = em.merge(community);
        Post post = new Post(POST_TITLE,POST_BODY, user, community,POST_MEDIA,null,EXISTING_POST,POST_GROOVINESS, POST_DELETED,POST_CAT);
        post.setId(POST_ID);
        post = em.merge(post);
        postDao.editGrooviness(post.getId(), GroovyEnum.DOWN);

    }

    @Test
    public void testEditGroovyHistory(){

    }
}


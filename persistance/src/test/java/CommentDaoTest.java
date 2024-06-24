import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.persistance.CommentDaoJpa;
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
@Rollback
public class CommentDaoTest {

    private static final String BODY = "Comment 1";

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

    private static final Boolean POST_DELETED = false;

    private static final Integer COMMENT_GROOVINESS = 0;

    private static final Long COMMENT_ID = 10L;

    private static final Boolean COMMENT_DELETED = false;


    private static final LocalDateTime NOW = LocalDateTime.now();

    private static final LocalDateTime EXISTING_TIME = LocalDateTime.of(2024, 5, 5, 20, 30);

    private static final LocalDateTime EXISTING_POST = LocalDateTime.of(2020,1,1,0,0);




    @Autowired
    CommentDaoJpa commentDao;

    @PersistenceContext
    EntityManager em;

    @Autowired
    private DataSource ds;
    private JdbcTemplate jdbcTemplate;

    private static final String TABLE = "comment";
    private static final String GCH_TABLE = "groovy_comment_history";
    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
    }
    @Test
    @Rollback
    public void testCreateComment(){
        User user = new User(USERNAME, PASSWORD, EMAIL,VERIFIED,LOCALE,OWNER);
        user.setId(USER_ID);
        Community community = new Community(COMMUNITY_NAME,COMMUNITY_DESCRIPTION, null,COMMUNITY_PUBLISHER,COMMUNITY_DEVELOPER, NOW);
        community.setId(COMMUNITY_ID);
        Post post = new Post(POST_TITLE,POST_BODY, user, community,POST_MEDIA,null,EXISTING_POST,POST_GROOVINESS, POST_DELETED,POST_CAT);
        post.setId(POST_ID);
        Comment comment = commentDao.createComment(post, BODY, user, NOW, 10L);
        em.flush();
        Assert.assertNotNull(comment);
        Assert.assertEquals(4, JdbcTestUtils.countRowsInTable(jdbcTemplate, TABLE));

    }

    @Test
    public void testGetPostComments() {
        List<Comment> comments = commentDao.getPostComments(10L);
        Assert.assertEquals(3, comments.size());
    }

    @Test
    public void testGetCommentById() {
        Comment comment = commentDao.getCommentById(10L).get();
        Assert.assertEquals(10L, comment.getId().intValue());
        Assert.assertEquals(10L, comment.getAuthor().getId().intValue());
        Assert.assertEquals(10L, comment.getPost().getId().intValue());
        Assert.assertEquals(BODY, comment.getBody());
    }

    @Test
    @Rollback
    public void testEditGrooviness() {
        User user = new User(USERNAME, PASSWORD, EMAIL,VERIFIED,LOCALE,OWNER);
        user.setId(USER_ID);
        Community community = new Community(COMMUNITY_NAME,COMMUNITY_DESCRIPTION, null,COMMUNITY_PUBLISHER,COMMUNITY_DEVELOPER, NOW);
        community.setId(COMMUNITY_ID);
        Post post = new Post(POST_TITLE,POST_BODY, user, community,POST_MEDIA,null,EXISTING_POST,POST_GROOVINESS, POST_DELETED,POST_CAT);
        post.setId(POST_ID);
        Comment comment = commentDao.createComment(post, BODY, user, NOW, 10L);
        commentDao.editGrooviness(comment, GroovyEnum.UP);
        Assert.assertEquals(1, comment.getGrooviness().intValue());
    }

    @Test
    public void testGetGroovedComment() {
        List<Comment> comments = commentDao.getGroovedComments(10L,10L);
        Assert.assertEquals(0, comments.size());
    }

    @Test
    public void testGetDownGroovedComment() {
        List<Comment> comments = commentDao.getDownGroovedComments(10L,10L);
        Assert.assertEquals(0, comments.size());
    }

    @Test
    @Rollback
    public void testInsertGroovinessIntoComment() {
        User user = new User(USERNAME, PASSWORD, EMAIL,VERIFIED,LOCALE,OWNER);
        user.setId(USER_ID);
        user = em.merge(user);
        Community community = new Community(COMMUNITY_NAME,COMMUNITY_DESCRIPTION, null,COMMUNITY_PUBLISHER,COMMUNITY_DEVELOPER, NOW);
        community.setId(COMMUNITY_ID);
        community = em.merge(community);
        Post post = new Post(POST_TITLE,POST_BODY, user, community,POST_MEDIA,null,EXISTING_POST,POST_GROOVINESS, POST_DELETED,POST_CAT);
        post.setId(POST_ID);
        post = em.merge(post);
        Comment comment = new Comment(post,null, user, BODY, EXISTING_TIME,COMMENT_GROOVINESS, COMMENT_DELETED);
        comment.setId(COMMENT_ID);
        comment = em.merge(comment);
        commentDao.insertGroovinessIntoComment(comment, user, post, false);
        em.flush();
        Assert.assertEquals(1, JdbcTestUtils.countRowsInTable (jdbcTemplate, GCH_TABLE));
    }

}

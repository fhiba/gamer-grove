import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistance.CommentDaoJpa;
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
@Rollback
public class CommentDaoTest {

    private static final String BODY = "Comment 1";

    private static final String USERNAME = "username";

    private static final LocalDateTime NOW = LocalDateTime.now();

    private static final LocalDateTime EXISTING_TIME = LocalDateTime.of(2024, 5, 5, 20, 30);

    private static final LocalDateTime EXISTING_POST = LocalDateTime.of(2020,1,1,0,0);

    @Autowired
    CommentDaoJpa commentDao;

    @PersistenceContext
    EntityManager em;

    @Test
    @Rollback
    public void testCreateComment(){
        User user = new User("Pedro", "curti", "pedro@curti.com",false,"en",false);
        user.setId(10L);
        Community community = new Community("name", "description", null, "developer", "publisher", NOW);
        community.setId(10L);
        Post post = new Post("title", "body", user, community,false,1L,NOW, 0, false, "Help");
        post.setId(10L);
        Comment comment = commentDao.createComment(post, BODY, user, NOW, 10L);
        em.flush();
        Assert.assertNotNull(comment);
        Assert.assertEquals(4,((Number) em.createNativeQuery("SELECT count(*) FROM comment").getSingleResult()).intValue());

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
        User user = new User("Pedro", "curti", "pedro@curti.com",false,"en",false);
        user.setId(10L);
        Community community = new Community("name", "description", null, "developer", "publisher", NOW);
        community.setId(10L);
        Post post = new Post("title", "body", user, community,false,1L,NOW, 0, false, "Help");
        post.setId(10L);
        Comment comment = commentDao.createComment(post, BODY, user, NOW, 10L);
        commentDao.editGrooviness(comment, 1);
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
        User user = new User("Pedro", "curti", "pedro@curti.com", false, "en", false);
        user.setId(10L);
        user = em.merge(user);
        Community community = new Community("test", "description", null, "developer", "publisher", EXISTING_POST);
        community.setId(10L);
        community = em.merge(community);
        Post post = new Post("First post", "This is my first post", user, community, false, 1L, EXISTING_POST, 0, false, "Help");
        post.setId(10L);
        post = em.merge(post);
        Comment comment = new Comment(post,null, user, BODY, EXISTING_TIME,0, false);
        comment.setId(10L);
        comment = em.merge(comment);
        commentDao.insertGroovinessIntoComment(comment, user, post, false);
        Assert.assertEquals(1, ((Number) em.createNativeQuery("SELECT count(*) FROM groovy_comment_history").getSingleResult()).intValue());
    }

}

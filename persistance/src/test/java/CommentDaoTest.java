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
}

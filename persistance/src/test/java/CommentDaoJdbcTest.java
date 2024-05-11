import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistance.CommentDao;
import ar.edu.itba.paw.persistance.PostDao;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class CommentDaoJdbcTest {
    private static final String BODY = "body";

    private static final String USERNAME = "username";

    private static final LocalDateTime NOW = LocalDateTime.now();


    private static final int POST_ID = 1;
    private static final int USER_ID = 1;

    @Autowired
    private DataSource ds;

    @Autowired
    private CommentDao commentDao;


    @Before
    public void setup(){
        JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void testCreateComment(){
        final Comment comment = commentDao.createComment(POST_ID,BODY,USERNAME,NOW,USER_ID);
        Assert.assertEquals(4,comment.getId());
        Assert.assertEquals(BODY, comment.getBody());
        Assert.assertEquals(USERNAME, comment.getUsername());
        Assert.assertEquals(POST_ID,comment.getPostId());
        Assert.assertEquals(NOW,comment.getDate());
        Assert.assertEquals(-1,comment.getParentId());
        Assert.assertEquals(0, comment.getGrooviness());
    }

    @Test
    public void testGetPostCommentsPaginated() {
        // Call the method to be tested
        for (int i = 0; i < 3; i++) {
            List<Comment> result = commentDao.getPostCommentsPaginated(POST_ID, 1, i);
            Assert.assertEquals(1, result.size());
            Assert.assertEquals(i+1,result.get(0).getId());
        }
    }

}

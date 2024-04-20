import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.User;
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
public class PostDaoJdbcTest {

    private static final long id = 2;
    private static final String title = "title";
    private static final String body = "This is my first post";
    private static final long author_id = 1;
    private static final String community_name = "test";
    private static final boolean media = false;
    private static final LocalDateTime date = LocalDateTime.now();
    private static final int grooviness = 0;
    private static final String category = "Miscellaneous";

    @Autowired
    private DataSource ds;

    @Autowired
    private PostDao postDao;


    @Before
    public void setup(){
        JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "post");
    }

    @Test
    public void testCreatePost(){
        User user = new User(1,"Pedro", "curti", "pedro@curti.com");
        Community community = new Community(1, "test",  "This is a test community");
        community.setPortrait_id(0);
        final Post post = postDao.createPost(title, body, (int)user.getId(), community.getName(), media, date, category);
        Assert.assertEquals(id, post.getId());
        Assert.assertEquals(title, post.getTitle());
        Assert.assertEquals(body, post.getBody());
        Assert.assertEquals(author_id, post.getAuthor_id());
        Assert.assertEquals(community_name, post.getCommunity_name());
        Assert.assertEquals(media, post.getMedia());
        Assert.assertEquals(date, post.getDate());
        Assert.assertEquals(grooviness, post.getGrooviness());
        Assert.assertEquals(category, post.getCategory());

    }

    @Test
    public void testFindPostsByUser(long id) {
        //TODO: Implement test
    }

    @Test
    public void testFindPostsLikedByUser(long id) {
        //TODO: Implement test
    }



}

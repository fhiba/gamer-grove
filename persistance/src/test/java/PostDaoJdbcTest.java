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
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
    private static final String category = "Discussion";

    @Autowired
    private DataSource ds;

    @Autowired
    private PostDao postDao;


    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);
//        JdbcTestUtils.deleteFromTables(jdbcTemplate, "post");
    }

    @Test
    public void testCreatePost(){
        User user = new User(1,"Pedro", "curti", "pedro@curti.com",0,false);
        Community community = new Community(1, "test",  "This is a test community","falsedeveloper", "falsepub", LocalDateTime.now());
        community.setPortrait_id(0);
        final Post post = postDao.createPost(title, body, (int) user.getId(), community.getName(), media, date, category);
        assertEquals(id, post.getId());
        assertEquals(title, post.getTitle());
        assertEquals(body, post.getBody());
        assertEquals(author_id, post.getAuthorId());
        assertEquals(community_name, post.getCommunityName());
        assertEquals(media, post.getMedia());
        assertEquals(date, post.getDate());
        assertEquals(grooviness, post.getGrooviness());
        assertEquals(category, post.getCategory());

    }

    @Test
    public void testFindPostsByUser() {
        // When
        List<Post> posts = postDao.findPostsByUser(1);

        // Then
        // Assert that the size of the list is as expected
        assertEquals(1, posts.size()); // Replace expectedSize with the expected size of the list

        // Assert that the IDs of the returned posts match the IDs of the liked posts
        for (Post postTest : posts) {
            assertEquals(postTest.getId(), 1);
        }

    }

    @Test
    public void testFindPostsLikedByUser() {

        // When
        List<Post> posts = postDao.findPostsLikedByUser(1);

        // Then
        // Assert that the size of the list is as expected
        assertEquals(1, posts.size()); // Replace expectedSize with the expected size of the list

        // Assert that the IDs of the returned posts match the IDs of the liked posts
        for (Post postTest : posts) {
            assertEquals(postTest.getId(), 1);
        }
    }


}

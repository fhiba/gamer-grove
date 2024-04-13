import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistance.CommentDao;
import ar.edu.itba.paw.persistance.UserDao;
import ar.edu.itba.paw.services.*;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

public class CommentServiceTest {

    @Mock
    private CommentDao commentDao;

    @Mock
    private UserService mockUserService;

    @InjectMocks
    private CommentServiceImpl commentService;

    @Test
    public void testCreateComment() {
        // Mocking user service to return a dummy user
        when(mockUserService.getLoggedUser()).thenReturn(Optional.of(new User(1,"username", "password", "email")));


        // Mocking commentDao's createComment method
        when(commentDao.createComment(anyLong(), anyString(), anyString(), any(LocalDateTime.class), anyLong()))
                .thenReturn(new Comment(1L, 1L, "testUser", -1, "Test comment", LocalDateTime.now(), 0));

        // Call the method to be tested
        Comment createdComment = commentService.createComment(1, "Test comment");

        assertNotNull(createdComment);
//        Assert.assertEquals(USERNAME, maybeUser.getUsername());
//        Assert.assertEquals(PASSWORD, maybeUser.getPassword());
//        Assert.assertEquals(EMAIL, maybeUser.getEmail());
    }
}

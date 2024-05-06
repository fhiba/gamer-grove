import ar.edu.itba.paw.exceptions.NoLoggedUserException;
import ar.edu.itba.paw.exceptions.NoSuchCommunityException;
import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistance.CommentDao;
import ar.edu.itba.paw.persistance.UserDao;
import ar.edu.itba.paw.services.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class CommentServiceTest {
    public static final String USERNAME = "username";

    public static final String COMMENT_BODY = "Test comment";
    @Mock
    private CommentDao commentDao;

    @Mock
    private UserService mockUserService;

    @InjectMocks
    private CommentServiceImpl commentService;

    @Test
    public void testCreateComment() throws NoLoggedUserException {
        // Mocking user service to return a dummy user
        when(mockUserService.getLoggedUser()).thenReturn(Optional.of(new User(1,"username", "password", "email",0, false)));

        LocalDateTime now = LocalDateTime.now();
        // Mocking commentDao's createComment method
        when(commentDao.createComment(anyLong(), anyString(), anyString(), any(LocalDateTime.class), anyLong()))
                .thenReturn(new Comment(1L, 1L, "username", -1, COMMENT_BODY, now, 0,false));

        // Call the method to be tested
        Comment createdComment = commentService.createComment(1, "Test comment");

        assertNotNull(createdComment);
        assertEquals(COMMENT_BODY,createdComment.getBody());
        assertEquals(1,createdComment.getId());
        assertEquals(USERNAME,createdComment.getUsername());
        assertEquals(0,createdComment.getGrooviness());
        assertEquals(-1,createdComment.getParentId());
        assertEquals(now,createdComment.getDate());
    }

    @Test(expected = NoLoggedUserException.class)
    public void testFailedCreateWithNoUser() throws NoLoggedUserException {
        //	1.	Setup!
        when(mockUserService.getLoggedUser()).thenReturn(Optional.empty());
        //when(mockDao.createPost(Mockito.eq(TITLE), Mockito.eq(BODY), Mockito.anyInt(), Mockito.eq(COMMUNITY_NAME), Mockito.anyBoolean(), Mockito.any(LocalDateTime.class), Mockito.eq(CATEGORY))).thenReturn(new Post(1, TITLE, BODY, 1, COMMUNITY_NAME, false,0, LocalDateTime.now(), 0,CATEGORY));
        // 	2.	"ejercito"	la	class	under	test

        commentService.createComment(1, "Test comment");

    }

}

import ar.edu.itba.paw.exceptions.NoLoggedUserException;
import ar.edu.itba.paw.exceptions.NoSuchPostException;
import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.pagination.PaginatedDataWrapper;
import ar.edu.itba.paw.models.pagination.PaginationRequest;
import ar.edu.itba.paw.persistance.CommentDao;
import ar.edu.itba.paw.services.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
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

    @Mock
    private MailingService mailingService;

    @Mock
    private PostService mockPostService;

    @InjectMocks
    private CommentServiceImpl commentService;



    @Test
    public void testCreateComment() throws NoLoggedUserException, NoSuchPostException {
        // Mocking user service to return a dummy user
        when(mockUserService.getLoggedUser()).thenReturn(Optional.of(new User(1,"username", "password", "email",0, false)));
        when(mockUserService.findById(anyLong())).thenReturn(Optional.of(new User(1,"username", "password", "email",0, false)));
        when(mockPostService.getPostById(anyLong()))
                .thenReturn(new Post(1, "title", "post body", 1, "communityName", false, 0, LocalDateTime.now(), 0,false,"category"));
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

    @Test
    public void testGetPostCommentsPaginated() {
        // Mock data for testing
        List<Comment> mockComments = Arrays.asList(
                new Comment(1L, 1L, "username", -1, "Comment 1", LocalDateTime.now(), 0, false),
                new Comment(2L, 1L, "username", -1, "Comment 2", LocalDateTime.now(), 0, false),
                new Comment(3L, 1L, "username", -1, "Comment 3", LocalDateTime.now(), 0, false)
        );

        // Mock behavior of commentDao methods
        when(commentDao.getPostCommentsPaginated(1L, 10, 0)).thenReturn(mockComments);
        when(commentDao.getPostCommentsTotalCount(1L)).thenReturn(3);

        // Call the method to be tested
        PaginatedDataWrapper<Comment> result = commentService.getPostCommentsPaginated(1L, new PaginationRequest());

        // Verify the result
        assertEquals(1, result.getPageNumber());
        assertEquals(1, result.getTotalPages());
        assertEquals(3, result.getTotalCount());
        assertEquals(10, result.getPageSize());
        assertEquals(mockComments, result.getData());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalPostIdGetPostCommentsPaginated() {
        commentService.getPostCommentsPaginated(-1,new PaginationRequest());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalPageNumberGetPostCommentsPaginated() {
        commentService.getPostCommentsPaginated(1,new PaginationRequest(-1,1));
    }
    @Test(expected = IllegalArgumentException.class)
    public void testIllegalPageSizeGetPostCommentsPaginated() {
        commentService.getPostCommentsPaginated(1,new PaginationRequest(1,-1));
    }
    @Test(expected = IllegalArgumentException.class)
    public void testIllegalPageNumberMoreThanPageCountGetPostCommentsPaginated() {
        when(commentDao.getPostCommentsTotalCount(eq(1L))).thenReturn(3);
        commentService.getPostCommentsPaginated(1,new PaginationRequest(4,2));
    }

}

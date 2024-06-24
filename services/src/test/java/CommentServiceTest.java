import ar.edu.itba.paw.exceptions.*;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.pagination.PaginatedDataWrapper;
import ar.edu.itba.paw.models.pagination.PaginationRequest;
import ar.edu.itba.paw.persistance.CommentDao;
import ar.edu.itba.paw.services.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class CommentServiceTest {
    public static final Long USER_ID = 1L;
    public static final Long COMMENT_ID = 1L;
    public static final Long COMMUNITY_ID = 1L;
    public static final Long POST_ID = 1L;
    public static final String USERNAME = "username";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String POST_TITLE = "Test post";
    public static final String POST_BODY = "Test post body";
    public static final PostCategories POST_CATEGORY = PostCategories.DISC;
    public static final String COMMUNITY_NAME = "Test community";
    public static final String COMMUNITY_DESCRIPTION = "Test community description";
    public static final String COMMUNITY_PUBLISHER = "Test community publisher";
    public static final String COMMUNITY_DEVELOPER = "Test community developer";
    public static LocalDateTime RELEASE_DATE = LocalDateTime.now();



    public static final String COMMENT_BODY = "Test comment";
    @Mock
    private CommentDao commentDao;

    @Mock
    private UserService mockUserService;

    @Mock
    private MailingService mailingService;

    @Mock
    private PostService mockPostService;

    @Mock
    private GroovyCommentHistoryService mockGroovyCommentHistoryService;

    @InjectMocks
    private CommentServiceImpl commentService;


    @Test
    public void testCreateComment() throws NoLoggedUserException, NoSuchPostException, PostIsDeletedException {
        // SET UP
        final User user = new User(USERNAME, PASSWORD, EMAIL, true, "es", true);
        user.setId(USER_ID);
        final Community community = new Community(COMMUNITY_NAME, COMMUNITY_DESCRIPTION, COMMUNITY_PUBLISHER, COMMUNITY_DEVELOPER, RELEASE_DATE);
        community.setId(COMMUNITY_ID);
        final Post post = new Post(POST_TITLE, POST_BODY, user, community, false, null, LocalDateTime.now(), 0, false, POST_CATEGORY.getCategory());
        post.setId(POST_ID);

        Mockito.when(mockUserService.getLoggedUser()).thenReturn(Optional.of(user));
        Mockito.when(mockPostService.getPostById(POST_ID)).thenReturn(post);
        Mockito.when(commentDao.createComment(eq(post), eq(COMMENT_BODY), eq(user), any(LocalDateTime.class), eq(USER_ID))).thenReturn(new Comment(post, user, COMMENT_BODY, LocalDateTime.now(), 0, false));

        Comment comment = commentService.createComment(POST_ID, COMMENT_BODY);

        Assert.assertEquals(COMMENT_BODY, comment.getBody());
        Assert.assertEquals(user, comment.getAuthor());
        Assert.assertEquals(post, comment.getPost());
        Assert.assertNotNull(comment.getDate());
    }

    @Test(expected = NoLoggedUserException.class)
    public void testCreateCommentNoLoggedUser() throws NoLoggedUserException, NoSuchPostException, PostIsDeletedException {
        // SET UP
        final User user = new User(USERNAME, PASSWORD, EMAIL, true, "es", true);
        user.setId(USER_ID);
        final Community community = new Community(COMMUNITY_NAME, COMMUNITY_DESCRIPTION, COMMUNITY_PUBLISHER, COMMUNITY_DEVELOPER, RELEASE_DATE);
        community.setId(COMMUNITY_ID);
        final Post post = new Post(POST_TITLE, POST_BODY, user, community, false, null, LocalDateTime.now(), 0, false, POST_CATEGORY.getCategory());
        post.setId(POST_ID);

        Mockito.when(mockUserService.getLoggedUser()).thenReturn(Optional.empty());

        Comment comment = commentService.createComment(POST_ID, COMMENT_BODY);

        Assert.assertEquals(COMMENT_BODY, comment.getBody());
        Assert.assertEquals(user, comment.getAuthor());
        Assert.assertEquals(post, comment.getPost());
        Assert.assertNotNull(comment.getDate());
    }
    @Test(expected = PostIsDeletedException.class)
    public void testCreateCommentOnDeletedPost() throws NoLoggedUserException, NoSuchPostException, PostIsDeletedException {
        // SET UP
        final User user = new User(USERNAME, PASSWORD, EMAIL, true, "es", true);
        user.setId(USER_ID);
        final Community community = new Community(COMMUNITY_NAME, COMMUNITY_DESCRIPTION, COMMUNITY_PUBLISHER, COMMUNITY_DEVELOPER, RELEASE_DATE);
        community.setId(COMMUNITY_ID);
        final Post post = new Post(POST_TITLE, POST_BODY, user, community, false, null, LocalDateTime.now(), 0, true, POST_CATEGORY.getCategory());
        post.setId(POST_ID);

        Mockito.when(mockUserService.getLoggedUser()).thenReturn(Optional.of(user));
        Mockito.when(mockPostService.getPostById(POST_ID)).thenReturn(post);

        Comment comment = commentService.createComment(POST_ID, COMMENT_BODY);

        Assert.assertEquals(COMMENT_BODY, comment.getBody());
        Assert.assertEquals(user, comment.getAuthor());
        Assert.assertEquals(post, comment.getPost());
        Assert.assertNotNull(comment.getDate());
    }

    @Test
    public void getPostCommentsPaginated() {
        PaginationRequest request = new PaginationRequest(1, 10);
        Mockito.when(commentDao.getPostCommentsTotalCount(eq(POST_ID))).thenReturn(10);
        Mockito.when(commentDao.getPostCommentsPaginated(eq(POST_ID), eq(10), eq(0))).thenReturn(Arrays.asList(new Comment(), new Comment(), new Comment(), new Comment(), new Comment(), new Comment(), new Comment(), new Comment(), new Comment(), new Comment()));
        PaginatedDataWrapper<Comment> comments = commentService.getPostCommentsPaginated(POST_ID, request);
        assertNotNull(comments);
        assertEquals(10, comments.getTotalCount());
    }

    @Test(expected = IllegalArgumentException.class)
    public void getPostCommentsPaginatedWithBadPageSize() {
        PaginationRequest request = new PaginationRequest(1,-3);
        PaginatedDataWrapper<Comment> comments = commentService.getPostCommentsPaginated(POST_ID, request);
        assertNotNull(comments);
        assertEquals(10, comments.getTotalCount());
    }

    @Test(expected = IllegalArgumentException.class)
    public void getPostCommentsPaginatedWithBadPostId(){
        PaginationRequest request = new PaginationRequest(1,10);
        PaginatedDataWrapper<Comment> comments = commentService.getPostCommentsPaginated(0, request);
        assertNotNull(comments);
        assertEquals(10, comments.getTotalCount());
    }

    @Test(expected = IllegalArgumentException.class)
    public void getPostCommentsPaginatedWithBadPageNumber(){
        PaginationRequest request = new PaginationRequest(0,10);
        Mockito.when(commentDao.getPostCommentsTotalCount(eq(POST_ID))).thenReturn(10);
        PaginatedDataWrapper<Comment> comments = commentService.getPostCommentsPaginated(POST_ID, request);
        assertNotNull(comments);
        assertEquals(10, comments.getTotalCount());
    }

    @Test(expected = IllegalArgumentException.class)
    public void getPostCommentsPaginatedWithBadExceedingPageNumber(){
        PaginationRequest request = new PaginationRequest(0,10);
        Mockito.when(commentDao.getPostCommentsTotalCount(eq(POST_ID))).thenReturn(10);
        PaginatedDataWrapper<Comment> comments = commentService.getPostCommentsPaginated(POST_ID, request);
        assertNotNull(comments);
        assertEquals(10, comments.getTotalCount());
    }

    @Test
    public void testEditGroovinessOnComment() throws NoSuchPostException, NoLoggedUserException, NoSuchCommentException {
        final User user = new User(USERNAME, PASSWORD, EMAIL, true, "es", true);
        user.setId(USER_ID);
        final Community community = new Community(COMMUNITY_NAME, COMMUNITY_DESCRIPTION, COMMUNITY_PUBLISHER, COMMUNITY_DEVELOPER, RELEASE_DATE);
        community.setId(COMMUNITY_ID);
        final Post post = new Post(POST_TITLE, POST_BODY, user, community, false, null, LocalDateTime.now(), 0, true, POST_CATEGORY.getCategory());
        post.setId(POST_ID);
        final Comment comment = new Comment(post, user, COMMENT_BODY, LocalDateTime.now(), 0, false);
        GroovyCommentHistory gch = new GroovyCommentHistory(user, comment, post, true);
        Mockito.when(mockUserService.getLoggedUser()).thenReturn(Optional.of(user));
        Mockito.when(commentDao.getCommentById(eq(COMMENT_ID))).thenReturn(Optional.of(comment));
        Mockito.when(mockPostService.getPostById(eq(POST_ID))).thenReturn(post);
        Mockito.when(mockGroovyCommentHistoryService.findGroovyCommentHistory(eq(user), eq(comment), eq(post))).thenReturn(Optional.empty());
        commentService.editGroovinessOnComment(COMMENT_ID, 1, POST_ID);
    }
    @Test(expected = NoLoggedUserException.class)
    public void testEditGroovinessOnCommentWithNoUser() throws NoSuchPostException, NoLoggedUserException, NoSuchCommentException {
        final User user = new User(USERNAME, PASSWORD, EMAIL, true, "es", true);
        user.setId(USER_ID);
        final Community community = new Community(COMMUNITY_NAME, COMMUNITY_DESCRIPTION, COMMUNITY_PUBLISHER, COMMUNITY_DEVELOPER, RELEASE_DATE);
        community.setId(COMMUNITY_ID);
        final Post post = new Post(POST_TITLE, POST_BODY, user, community, false, null, LocalDateTime.now(), 0, true, POST_CATEGORY.getCategory());
        post.setId(POST_ID);
        final Comment comment = new Comment(post, user, COMMENT_BODY, LocalDateTime.now(), 0, false);
        GroovyCommentHistory gch = new GroovyCommentHistory(user, comment, post, true);
        Mockito.when(mockUserService.getLoggedUser()).thenReturn(Optional.empty());
        Mockito.when(commentDao.getCommentById(eq(COMMENT_ID))).thenReturn(Optional.of(comment));
        commentService.editGroovinessOnComment(COMMENT_ID, 1, POST_ID);
    }

    @Test(expected = NoSuchCommentException.class)
    public void testEditGroovinessOnCommentWithNoComment() throws NoSuchPostException, NoLoggedUserException, NoSuchCommentException {
        final User user = new User(USERNAME, PASSWORD, EMAIL, true, "es", true);
        user.setId(USER_ID);
        final Community community = new Community(COMMUNITY_NAME, COMMUNITY_DESCRIPTION, COMMUNITY_PUBLISHER, COMMUNITY_DEVELOPER, RELEASE_DATE);
        community.setId(COMMUNITY_ID);
        final Post post = new Post(POST_TITLE, POST_BODY, user, community, false, null, LocalDateTime.now(), 0, true, POST_CATEGORY.getCategory());
        post.setId(POST_ID);
        final Comment comment = new Comment(post, user, COMMENT_BODY, LocalDateTime.now(), 0, false);
        GroovyCommentHistory gch = new GroovyCommentHistory(user, comment, post, true);
        Mockito.when(mockUserService.getLoggedUser()).thenReturn(Optional.of(user));
        Mockito.when(commentDao.getCommentById(eq(COMMENT_ID))).thenReturn(Optional.empty());
        commentService.editGroovinessOnComment(COMMENT_ID, 1, POST_ID);
    }
    @Test(expected = IllegalArgumentException.class)
    public void testEditGroovinessOnCommentWithBadGrooviness() throws NoSuchPostException, NoLoggedUserException, NoSuchCommentException {
        final User user = new User(USERNAME, PASSWORD, EMAIL, true, "es", true);
        user.setId(USER_ID);
        final Community community = new Community(COMMUNITY_NAME, COMMUNITY_DESCRIPTION, COMMUNITY_PUBLISHER, COMMUNITY_DEVELOPER, RELEASE_DATE);
        community.setId(COMMUNITY_ID);
        final Post post = new Post(POST_TITLE, POST_BODY, user, community, false, null, LocalDateTime.now(), 0, true, POST_CATEGORY.getCategory());
        post.setId(POST_ID);
        final Comment comment = new Comment(post, user, COMMENT_BODY, LocalDateTime.now(), 0, false);
        GroovyCommentHistory gch = new GroovyCommentHistory(user, comment, post, true);
        Mockito.when(mockUserService.getLoggedUser()).thenReturn(Optional.of(user));
        Mockito.when(commentDao.getCommentById(eq(COMMENT_ID))).thenReturn(Optional.of(comment));
        commentService.editGroovinessOnComment(COMMENT_ID, 3, POST_ID);
    }

    @Test
    public void TestGetUpGroovedComments() throws UserNotFoundException, NoSuchPostException, UserNotFoundException {
        final User user = new User(USERNAME, PASSWORD, EMAIL, true, "es", true);
        user.setId(USER_ID);
        final Community community = new Community(COMMUNITY_NAME, COMMUNITY_DESCRIPTION, COMMUNITY_PUBLISHER, COMMUNITY_DEVELOPER, RELEASE_DATE);
        community.setId(COMMUNITY_ID);
        final Post post = new Post(POST_TITLE, POST_BODY, user, community, false, null, LocalDateTime.now(), 0, true, POST_CATEGORY.getCategory());
        post.setId(POST_ID);
        Mockito.when(mockUserService.getLoggedUser()).thenReturn(Optional.of(user));
        Mockito.when(mockPostService.getPostById(eq(POST_ID))).thenReturn(post);
        List<Comment> comments = Arrays.asList(new Comment(), new Comment(), new Comment(), new Comment(), new Comment(), new Comment(), new Comment(), new Comment(), new Comment(), new Comment());
        Mockito.when(commentDao.getGroovedComments(eq(POST_ID), eq(USER_ID))).thenReturn(comments);
        List<Comment> upGroovedComments = commentService.getUpGroovedComments(POST_ID);
        assertNotNull(upGroovedComments);
        assertEquals(10, upGroovedComments.size());
    }

    @Test(expected = UserNotFoundException.class)
    public void TestGetUpGroovedCommentsWithNoUser() throws UserNotFoundException, NoSuchPostException, UserNotFoundException {
        Mockito.when(mockUserService.getLoggedUser()).thenReturn(Optional.empty());
        List<Comment> comments = Arrays.asList(new Comment(), new Comment(), new Comment(), new Comment(), new Comment(), new Comment(), new Comment(), new Comment(), new Comment(), new Comment());
        List<Comment> upGroovedComments = commentService.getUpGroovedComments(POST_ID);
        assertNotNull(upGroovedComments);
        assertEquals(10, upGroovedComments.size());
    }

    @Test
    public void TestGetDownGroovedComments() throws UserNotFoundException, NoSuchPostException, UserNotFoundException {
        final User user = new User(USERNAME, PASSWORD, EMAIL, true, "es", true);
        user.setId(USER_ID);
        Mockito.when(mockUserService.getLoggedUser()).thenReturn(Optional.of(user));
        List<Comment> comments = Arrays.asList(new Comment(), new Comment(), new Comment(), new Comment(), new Comment(), new Comment(), new Comment(), new Comment(), new Comment(), new Comment());
        Mockito.when(commentDao.getDownGroovedComments(eq(POST_ID), eq(USER_ID))).thenReturn(comments);
        List<Comment> upGroovedComments = commentService.getDownGroovedComments(POST_ID);
        assertNotNull(upGroovedComments);
        assertEquals(10, upGroovedComments.size());
    }

    @Test(expected = UserNotFoundException.class)
    public void TestGetDownGroovedCommentsWithNoUser() throws UserNotFoundException, NoSuchPostException, UserNotFoundException {
        Mockito.when(mockUserService.getLoggedUser()).thenReturn(Optional.empty());
        List<Comment> comments = Arrays.asList(new Comment(), new Comment(), new Comment(), new Comment(), new Comment(), new Comment(), new Comment(), new Comment(), new Comment(), new Comment());
        List<Comment> upGroovedComments = commentService.getDownGroovedComments(POST_ID);
        assertNotNull(upGroovedComments);
        assertEquals(10, upGroovedComments.size());
    }

    @Test
    public void testDeleteComment() throws NoSuchPostException, NoSuchCommentException, PostIsDeletedException {
        final User user = new User(USERNAME, PASSWORD, EMAIL, true, "es", true);
        user.setId(USER_ID);
        final Community community = new Community(COMMUNITY_NAME, COMMUNITY_DESCRIPTION, COMMUNITY_PUBLISHER, COMMUNITY_DEVELOPER, RELEASE_DATE);
        community.setId(COMMUNITY_ID);
        final Post post = new Post(POST_TITLE, POST_BODY, user, community, false, null, LocalDateTime.now(), 0, false, POST_CATEGORY.getCategory());
        post.setId(POST_ID);
        final Comment comment = new Comment(post, user, COMMENT_BODY, LocalDateTime.now(), 0, false);
        Mockito.when(commentDao.getCommentById(eq(COMMENT_ID))).thenReturn(Optional.of(comment));
        Mockito.when(mockPostService.getPostById(eq(POST_ID))).thenReturn(post);
        Mockito.when(commentDao.deleteComment(eq(comment))).thenReturn(1);
        int result = commentService.deleteComment(COMMENT_ID);
        assertTrue(result > 0);
    }

    @Test(expected = NoSuchCommentException.class)
    public void testDeleteCommentWithNotExistingComment() throws NoSuchPostException, NoSuchCommentException, PostIsDeletedException {
        final User user = new User(USERNAME, PASSWORD, EMAIL, true, "es", true);
        user.setId(USER_ID);
        final Community community = new Community(COMMUNITY_NAME, COMMUNITY_DESCRIPTION, COMMUNITY_PUBLISHER, COMMUNITY_DEVELOPER, RELEASE_DATE);
        community.setId(COMMUNITY_ID);
        final Post post = new Post(POST_TITLE, POST_BODY, user, community, false, null, LocalDateTime.now(), 0, false, POST_CATEGORY.getCategory());
        post.setId(POST_ID);
        final Comment comment = new Comment(post, user, COMMENT_BODY, LocalDateTime.now(), 0, false);
        Mockito.when(commentDao.getCommentById(eq(COMMENT_ID))).thenReturn(Optional.empty());
        int result = commentService.deleteComment(COMMENT_ID);
        assertTrue(result > 0);
    }

    @Test(expected = PostIsDeletedException.class)
    public void testDeleteCommentWithDeletedPost() throws NoSuchPostException, NoSuchCommentException, PostIsDeletedException {
        final User user = new User(USERNAME, PASSWORD, EMAIL, true, "es", true);
        user.setId(USER_ID);
        final Community community = new Community(COMMUNITY_NAME, COMMUNITY_DESCRIPTION, COMMUNITY_PUBLISHER, COMMUNITY_DEVELOPER, RELEASE_DATE);
        community.setId(COMMUNITY_ID);
        final Post post = new Post(POST_TITLE, POST_BODY, user, community, false, null, LocalDateTime.now(), 0, true, POST_CATEGORY.getCategory());
        post.setId(POST_ID);
        final Comment comment = new Comment(post, user, COMMENT_BODY, LocalDateTime.now(), 0, false);
        Mockito.when(commentDao.getCommentById(eq(COMMENT_ID))).thenReturn(Optional.of(comment));
        Mockito.when(mockPostService.getPostById(eq(POST_ID))).thenReturn(post);
        int result = commentService.deleteComment(COMMENT_ID);
        assertTrue(result > 0);
    }





}
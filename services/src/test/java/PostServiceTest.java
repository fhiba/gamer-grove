import ar.edu.itba.paw.exceptions.IllegalPageException;
import ar.edu.itba.paw.exceptions.NoLoggedUserException;
import ar.edu.itba.paw.exceptions.NoSuchCommunityException;
import ar.edu.itba.paw.exceptions.NoSuchGroovyPostHistory;
import ar.edu.itba.paw.exceptions.NoSuchPostException;
import ar.edu.itba.paw.exceptions.PageNotFoundException;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.pagination.PaginatedDataWrapper;
import ar.edu.itba.paw.models.pagination.PaginationRequest;
import ar.edu.itba.paw.persistance.PostDao;
import ar.edu.itba.paw.services.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PostServiceTest {

    public static final Long USER_ID = 1L;
    public static final Long COMMUNITY_ID = 1L;
    public static final Long POST_ID = 1L;
    public static final String USERNAME = "username";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String POST_TITLE = "Test post";
    public static final String POST_BODY = "Test post body";
    public static final PostCategories POST_CATEGORY = PostCategories.DISC;
    public static final PostOrders POST_ORDERS = PostOrders.HOTTEST;
    public static final String COMMUNITY_NAME = "Test community";
    public static final String COMMUNITY_DESCRIPTION = "Test community description";
    public static final String COMMUNITY_PUBLISHER = "Test community publisher";
    public static final String COMMUNITY_DEVELOPER = "Test community developer";
    public static LocalDateTime RELEASE_DATE = LocalDateTime.now();
    public static final Integer PAGE_SIZE = 10;
    public static final Integer PAGE_NUMBER = 1;
    @Mock
    public UserService mockUserService;

    @Mock
    public PostDao postDao;

    @Mock
    public MailingService mailingService;

    @Mock
    public CommunityService mockCommunityService;

    @Mock
    public FileService mockFileService;

    @InjectMocks
    public PostServiceImpl postService = new PostServiceImpl();

    @Test
    public void testCreate() throws NoSuchCommunityException, NoLoggedUserException {
        // 1. Setup!
        final User user = new User(USERNAME, PASSWORD, EMAIL, true, "es", true);
        user.setId(USER_ID);
        final Community community = new Community(COMMUNITY_NAME, COMMUNITY_DESCRIPTION, COMMUNITY_PUBLISHER,
                COMMUNITY_DEVELOPER, RELEASE_DATE);
        community.setId(COMMUNITY_ID);
        final Post post = new Post(POST_TITLE, POST_BODY, user, community, false, null, LocalDateTime.now(), 0, false,
                POST_CATEGORY.getCategory());
        post.setId(POST_ID);
        MultipartFile mockMultipartFile = Mockito.mock(MultipartFile.class);
        when(mockUserService.getLoggedUser()).thenReturn(Optional.of(user));
        when(mockCommunityService.findByName(COMMUNITY_NAME)).thenReturn(community);
        when(postDao.createPost(Mockito.eq(POST_TITLE), Mockito.eq(POST_BODY), eq(user), eq(community),
                Mockito.anyBoolean(), Mockito.any(LocalDateTime.class), Mockito.eq(POST_CATEGORY.getCategory())))
                .thenReturn(post);
        // 2. "ejercito" la class under test
        Post result = postService.createPost(POST_TITLE, POST_BODY, COMMUNITY_NAME, POST_CATEGORY.getCategory(),
                Arrays.asList("Hello World".getBytes()));
        // 3. Asserts!
        Assert.assertEquals(POST_TITLE, result.getTitle());
        Assert.assertEquals(POST_BODY, result.getBody());
        Assert.assertEquals(user.getId(), result.getAuthor().getId());
        Assert.assertEquals(COMMUNITY_NAME, post.getCommunityName());
        Assert.assertFalse(result.getMedia());

    }

    @Test(expected = NoLoggedUserException.class)
    public void testFailedCreateWithNoUser() throws NoSuchCommunityException, NoLoggedUserException {
        // 1. Setup!
        when(mockUserService.getLoggedUser()).thenReturn(Optional.empty());
        // 2. "ejercito" la class under test
        postService.createPost(POST_TITLE, POST_BODY, COMMUNITY_NAME, POST_CATEGORY.getCategory(), null);
    }

    @Test
    public void testGetPostsByUser() {
        // Mock data
        final User user = new User(USERNAME, PASSWORD, EMAIL, true, "es", true);
        user.setId(USER_ID);
        final Community community = new Community(COMMUNITY_NAME, COMMUNITY_DESCRIPTION, COMMUNITY_PUBLISHER,
                COMMUNITY_DEVELOPER, RELEASE_DATE);
        community.setId(COMMUNITY_ID);
        final Post post = new Post(POST_TITLE, POST_BODY, user, community, false, null, LocalDateTime.now(), 0, false,
                POST_CATEGORY.getCategory());
        post.setId(POST_ID);
        List<Post> mockPosts = List.of(post);

        // Mock behavior
        when(postDao.findPostsByUser(USER_ID)).thenReturn(mockPosts);

        // Call the method under test
        List<Post> result = postService.getPostsByUser(USER_ID);

        // Verify the result
        assertEquals(mockPosts, result);
    }

    @Test

    public void testGetAllPostsPaginated() throws PageNotFoundException, IllegalPageException {
        // Mock data for testing
        PaginationRequest request = new PaginationRequest(PAGE_NUMBER, PAGE_SIZE);
        final User user = new User(USERNAME, PASSWORD, EMAIL, true, "es", true);
        user.setId(USER_ID);
        final Community community = new Community(COMMUNITY_NAME, COMMUNITY_DESCRIPTION, COMMUNITY_PUBLISHER,
                COMMUNITY_DEVELOPER, RELEASE_DATE);
        community.setId(COMMUNITY_ID);
        final Post post = new Post(POST_TITLE, POST_BODY, user, community, false, null, LocalDateTime.now(), 0, false,
                POST_CATEGORY.getCategory());
        post.setId(POST_ID);
        List<Post> mockPosts = Arrays.asList(
                post, post, post);

        // Mock behavior of postDao methods
        when(postDao.findCount(eq(POST_CATEGORY), eq(null), eq(null), eq(null), eq(null))).thenReturn(mockPosts.size());
        when(postDao.find(eq(PAGE_SIZE), eq((PAGE_NUMBER - 1) * PAGE_SIZE), eq(POST_CATEGORY), eq(POST_ORDERS),
                eq(null), eq(null), eq(null), eq(null))).thenReturn(mockPosts);

        // Call the method to be tested
        PaginatedDataWrapper<Post> result = postService.getAllPostsPaginated(POST_CATEGORY.getCategory(),
                POST_ORDERS.getOrder(), request, null, null, null, null);

        // Verify the result
        assertEquals(PAGE_NUMBER.intValue(), result.getPageNumber());
        assertEquals(1, result.getTotalPages());
        assertEquals(mockPosts.size(), result.getTotalCount());
        assertEquals(PAGE_SIZE.intValue(), result.getPageSize());
        assertEquals(mockPosts, result.getData());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetAllPostsPaginatedInvalidPageSize() throws PageNotFoundException, IllegalPageException {
        PaginationRequest request = new PaginationRequest(1, 0);
        postService.getAllPostsPaginated(null, null, request, null, null, null, null);
    }

    @Test(expected = IllegalPageException.class)
    public void testGetAllPostsPaginatedInvalidPageNumber() throws PageNotFoundException, IllegalPageException {
        PaginationRequest request = new PaginationRequest(0, 10);
        postService.getAllPostsPaginated(null, null, request, null, null, null, null);
    }

    @Test
    public void testEditGroovinessSuccess() throws NoLoggedUserException, NoSuchPostException, NoSuchGroovyPostHistory {
        final User user = new User(USERNAME, PASSWORD, EMAIL, true, "es", true);
        user.setId(USER_ID);
        final Community community = new Community(COMMUNITY_NAME, COMMUNITY_DESCRIPTION, COMMUNITY_PUBLISHER,
                COMMUNITY_DEVELOPER, RELEASE_DATE);
        community.setId(COMMUNITY_ID);
        final Post post = new Post(POST_TITLE, POST_BODY, user, community, false, null, LocalDateTime.now(), 0, false,
                POST_CATEGORY.getCategory());
        post.setId(POST_ID);

        when(postDao.findById(POST_ID)).thenReturn(Optional.of(post));
        when(mockUserService.getLoggedUser()).thenReturn(Optional.of(user));
        postService.editGrooviness(GroovyEnum.UP, POST_ID.intValue());
    }

    @Test(expected = NoSuchPostException.class)
    public void testEditGroovinessPostNotFound()
            throws NoSuchPostException, NoLoggedUserException, NoSuchGroovyPostHistory {
        final User user = new User(USERNAME, PASSWORD, EMAIL, true, "es", true);
        user.setId(USER_ID);
        when(mockUserService.getLoggedUser()).thenReturn(Optional.of(user));
        when(postDao.findById(POST_ID)).thenReturn(Optional.empty());
        postService.editGrooviness(GroovyEnum.UP, POST_ID.intValue());
    }

}

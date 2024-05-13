import ar.edu.itba.paw.exceptions.NoLoggedUserException;
import ar.edu.itba.paw.exceptions.NoSuchCommunityException;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.pagination.PaginatedDataWrapper;
import ar.edu.itba.paw.models.pagination.PaginationRequest;
import ar.edu.itba.paw.persistance.PostDao;
import ar.edu.itba.paw.services.CommunityService;
import ar.edu.itba.paw.services.PostServiceImpl;
import ar.edu.itba.paw.services.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PostServiceTest {

    public static final String TITLE = "title";
    public static final String BODY = "body";
    public static final String COMMUNITY_NAME = "community";
    public static final String CATEGORY = "category";

    @Mock
    public UserService mockUserService;

    @Mock
    public PostDao postDao;

    @Mock
    public CommunityService mockCommunityService;

    @InjectMocks
    public PostServiceImpl postService = new PostServiceImpl();

    @Test
    public void testCreate() throws NoSuchCommunityException, NoLoggedUserException {
        //	1.	Setup!
        Community mockCommunity = new Community(1, COMMUNITY_NAME, "description","falsedeveloper", "falsepub", LocalDateTime.now());
        mockCommunity.setPortrait_id(0);
        when(mockUserService.getLoggedUser()).thenReturn(Optional.of(new User(1,"username", "password", "email",0,false)));
        when(mockUserService.findByCommunity(anyString())).thenReturn(List.of(new User(1,"username", "password", "email",0,false)));
        when(mockCommunityService.findByName(COMMUNITY_NAME)).thenReturn(mockCommunity);
        when(postDao.createPost(Mockito.eq(TITLE), Mockito.eq(BODY), Mockito.anyInt(), Mockito.eq(COMMUNITY_NAME), Mockito.anyBoolean(), Mockito.any(LocalDateTime.class), Mockito.eq(CATEGORY))).thenReturn(new Post(1, TITLE, BODY, 1, COMMUNITY_NAME, false,0, LocalDateTime.now(), 0,false,CATEGORY));
        // 	2.	"ejercito"	la	class	under	test
        Post post = postService.createPost(TITLE, BODY, COMMUNITY_NAME, CATEGORY, new MultipartFile[]{});
        // 	3.	Asserts!
        Assert.assertEquals(TITLE, post.getTitle());
        Assert.assertEquals(BODY, post.getBody());
        Assert.assertEquals(1, post.getAuthorId());
        Assert.assertEquals(COMMUNITY_NAME, post.getCommunityName());
        Assert.assertFalse(post.getMedia());

    }

    @Test(expected = NoLoggedUserException.class)
    public void testFailedCreateWithNoUser() throws NoSuchCommunityException, NoLoggedUserException {
        //	1.	Setup!
        when(mockUserService.getLoggedUser()).thenReturn(Optional.empty());
        Community mockCommunity = new Community(1, COMMUNITY_NAME, "description","falsedeveloper", "falsepub", LocalDateTime.now());
        mockCommunity.setPortrait_id(0);

        // 	2.	"ejercito"	la	class	under	test

        postService.createPost(TITLE, BODY, COMMUNITY_NAME, CATEGORY,null);

    }

    @Test
    public void testGetPostsByUser() {
        // Mock data
        long userId = 1L;
        List<Post> mockPosts = List.of(new Post(1, TITLE, BODY, 1, COMMUNITY_NAME, false, 0, LocalDateTime.now(), 0,false, CATEGORY));

        // Mock behavior
        when(postDao.findPostsByUser(userId)).thenReturn(mockPosts);

        // Call the method under test
        List<Post> result = postService.getPostsByUser(userId);

        // Verify the result
        assertEquals(mockPosts, result);
    }

    @Test
    public void testGetUserLikedPosts() {
        // Mock data
        long userId = 1L;
        List<Post> mockPosts = List.of(new Post(1, TITLE, BODY, 1, COMMUNITY_NAME, false, 0, LocalDateTime.now(), 0,false, CATEGORY));

        // Mock behavior
        when(postDao.findPostsByUser(userId)).thenReturn(mockPosts);

        // Call the method under test
        List<Post> result = postService.getPostsByUser(userId);

        // Verify the result
        assertEquals(mockPosts, result);
    }


    @Test
    public void testGetPostsPaginated() {
        // Mock data for testing
        long userId = 1L;
        int pageSize = 10;
        int pageNumber = 1;
        int offset = 0;
        PaginationRequest request = new PaginationRequest(pageNumber,pageSize);

        List<Post> mockPosts = Arrays.asList(
                new Post(1L, "Test Post 1", "Body of Test Post 1", userId, "Test Community", false, -1L, LocalDateTime.now(), 0, false, "Test Category"),
                new Post(2L, "Test Post 2", "Body of Test Post 2", userId, "Test Community", false, -1L, LocalDateTime.now(), 0, false, "Test Category"),
                new Post(3L, "Test Post 3", "Body of Test Post 3", userId, "Test Community", false, -1L, LocalDateTime.now(), 0, false, "Test Category")
        );

        // Mock behavior of postDao methods
        when(postDao.getTotalPostCount()).thenReturn(mockPosts.size());
        when(postDao.getAllPostsPaginated(pageSize, offset)).thenReturn(mockPosts);

        // Call the method to be tested
        PaginatedDataWrapper<Post> result = postService.getAllPostsPaginated(request);

        // Verify the result
        assertEquals(pageNumber, result.getPageNumber());
        assertEquals(1, result.getTotalPages());
        assertEquals(mockPosts.size(), result.getTotalCount());
        assertEquals(pageSize, result.getPageSize());
        assertEquals(mockPosts, result.getData());
    }

    @Test
    public void testGetPostsByUserPaginated() {
        // Mock data for testing
        long userId = 1L;
        int pageSize = 10;
        int pageNumber = 1;
        int offset = 0;
        PaginationRequest request = new PaginationRequest(pageNumber,pageSize);

        List<Post> mockPosts = Arrays.asList(
                new Post(1L, "Test Post 1", "Body of Test Post 1", userId, "Test Community", false, -1L, LocalDateTime.now(), 0, false, "Test Category"),
                new Post(2L, "Test Post 2", "Body of Test Post 2", userId, "Test Community", false, -1L, LocalDateTime.now(), 0, false, "Test Category"),
                new Post(3L, "Test Post 3", "Body of Test Post 3", userId, "Test Community", false, -1L, LocalDateTime.now(), 0, false, "Test Category")
        );

        // Mock behavior of postDao methods
        when(postDao.getTotalUserLikedPostCount(userId)).thenReturn(mockPosts.size());
        when(postDao.getUserLikedPostPaginated(userId, pageSize, offset)).thenReturn(mockPosts);

        // Call the method to be tested
        PaginatedDataWrapper<Post> result = postService.getUserLikedPostsPaginated(userId, request);

        // Verify the result
        assertEquals(pageNumber, result.getPageNumber());
        assertEquals(1, result.getTotalPages());
        assertEquals(mockPosts.size(), result.getTotalCount());
        assertEquals(pageSize, result.getPageSize());
        assertEquals(mockPosts, result.getData());
    }

    @Test
    public void testGetUserLikedPostPaginated() {
        // Mock data for testing
        long userId = 1L;
        int pageSize = 10;
        int pageNumber = 1;
        int offset = 0;
        PaginationRequest request = new PaginationRequest(pageNumber,pageSize);

        List<Post> mockPosts = Arrays.asList(
                new Post(1L, "Test Post 1", "Body of Test Post 1", userId, "Test Community", false, -1L, LocalDateTime.now(), 0, false, "Test Category"),
                new Post(2L, "Test Post 2", "Body of Test Post 2", userId, "Test Community", false, -1L, LocalDateTime.now(), 0, false, "Test Category"),
                new Post(3L, "Test Post 3", "Body of Test Post 3", userId, "Test Community", false, -1L, LocalDateTime.now(), 0, false, "Test Category")
        );

        // Mock behavior of postDao methods
        when(postDao.getTotalUserLikedPostCount(userId)).thenReturn(mockPosts.size());
        when(postDao.getUserLikedPostPaginated(userId, pageSize, offset)).thenReturn(mockPosts);

        // Call the method to be tested
        PaginatedDataWrapper<Post> result = postService.getUserLikedPostsPaginated(userId, request);

        // Verify the result
        assertEquals(pageNumber, result.getPageNumber());
        assertEquals(1, result.getTotalPages());
        assertEquals(mockPosts.size(), result.getTotalCount());
        assertEquals(pageSize, result.getPageSize());
        assertEquals(mockPosts, result.getData());
    }

    @Test
    public void testGetUserFollowedPostsPaginated() {
        // Mock data for testing
        long userId = 1L;
        int pageSize = 10;
        int pageNumber = 1;
        int offset = 0;
        PaginationRequest request = new PaginationRequest(pageNumber,pageSize);

        List<Post> mockPosts = Arrays.asList(
                new Post(1L, "Test Post 1", "Body of Test Post 1", userId, "Test Community", false, -1L, LocalDateTime.now(), 0, false, "Test Category"),
                new Post(2L, "Test Post 2", "Body of Test Post 2", userId, "Test Community", false, -1L, LocalDateTime.now(), 0, false, "Test Category"),
                new Post(3L, "Test Post 3", "Body of Test Post 3", userId, "Test Community", false, -1L, LocalDateTime.now(), 0, false, "Test Category")
        );

        // Mock behavior of postDao methods
        when(postDao.getTotaltFollowedPostsByUserCount(userId)).thenReturn(mockPosts.size());
        when(postDao.getFollowedPostsByUserPaginated(userId, pageSize, offset)).thenReturn(mockPosts);

        // Call the method to be tested
        PaginatedDataWrapper<Post> result = postService.getUserFollowedPostsPaginated(userId, request);

        // Verify the result
        assertEquals(pageNumber, result.getPageNumber());
        assertEquals(1, result.getTotalPages());
        assertEquals(mockPosts.size(), result.getTotalCount());
        assertEquals(pageSize, result.getPageSize());
        assertEquals(mockPosts, result.getData());
    }


    @Test
    public void testGetUserFollowedPostsByCategoryPaginated() {
        // Mock data for testing
        long userId = 1L;
        String category = "News";
        int pageSize = 10;
        int pageNumber = 1;
        int offset = 0;
        PaginationRequest request = new PaginationRequest(pageNumber,pageSize);

        List<Post> mockPosts = Arrays.asList(
                new Post(1L, "Test Post 1", "Body of Test Post 1", userId, "Test Community", false, -1L, LocalDateTime.now(), 0, false, category),
                new Post(2L, "Test Post 2", "Body of Test Post 2", userId, "Test Community", false, -1L, LocalDateTime.now(), 0, false, category),
                new Post(3L, "Test Post 3", "Body of Test Post 3", userId, "Test Community", false, -1L, LocalDateTime.now(), 0, false, category)
        );

        // Mock behavior of postDao methods
        when(postDao.getTotalUserFollowedPostsByCategoryCount(userId,category)).thenReturn(mockPosts.size());
        when(postDao.getUserFollowedPostsByCategoryPaginated(userId,category, pageSize, offset)).thenReturn(mockPosts);

        // Call the method to be tested
        PaginatedDataWrapper<Post> result = postService.getUserFollowedPostsByCategoryPaginated(category,userId, request);

        // Verify the result
        assertEquals(pageNumber, result.getPageNumber());
        assertEquals(1, result.getTotalPages());
        assertEquals(mockPosts.size(), result.getTotalCount());
        assertEquals(pageSize, result.getPageSize());
        assertEquals(mockPosts, result.getData());
    }

    @Test
    public void testGetPostsByCategoryPaginated() {
        // Mock data for testing
        long userId = 1L;
        String category = "News";
        int pageSize = 10;
        int pageNumber = 1;
        int offset = 0;
        PaginationRequest request = new PaginationRequest(pageNumber,pageSize);

        List<Post> mockPosts = Arrays.asList(
                new Post(1L, "Test Post 1", "Body of Test Post 1", userId, "Test Community", false, -1L, LocalDateTime.now(), 0, false, category),
                new Post(2L, "Test Post 2", "Body of Test Post 2", userId, "Test Community", false, -1L, LocalDateTime.now(), 0, false, category),
                new Post(3L, "Test Post 3", "Body of Test Post 3", userId, "Test Community", false, -1L, LocalDateTime.now(), 0, false, category)
        );

        // Mock behavior of postDao methods
        when(postDao.getTotalPostByCategoryCount(category)).thenReturn(mockPosts.size());
        when(postDao.getAllPostsByCategoryPaginated(category, pageSize, offset)).thenReturn(mockPosts);

        // Call the method to be tested
        PaginatedDataWrapper<Post> result = postService.getPostsByCategoryPaginated(category, request);

        // Verify the result
        assertEquals(pageNumber, result.getPageNumber());
        assertEquals(1, result.getTotalPages());
        assertEquals(mockPosts.size(), result.getTotalCount());
        assertEquals(pageSize, result.getPageSize());
        assertEquals(mockPosts, result.getData());
    }

    @Test
    public void testgGetPostsByCommunityPaginated() {
        // Mock data for testing
        long userId = 1L;
        String category = "News";
        String communty = "Community";
        int pageSize = 10;
        int pageNumber = 1;
        int offset = 0;
        PaginationRequest request = new PaginationRequest(pageNumber,pageSize);

        List<Post> mockPosts = Arrays.asList(
                new Post(1L, "Test Post 1", "Body of Test Post 1", userId, communty, false, -1L, LocalDateTime.now(), 0, false, category),
                new Post(2L, "Test Post 2", "Body of Test Post 2", userId, communty, false, -1L, LocalDateTime.now(), 0, false, category),
                new Post(3L, "Test Post 3", "Body of Test Post 3", userId, communty, false, -1L, LocalDateTime.now(), 0, false, category)
        );

        // Mock behavior of postDao methods
        when(postDao.getTotalPostByCommunityCount(communty)).thenReturn(mockPosts.size());
        when(postDao.getPostsByCommunityPaginated(communty, pageSize, offset)).thenReturn(mockPosts);

        // Call the method to be tested
        PaginatedDataWrapper<Post> result = postService.getPostsByCommunityPaginated(communty, request);

        // Verify the result
        assertEquals(pageNumber, result.getPageNumber());
        assertEquals(1, result.getTotalPages());
        assertEquals(mockPosts.size(), result.getTotalCount());
        assertEquals(pageSize, result.getPageSize());
        assertEquals(mockPosts, result.getData());
    }


}

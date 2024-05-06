import ar.edu.itba.paw.exceptions.NoLoggedUserException;
import ar.edu.itba.paw.exceptions.NoSuchCommunityException;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistance.PostDao;
import ar.edu.itba.paw.services.CommunityService;
import ar.edu.itba.paw.services.PostServiceImpl;
import ar.edu.itba.paw.services.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.MockitoJUnitRunner;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
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

//    @Test
//    public void testCreate() throws NoSuchCommunityException, NoLoggedUserException {
//        //	1.	Setup!
//        Community mockCommunity = new Community(1, COMMUNITY_NAME, "description");
//        mockCommunity.setPortrait_id(0);
//        when(mockUserService.getLoggedUser()).thenReturn(Optional.of(new User(1,"username", "password", "email",0)));
//        when(mockCommunityService.findByName(COMMUNITY_NAME)).thenReturn(mockCommunity);
//
//        // 	2.	"ejercito"	la	class	under	test
//        postService.createPost(TITLE, BODY, COMMUNITY_NAME, CATEGORY,null);
//        // 	3.	Asserts!
//        //no devulve nada todavia
//    }

//    @Test(expected = NoLoggedUserException.class)
//    public void testFailedCreateWithNoUser() throws NoSuchCommunityException, NoLoggedUserException {
//        //	1.	Setup!
//        when(mockUserService.getLoggedUser()).thenReturn(Optional.empty());
//        Community mockCommunity = new Community(1, COMMUNITY_NAME, "description");
//        mockCommunity.setPortrait_id(0);
//        when(mockCommunityService.findByName(COMMUNITY_NAME)).thenReturn(mockCommunity);
//        //when(mockDao.createPost(Mockito.eq(TITLE), Mockito.eq(BODY), Mockito.anyInt(), Mockito.eq(COMMUNITY_NAME), Mockito.anyBoolean(), Mockito.any(LocalDateTime.class), Mockito.eq(CATEGORY))).thenReturn(new Post(1, TITLE, BODY, 1, COMMUNITY_NAME, false,0, LocalDateTime.now(), 0,CATEGORY));
//        // 	2.	"ejercito"	la	class	under	test
//
//        postService.createPost(TITLE, BODY, COMMUNITY_NAME, CATEGORY,null);
//
//    }

    @Test(expected = NoSuchCommunityException.class)
    public void testFailedCreateWithNoCommunity() throws NoSuchCommunityException, NoLoggedUserException {
        //	1.	Setup!
        when(mockUserService.getLoggedUser()).thenReturn(Optional.of(new User(1,"username", "password", "email",0, false)));
        when(mockCommunityService.findByName(COMMUNITY_NAME)).thenReturn(null);

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

}

import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistance.PostDao;
import ar.edu.itba.paw.services.CommunityService;
import ar.edu.itba.paw.services.PostServiceImpl;
import ar.edu.itba.paw.services.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PostServiceTest {

    public static final String TITLE = "title";
    public static final String BODY = "body";
    public static final String COMMUNITY_NAME = "community";
    public static final String CATEGORY = "category";


    @Mock
    public PostDao mockDao;

    @Mock
    public UserService mockUserService;

    @Mock
    public CommunityService mockCommunityService;

    @InjectMocks
    public PostServiceImpl postService = new PostServiceImpl();

    @Test
    public void testCreate() {
        //	1.	Setup!
        when(mockUserService.getLoggedUser()).thenReturn(Optional.of(new User(1,"username", "password", "email")));
        when(mockCommunityService.findByName(COMMUNITY_NAME)).thenReturn(Optional.of(new Community(1, COMMUNITY_NAME, 0,"description")));

        // 	2.	"ejercito"	la	class	under	test
        postService.createPost(TITLE, BODY, COMMUNITY_NAME, CATEGORY);
        // 	3.	Asserts!
        //no devulve nada todavia
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFailedCreateWithNoUser(){
        //	1.	Setup!
        when(mockUserService.getLoggedUser()).thenReturn(Optional.empty());
        when(mockCommunityService.findByName(COMMUNITY_NAME)).thenReturn(Optional.of(new Community(1, COMMUNITY_NAME, 0,"description")));
        //when(mockDao.createPost(Mockito.eq(TITLE), Mockito.eq(BODY), Mockito.anyInt(), Mockito.eq(COMMUNITY_NAME), Mockito.anyBoolean(), Mockito.any(LocalDateTime.class), Mockito.eq(CATEGORY))).thenReturn(new Post(1, TITLE, BODY, 1, COMMUNITY_NAME, false,0, LocalDateTime.now(), 0,CATEGORY));
        // 	2.	"ejercito"	la	class	under	test

        postService.createPost(TITLE, BODY, COMMUNITY_NAME, CATEGORY);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testFailedCreateWithNoCommunity(){
        //	1.	Setup!
        when(mockUserService.getLoggedUser()).thenReturn(Optional.of(new User(1,"username", "password", "email")));
        when(mockCommunityService.findByName(COMMUNITY_NAME)).thenReturn(Optional.empty());

        // 	2.	"ejercito"	la	class	under	test

        postService.createPost(TITLE, BODY, COMMUNITY_NAME, CATEGORY);

    }
}

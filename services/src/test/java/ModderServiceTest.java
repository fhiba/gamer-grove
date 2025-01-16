import ar.edu.itba.paw.exceptions.AlreadyModException;
import ar.edu.itba.paw.exceptions.NoSuchCommunityException;
import ar.edu.itba.paw.exceptions.NoSuchPostException;
import ar.edu.itba.paw.exceptions.UserNotFoundException;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.pagination.PaginatedDataWrapper;
import ar.edu.itba.paw.models.pagination.PaginationRequest;
import ar.edu.itba.paw.persistance.ModderDao;
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
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ModderServiceTest {

    public static final Long USER_ID = 1L;
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

    @Mock
    ModderDao mockModderDao;
    @Mock
    UserService mockUserService;
    @Mock
    PostService mockPostService;

    @Mock
    CommunityService mockCommunityService;

    @Mock
    private MailingService mailingService;

    @InjectMocks
    ModderServiceImpl modderService = new ModderServiceImpl();

    @Test
    public void testAddModderSuccess() throws NoSuchCommunityException, UserNotFoundException, AlreadyModException {
        User user = new User(USERNAME, PASSWORD, EMAIL, true, "es", false);
        user.setId(USER_ID);
        Community community = new Community(COMMUNITY_NAME, COMMUNITY_DESCRIPTION, COMMUNITY_PUBLISHER,
                COMMUNITY_DEVELOPER, RELEASE_DATE);
        community.setId(COMMUNITY_ID);

        Mockito.when(mockUserService.findByUsername(USERNAME)).thenReturn(Optional.of(user));
        Mockito.when(mockCommunityService.findById(COMMUNITY_ID)).thenReturn(community);
        Mockito.when(mockModderDao.addModder(user, community))
                .thenReturn(new Mod(user, community, LocalDateTime.now()));
        Mockito.when(mockModderDao.isModderOfCommunity(user, community)).thenReturn(false);
        boolean result = modderService.addModder(USERNAME, COMMUNITY_ID);

        assertTrue(result);
    }

    @Test(expected = UserNotFoundException.class)
    public void testAddModderUserNotFound()
            throws NoSuchCommunityException, UserNotFoundException, AlreadyModException {
        when(mockUserService.findByUsername(USERNAME)).thenReturn(Optional.empty());

        modderService.addModder(USERNAME, COMMUNITY_ID);
    }

    @Test(expected = NoSuchCommunityException.class)
    public void testAddModderCommunityNotFound()
            throws NoSuchCommunityException, UserNotFoundException, AlreadyModException {
        User user = new User(USERNAME, PASSWORD, EMAIL, true, "es", true);
        user.setId(USER_ID);

        when(mockUserService.findByUsername(USERNAME)).thenReturn(Optional.of(user));
        when(mockCommunityService.findById(COMMUNITY_ID)).thenThrow(new NoSuchCommunityException());

        modderService.addModder(USERNAME, COMMUNITY_ID);
    }

    @Test(expected = AlreadyModException.class)
    public void testAddModderAlreadyMod() throws NoSuchCommunityException, UserNotFoundException, AlreadyModException {
        User user = new User(USERNAME, PASSWORD, EMAIL, true, "es", true);
        user.setId(USER_ID);
        Community community = new Community(COMMUNITY_NAME, COMMUNITY_DESCRIPTION, COMMUNITY_PUBLISHER,
                COMMUNITY_DEVELOPER, RELEASE_DATE);
        community.setId(COMMUNITY_ID);

        Mockito.when(mockUserService.findByUsername(USERNAME)).thenReturn(Optional.of(user));
        Mockito.when(mockCommunityService.findById(COMMUNITY_ID)).thenReturn(community);
        Mockito.when(mockModderDao.isModderOfCommunity(user, community)).thenReturn(true);

        modderService.addModder(USERNAME, COMMUNITY_ID);
    }

    @Test(expected = UserNotFoundException.class)
    public void testAddNonExistingUser() throws NoSuchCommunityException, UserNotFoundException, AlreadyModException {
        when(mockUserService.findByUsername(USERNAME)).thenReturn(Optional.empty());
        modderService.addModder(USERNAME, COMMUNITY_ID);
    }

    @Test
    public void testRemoveModderSuccess() throws NoSuchCommunityException, UserNotFoundException {
        User user = new User(USERNAME, PASSWORD, EMAIL, true, "es", true);
        user.setId(USER_ID);
        Community community = new Community(COMMUNITY_NAME, COMMUNITY_DESCRIPTION, COMMUNITY_PUBLISHER,
                COMMUNITY_DEVELOPER, RELEASE_DATE);
        community.setId(COMMUNITY_ID);
        Mod mod = new Mod(user, community, LocalDateTime.now());

        when(mockUserService.findByUsername(USERNAME)).thenReturn(Optional.of(user));
        when(mockCommunityService.findById(COMMUNITY_ID)).thenReturn(community);
        when(mockModderDao.findByid(user, community)).thenReturn(Optional.of(mod));
        when(mockUserService.findByUsername(USERNAME)).thenReturn(Optional.of(user));
        when(mockModderDao.isModderOfCommunity(user, community)).thenReturn(true);

        boolean result = modderService.removeModder(USERNAME, COMMUNITY_ID);

        assertTrue(result);
    }

    @Test(expected = UserNotFoundException.class)
    public void testRemoveModderUserNotFound() throws NoSuchCommunityException, UserNotFoundException {
        when(mockUserService.findByUsername(USERNAME)).thenReturn(Optional.empty());

        modderService.removeModder(USERNAME, COMMUNITY_ID);
    }

    @Test(expected = NoSuchCommunityException.class)
    public void testRemoveModderCommunityNotFound() throws NoSuchCommunityException, UserNotFoundException {
        User user = new User(USERNAME, PASSWORD, EMAIL, true, "es", true);
        user.setId(USER_ID);

        when(mockUserService.findByUsername(USERNAME)).thenReturn(Optional.of(user));
        when(mockCommunityService.findById(COMMUNITY_ID)).thenThrow(new NoSuchCommunityException());

        modderService.removeModder(USERNAME, COMMUNITY_ID);
    }

    @Test
    public void testRemoveModderNotAMod() throws NoSuchCommunityException, UserNotFoundException {
        User user = new User(USERNAME, PASSWORD, EMAIL, true, "es", true);
        user.setId(USER_ID);
        Community community = new Community(COMMUNITY_NAME, COMMUNITY_DESCRIPTION, COMMUNITY_PUBLISHER,
                COMMUNITY_DEVELOPER, RELEASE_DATE);
        community.setId(COMMUNITY_ID);

        when(mockUserService.findByUsername(USERNAME)).thenReturn(Optional.of(user));
        when(mockCommunityService.findById(COMMUNITY_ID)).thenReturn(community);
        when(mockUserService.findByUsername(USERNAME)).thenReturn(Optional.of(user));
        when(mockModderDao.isModderOfCommunity(user, community)).thenReturn(false);

        boolean result = modderService.removeModder(USERNAME, COMMUNITY_ID);

        assertFalse(result);
    }

    @Test
    public void testGetAllModPaginated() {
        PaginationRequest request = new PaginationRequest(1, 10);
        Mod mod1 = new Mod();
        Mod mod2 = new Mod();
        List<Mod> mods = Arrays.asList(mod1, mod2);

        when(mockModderDao.getAllModdersPaginated(request.getPageSize(), 0)).thenReturn(mods);
        when(mockModderDao.getTotalModders()).thenReturn(mods.size());

        PaginatedDataWrapper<Mod> result = modderService.getAllModPaginated(request);

        assertEquals(1, result.getPageNumber());
        assertEquals(10, result.getPageSize());
        assertEquals(2, result.getTotalCount());
        assertEquals(mods, result.getData());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetAllModPaginatedInvalidPageSize() {
        PaginationRequest request = new PaginationRequest(1, 0);
        modderService.getAllModPaginated(request);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetAllModPaginatedSInvalidPageNumber() {
        PaginationRequest request = new PaginationRequest(0, 10);
        modderService.getAllModPaginated(request);
    }

    @Test
    public void testCanRemovePostAlternativeSuccess() throws NoSuchPostException, NoSuchCommunityException {
        final User user = new User(USERNAME, PASSWORD, EMAIL, true, "es", true);
        user.setId(USER_ID);
        final Community community = new Community(COMMUNITY_NAME, COMMUNITY_DESCRIPTION, COMMUNITY_PUBLISHER,
                COMMUNITY_DEVELOPER, RELEASE_DATE);
        community.setId(COMMUNITY_ID);
        final Post post = new Post(POST_TITLE, POST_BODY, user, community, false, null, LocalDateTime.now(), 0, false,
                POST_CATEGORY.getCategory());
        post.setId(POST_ID);

        when(mockPostService.getPostById(POST_ID)).thenReturn(post);
        when(mockCommunityService.findByName(community.getName())).thenReturn(community);
        when(mockUserService.getLoggedUser()).thenReturn(Optional.of(user));
        when(mockModderDao.isModderOfCommunity(user, community)).thenReturn(true);

        boolean result = modderService.canRemovePostAlternative(POST_ID);

        assertTrue(result);
    }

    @Test
    public void testCanRemovePostAlternativeNoLoggedUser() throws NoSuchPostException, NoSuchCommunityException {
        final User user = new User(USERNAME, PASSWORD, EMAIL, true, "es", true);
        user.setId(USER_ID);
        final Community community = new Community(COMMUNITY_NAME, COMMUNITY_DESCRIPTION, COMMUNITY_PUBLISHER,
                COMMUNITY_DEVELOPER, RELEASE_DATE);
        community.setId(COMMUNITY_ID);
        final Post post = new Post(POST_TITLE, POST_BODY, user, community, false, null, LocalDateTime.now(), 0, false,
                POST_CATEGORY.getCategory());
        post.setId(POST_ID);

        when(mockPostService.getPostById(POST_ID)).thenReturn(post);
        when(mockCommunityService.findByName(community.getName())).thenReturn(community);
        when(mockUserService.getLoggedUser()).thenReturn(Optional.empty());

        boolean result = modderService.canRemovePostAlternative(POST_ID);

        assertFalse(result);
    }

    @Test()
    public void testCanRemovePostAlternativeNoSuchPost() throws NoSuchPostException, NoSuchCommunityException {
        when(mockPostService.getPostById(POST_ID)).thenThrow(new NoSuchPostException());

        Assert.assertFalse(modderService.canRemovePostAlternative(POST_ID));
    }

    @Test()
    public void testCanRemovePostAlternativeNoSuchCommunity() throws NoSuchPostException, NoSuchCommunityException {
        final User user = new User(USERNAME, PASSWORD, EMAIL, true, "es", true);
        user.setId(USER_ID);
        final Community community = new Community(COMMUNITY_NAME, COMMUNITY_DESCRIPTION, COMMUNITY_PUBLISHER,
                COMMUNITY_DEVELOPER, RELEASE_DATE);
        community.setId(COMMUNITY_ID);
        final Post post = new Post(POST_TITLE, POST_BODY, user, community, false, null, LocalDateTime.now(), 0, false,
                POST_CATEGORY.getCategory());
        post.setId(POST_ID);

        when(mockPostService.getPostById(POST_ID)).thenReturn(post);
        when(mockCommunityService.findByName(anyString())).thenThrow(new NoSuchCommunityException());
        Assert.assertFalse(modderService.canRemovePostAlternative(POST_ID));
    }

    @Test
    public void testCanRemovePostAlternativeNotAMod() throws NoSuchPostException, NoSuchCommunityException {
        final User user = new User(USERNAME, PASSWORD, EMAIL, true, "es", false);
        user.setId(USER_ID);
        final Community community = new Community(COMMUNITY_NAME, COMMUNITY_DESCRIPTION, COMMUNITY_PUBLISHER,
                COMMUNITY_DEVELOPER, RELEASE_DATE);
        community.setId(COMMUNITY_ID);
        final Post post = new Post(POST_TITLE, POST_BODY, user, community, false, null, LocalDateTime.now(), 0, false,
                POST_CATEGORY.getCategory());
        post.setId(POST_ID);

        when(mockPostService.getPostById(POST_ID)).thenReturn(post);
        when(mockCommunityService.findByName(community.getName())).thenReturn(community);
        when(mockUserService.getLoggedUser()).thenReturn(Optional.of(user));
        when(mockModderDao.isModderOfCommunity(user, community)).thenReturn(false);

        boolean result = modderService.canRemovePostAlternative(POST_ID);

        assertFalse(result);
    }
}

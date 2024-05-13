import ar.edu.itba.paw.exceptions.AlreadyModException;
import ar.edu.itba.paw.exceptions.NoSuchCommunityException;
import ar.edu.itba.paw.exceptions.NoSuchPostException;
import ar.edu.itba.paw.exceptions.UserNotFoundException;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistance.ModderDao;
import ar.edu.itba.paw.services.CommunityService;
import ar.edu.itba.paw.services.ModderServiceImpl;
import ar.edu.itba.paw.services.PostService;
import ar.edu.itba.paw.services.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ModderServiceTest {

    private static int USER_ID =1;
    private static int COMMUNITY_ID = 1;

    private static final String USERNAME = "username";

    @Mock
    ModderDao mockModderDao;
    @Mock
    UserService mockUserService;
    @Mock
    CommunityService mockCommunityService;
    @Mock
    PostService mockPostService;
    @InjectMocks
    ModderServiceImpl modderService = new ModderServiceImpl();


    @Test
    public void testIsModderOfCommunity() throws NoSuchCommunityException {
        when(mockModderDao.isModderOfCommunity(Mockito.anyLong(), Mockito.anyLong())).thenReturn(true);
        boolean result = modderService.isModderOfCommunity(USER_ID, COMMUNITY_ID);
        assertTrue(result);
    }

    @Test(expected = UserNotFoundException.class)
    public void testAddNonExistingUser() throws NoSuchCommunityException, UserNotFoundException, AlreadyModException {
        when(mockUserService.findByUsername(USERNAME)).thenReturn(Optional.empty());
        modderService.addModder(USERNAME, COMMUNITY_ID);
    }


    @Test
    public void testCanRemovePostAlternative() throws NoSuchCommunityException, NoSuchPostException, UserNotFoundException {
        when(mockModderDao.isModderOfCommunity(Mockito.anyLong(), Mockito.anyLong())).thenReturn(true);
        when(mockCommunityService.findByName(Mockito.anyString())).thenReturn(new Community(1, "name", "description","developer","publisher",LocalDateTime.now()));
        when(mockUserService.getLoggedUser()).thenReturn(Optional.of(new User(1, "username", "password", "email", 0, false)));
        when(mockPostService.getPostById(1L)).thenReturn(new Post(1, "title", "content", 1, "hola", false, 0, LocalDateTime.now(),0,false, "name"));

        boolean result = modderService.canRemovePostAlternative(1);
        assertTrue(result);
    }


    }

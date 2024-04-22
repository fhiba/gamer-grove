import ar.edu.itba.paw.exceptions.AlreadyModException;
import ar.edu.itba.paw.exceptions.NoSuchCommunityException;
import ar.edu.itba.paw.exceptions.UserNotFoundException;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistance.ModderDao;
import ar.edu.itba.paw.services.CommunityService;
import ar.edu.itba.paw.services.ModderServiceImpl;
import ar.edu.itba.paw.services.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
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
    @InjectMocks
    ModderServiceImpl modderService = new ModderServiceImpl();

//    @Test
//    public void testAddModder() throws NoSuchCommunityException, UserNotFoundException, AlreadyModException {
//
//        when(mockUserService.findByUsername(USERNAME)).thenReturn(Optional.of(new User(USER_ID,"username", "password", "email")));
//        when(mockCommunityService.findById(Mockito.anyLong())).thenReturn(new Community(1, "name", "description"));
//        when(mockModderDao.isModderOfCommunity(Mockito.eq(USER_ID), Mockito.anyInt())).thenReturn(false);
//        when(mockModderDao.addModder(Mockito.eq(USER_ID), Mockito.anyInt())).thenReturn(1);
//
//
//        // 2. "ejercito" la class under test
//        int result = modderService.addModder(USERNAME, COMMUNITY_ID);
//
//        // 3. Asserts!
//        assertEquals(1, result);
//    }

    @Test(expected = UserNotFoundException.class)
    public void testAddNonExistingUser() throws NoSuchCommunityException, UserNotFoundException, AlreadyModException {
        when(mockUserService.findByUsername(USERNAME)).thenReturn(Optional.empty());
        modderService.addModder(USERNAME, COMMUNITY_ID);
    }

//    @Test(expected = AlreadyModException.class)
//    public void testAddAlreadyMod() throws NoSuchCommunityException, UserNotFoundException, AlreadyModException {
//        when(mockUserService.findByUsername(USERNAME)).thenReturn(Optional.of(new User(USER_ID, "username", "password", "email")));
//        when(mockCommunityService.findById(Mockito.anyLong())).thenReturn(new Community(1, "name", "description"));
//        when(mockModderDao.isModderOfCommunity(Mockito.eq(USER_ID), Mockito.anyInt())).thenReturn(true);
//
//        int result = modderService.addModder(USERNAME, COMMUNITY_ID);
//    }


    }

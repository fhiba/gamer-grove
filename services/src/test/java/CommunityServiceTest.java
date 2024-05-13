import ar.edu.itba.paw.exceptions.NoSuchCommunityException;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.persistance.CommunityDao;
import ar.edu.itba.paw.services.FileService;
import ar.edu.itba.paw.services.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import ar.edu.itba.paw.services.CommunityServiceImpl;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.Assert.assertEquals;


@RunWith(MockitoJUnitRunner.class)
public class CommunityServiceTest {
    @Mock
    CommunityDao mockDao;
    @Mock
    UserService userService;
    @Mock
    FileService fileService;


    @InjectMocks
    CommunityServiceImpl cs = new CommunityServiceImpl();

    private final long ID = 1;
    private final long PORTRAIT_ID = 1;
    private final String NAME = "name";
    private final String DESCRIPTION = "description";
    private final String PUBLISHER = "publisher";
    private final String DEVELOPER = "developer";
    private final LocalDateTime RELEASE_DATE = LocalDateTime.now();

    @Test
    public void testFindByName() {
        // Setup
        Community mockCom = new Community(ID, NAME, DESCRIPTION, PUBLISHER, DEVELOPER, RELEASE_DATE);
        mockCom.setPortrait_id(PORTRAIT_ID);
        Mockito.when(mockDao.findByName(Mockito.eq(NAME))).thenReturn(Optional.of(mockCom));
        // Exercise
        Community community;
        try {
            community = cs.findByName(NAME);
        } catch (NoSuchCommunityException e) {
            throw new RuntimeException(e);
        }
        // Verify
        assertEquals(ID, community.getId());
        assertEquals(NAME, community.getName());
        assertEquals(DESCRIPTION, community.getDescription());
        assertEquals(PUBLISHER, community.getPublisher());
        assertEquals(DEVELOPER, community.getDeveloper());
        assertEquals(RELEASE_DATE, community.getReleaseDate());
    }

    @Test
    public void testFindById() {
        //Setup
        Community mockCom = new Community(ID, NAME, DESCRIPTION, PUBLISHER, DEVELOPER, RELEASE_DATE);
        mockCom.setPortrait_id(PORTRAIT_ID);
        Mockito.when(mockDao.findById(Mockito.eq(ID))).thenReturn(Optional.of(mockCom));
        // Exercise
        Community community;
        try {
            community = cs.findById(ID);
        } catch (NoSuchCommunityException e) {
            throw new RuntimeException(e);
        }
        // Verify
        assertEquals(ID, community.getId());
        assertEquals(NAME, community.getName());
        assertEquals(DESCRIPTION, community.getDescription());
        assertEquals(PUBLISHER, community.getPublisher());
        assertEquals(DEVELOPER, community.getDeveloper());
        assertEquals(RELEASE_DATE, community.getReleaseDate());
    }

    @Test(expected = NoSuchCommunityException.class)
    public void testFailedFindById() throws NoSuchCommunityException {
        // Setup
        Mockito.when(mockDao.findById(Mockito.eq(ID))).thenReturn(Optional.empty());
        // Exercise
        cs.findById(ID);
    }
}

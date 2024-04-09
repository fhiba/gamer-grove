import ar.edu.itba.paw.exceptions.NoSuchCommunityException;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.persistance.CommunityDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import ar.edu.itba.paw.services.CommunityServiceImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.assertEquals;


@RunWith(MockitoJUnitRunner.class)
public class CommunityServiceTest {
    @Mock
    CommunityDao mockDao;

    @InjectMocks
    CommunityServiceImpl cs = new CommunityServiceImpl();

    private final long ID = 1;
    private final long PORTRAIT_ID = 1;
    private final String NAME = "name";
    private final String DESCRIPTION = "description";


    @Test
    public void testFindByName() {
        // Setup
        Mockito.when(mockDao.findByName(Mockito.eq(NAME))).thenReturn(Optional.of(new Community(ID, NAME, PORTRAIT_ID, DESCRIPTION)));
        // Exercise
        Community community = null;
        try {
            community = cs.findByName(NAME);
        } catch (NoSuchCommunityException e) {
            throw new RuntimeException(e);
        }
        // Verify
        assertEquals(ID, community.getId());
        assertEquals(NAME, community.getName());
        assertEquals(DESCRIPTION, community.getDescription());
    }

    @Test
    public void testFindById() {
        // Setup
        Mockito.when(mockDao.findById(Mockito.eq(ID))).thenReturn(Optional.of(new Community(ID, NAME, PORTRAIT_ID, DESCRIPTION)));
        // Exercise
        Community community = null;
        try {
            community = cs.findById(ID);
        } catch (NoSuchCommunityException e) {
            throw new RuntimeException(e);
        }
        // Verify
        assertEquals(ID, community.getId());
        assertEquals(NAME, community.getName());
        assertEquals(DESCRIPTION, community.getDescription());
    }

    @Test(expected = NoSuchCommunityException.class)

    public void testFailedFindById() throws NoSuchCommunityException {
        // Setup
        Mockito.when(mockDao.findById(Mockito.eq(ID))).thenReturn(Optional.empty());
        // Exercise
        cs.findById(ID);
    }


    @Test
    public void find() {
        // Setup
        Mockito.when(mockDao.find(Mockito.eq(NAME))).thenReturn(new ArrayList<>(Arrays.asList(new Community(ID, NAME, PORTRAIT_ID, DESCRIPTION), new Community(ID, NAME + "2", PORTRAIT_ID, DESCRIPTION))));
        // Exercise
        cs.find(NAME);
        // Verify
        assertEquals(2, cs.find(NAME).size());
    }
}

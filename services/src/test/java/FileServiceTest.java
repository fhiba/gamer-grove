import ar.edu.itba.paw.exceptions.NoSuchCommunityException;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.File;
import ar.edu.itba.paw.models.PostCategories;
import ar.edu.itba.paw.persistance.FileDao;
import ar.edu.itba.paw.services.CommunityService;
import ar.edu.itba.paw.services.FileServiceImpl;
import ar.edu.itba.paw.services.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@RunWith(MockitoJUnitRunner.class)
public class FileServiceTest {
    @Mock
    private FileDao mockDao;

    @Mock
    private CommunityService mockCommunityService;

    @Mock
    private UserService mockUserService;

    @InjectMocks
    private FileServiceImpl fileService;

    public static final Long COMMUNITY_ID = 1L;
    public static final Long POST_ID = 1L;
    public static final String COMMUNITY_NAME = "Test community";
    public static final String COMMUNITY_DESCRIPTION = "Test community description";
    public static final String COMMUNITY_PUBLISHER = "Test community publisher";
    public static final String COMMUNITY_DEVELOPER = "Test community developer";
    public static LocalDateTime RELEASE_DATE = LocalDateTime.now();

    @Test
    public void testUploadCommunityImageNewImage()
            throws NoSuchCommunityException, IOException, IOException, NoSuchCommunityException {
        Community community = new Community(COMMUNITY_NAME, COMMUNITY_DESCRIPTION, COMMUNITY_PUBLISHER,
                COMMUNITY_DEVELOPER, RELEASE_DATE);
        community.setId(COMMUNITY_ID);
        File file = new File();
        file.setImageId(1L);
        MultipartFile mf = Mockito.mock(MultipartFile.class);
        Mockito.when(mf.getBytes()).thenReturn("Hello, World!".getBytes());

        Mockito.when(mockCommunityService.findByName(COMMUNITY_NAME)).thenReturn(community);
        Mockito.when(mockDao.uploadImage(any())).thenReturn(Optional.of(file));
        Optional<File> result = fileService.uploadCommunityImage(COMMUNITY_NAME, "Hello, World!".getBytes());

        Assert.assertTrue(result.isPresent());
    }

    @Test
    public void testUploadCommunityImageUpdateImage() throws NoSuchCommunityException, IOException {
        Community community = new Community(COMMUNITY_NAME, COMMUNITY_DESCRIPTION, COMMUNITY_PUBLISHER,
                COMMUNITY_DEVELOPER, RELEASE_DATE);
        community.setId(COMMUNITY_ID);
        community.setPortrait(new File());

        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.when(file.getBytes()).thenReturn("Hello, World!".getBytes());

        Mockito.when(mockCommunityService.findByName(COMMUNITY_NAME)).thenReturn(community);
        Mockito.when(mockDao.updateFile(any(), any())).thenReturn(Optional.of(new File()));

        Optional<File> result = fileService.uploadCommunityImage(COMMUNITY_NAME, "Hello, World!".getBytes());

        Assert.assertTrue(result.isPresent());
    }

    @Test(expected = NoSuchCommunityException.class)
    public void testUploadCommunityImageNoSuchCommunity() throws NoSuchCommunityException, IOException {
        MultipartFile file = Mockito.mock(MultipartFile.class);

        Mockito.when(mockCommunityService.findByName(COMMUNITY_NAME))
                .thenThrow(new NoSuchCommunityException());

        fileService.uploadCommunityImage(COMMUNITY_NAME, "Hello, World!".getBytes());
    }

    @Test
    public void testUploadPostImage() throws IOException {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.when(file.getBytes()).thenReturn("Hello, World!".getBytes());

        File image = new File();
        image.setImageId(1L);

        Mockito.when(mockDao.uploadImage(any())).thenReturn(Optional.of(image));

        fileService.uploadPostImage("Hello world!".getBytes(), POST_ID);

    }

    @Test
    public void testUploadPostImageFails() throws IOException {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.when(file.getBytes()).thenReturn("Hello, World!".getBytes());

        Mockito.when(mockDao.uploadImage(any())).thenReturn(Optional.empty());

        fileService.uploadPostImage("Hello world!".getBytes(), POST_ID);
    }

}

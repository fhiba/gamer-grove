import ar.edu.itba.paw.exceptions.NoLoggedUserException;
import ar.edu.itba.paw.exceptions.NoSuchCommunityException;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.pagination.PaginatedDataWrapper;
import ar.edu.itba.paw.models.pagination.PaginationRequest;
import ar.edu.itba.paw.persistance.CommunityDao;
import ar.edu.itba.paw.services.FileService;
import ar.edu.itba.paw.services.RatingService;
import ar.edu.itba.paw.services.UserService;
import javassist.bytecode.analysis.MultiType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import ar.edu.itba.paw.services.CommunityServiceImpl;
import org.springframework.web.multipart.MultipartFile;


import java.time.LocalDateTime;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class CommunityServiceTest {
    @Mock
    CommunityDao mockDao;

    @Mock
    private UserService mockUserService;
    @Mock
    private FileService mockFileService;
    @Mock
    private RatingService mockRatingService;

    @InjectMocks
    CommunityServiceImpl cs = new CommunityServiceImpl();


    public static final Long COMMUNITY_ID = 1L;
    public static final Long USER_ID = 1L;
    public static final String USERNAME = "username";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String COMMUNITY_NAME = "Test community";
    public static final String COMMUNITY_DESCRIPTION = "Test community description";
    public static final String COMMUNITY_PUBLISHER = "Test community publisher";
    public static final String COMMUNITY_DEVELOPER = "Test community developer";
    public static LocalDateTime RELEASE_DATE = LocalDateTime.now();
    public static final Float RATING = 4.0f;
    @Test
    public void testCreateCommunity() throws NoSuchCommunityException {
        Community community = new Community(COMMUNITY_NAME, COMMUNITY_DESCRIPTION, COMMUNITY_PUBLISHER, COMMUNITY_DEVELOPER, RELEASE_DATE);
        community.setId(COMMUNITY_ID);
        MultipartFile mockMultipartFile = Mockito.mock(MultipartFile.class);
        Mockito.when(mockDao.createCommunity(eq(COMMUNITY_NAME), eq(COMMUNITY_DESCRIPTION), eq(COMMUNITY_DEVELOPER), eq(COMMUNITY_PUBLISHER), eq(RELEASE_DATE))).thenReturn(community);
        Optional<Community> result = cs.createCommunity(COMMUNITY_NAME, COMMUNITY_DESCRIPTION, null, COMMUNITY_DEVELOPER, COMMUNITY_PUBLISHER, RELEASE_DATE,mockMultipartFile);
        Assert.assertTrue(result.isPresent());
    }

    @Test
    public void testFindByName() throws NoSuchCommunityException {
        Community community = new Community(COMMUNITY_NAME, COMMUNITY_DESCRIPTION, COMMUNITY_PUBLISHER, COMMUNITY_DEVELOPER, RELEASE_DATE);
        community.setId(COMMUNITY_ID);
        Mockito.when(mockDao.findByName(COMMUNITY_NAME)).thenReturn(Optional.of(community));
        Community result = cs.findByName(COMMUNITY_NAME);
        Assert.assertEquals(COMMUNITY_NAME, result.getName());
    }

    @Test(expected = NoSuchCommunityException.class)
    public void testFindByNameNotFound() throws NoSuchCommunityException {
        Mockito.when(mockDao.findByName(COMMUNITY_NAME)).thenReturn(Optional.empty());
        cs.findByName(COMMUNITY_NAME);
    }

    @Test
    public void testFindById() throws NoSuchCommunityException {
        Community community = new Community(COMMUNITY_NAME, COMMUNITY_DESCRIPTION, COMMUNITY_PUBLISHER, COMMUNITY_DEVELOPER, RELEASE_DATE);
        community.setId(COMMUNITY_ID);
        Mockito.when(mockDao.findById(COMMUNITY_ID)).thenReturn(Optional.of(community));
        Community result = cs.findById(COMMUNITY_ID);
        Assert.assertEquals(COMMUNITY_ID, result.getId());
    }

    @Test(expected = NoSuchCommunityException.class)
    public void testFindByIdNotFound() throws NoSuchCommunityException {
        Mockito.when(mockDao.findById(COMMUNITY_ID)).thenReturn(Optional.empty());
        cs.findById(COMMUNITY_ID);
    }

    @Test
    public void testUpdateRatingWithValidCommunityAndUser() throws NoSuchCommunityException, NoLoggedUserException {
        Community community = new Community(COMMUNITY_NAME, COMMUNITY_DESCRIPTION, COMMUNITY_PUBLISHER, COMMUNITY_DEVELOPER, RELEASE_DATE);
        community.setId(COMMUNITY_ID);
        final User user = new User(USERNAME, PASSWORD, EMAIL, true, "es", true);
        user.setId(USER_ID);
        final Rating rating = new Rating( user, community, RATING);
        when(mockDao.findById(COMMUNITY_ID)).thenReturn(Optional.of(community));
        when(mockUserService.getLoggedUser()).thenReturn(Optional.of(user));
        when(mockRatingService.createRating(any(User.class), any(Community.class), anyFloat())).thenReturn(rating);
        when(mockDao.updateRating(any(Community.class), anyFloat(), anyInt())).thenReturn(community);
        cs.updateRating(COMMUNITY_ID, RATING);
    }

    @Test(expected = NoSuchCommunityException.class)
    public void testUpdateRatingWithInvalidCommunity() throws NoSuchCommunityException, NoLoggedUserException {
        when(mockDao.findById(COMMUNITY_ID)).thenReturn(Optional.empty());

        cs.updateRating(COMMUNITY_ID, RATING);
    }

    @Test(expected = NoLoggedUserException.class)
    public void testUpdateRatingWithNoLoggedUser() throws NoSuchCommunityException, NoLoggedUserException {
        Community community = new Community(COMMUNITY_NAME, COMMUNITY_DESCRIPTION, COMMUNITY_PUBLISHER, COMMUNITY_DEVELOPER, RELEASE_DATE);
        community.setId(COMMUNITY_ID);
        when(mockDao.findById(COMMUNITY_ID)).thenReturn(Optional.of(community));
        when(mockUserService.getLoggedUser()).thenReturn(Optional.empty());

        cs.updateRating(COMMUNITY_ID, RATING);
    }

    @Test
    public void testDiscountRatingWithValidCommunityAndUser() throws NoSuchCommunityException, NoLoggedUserException {
        Community community = new Community(COMMUNITY_NAME, COMMUNITY_DESCRIPTION, COMMUNITY_PUBLISHER, COMMUNITY_DEVELOPER, RELEASE_DATE);
        community.setId(COMMUNITY_ID);
        final User user = new User(USERNAME, PASSWORD, EMAIL, true, "es", true);
        user.setId(USER_ID);
        final Rating rating = new Rating( user, community, RATING);
        when(mockDao.findByName(COMMUNITY_NAME)).thenReturn(Optional.of(community));
        when(mockUserService.getLoggedUser()).thenReturn(Optional.of(user));
        cs.discountRating(COMMUNITY_NAME, RATING);
    }

    @Test(expected = NoSuchCommunityException.class)
    public void testDiscountRatingWithInvalidCommunity() throws NoSuchCommunityException, NoLoggedUserException {
        when(mockDao.findByName(COMMUNITY_NAME)).thenReturn(Optional.empty());

        cs.discountRating(COMMUNITY_NAME, RATING);
    }

    @Test(expected = NoLoggedUserException.class)
    public void testDiscountRatingWithNoLoggedUser() throws NoSuchCommunityException, NoLoggedUserException {
        Community community = new Community(COMMUNITY_NAME, COMMUNITY_DESCRIPTION, COMMUNITY_PUBLISHER, COMMUNITY_DEVELOPER, RELEASE_DATE);
        community.setId(COMMUNITY_ID);
        when(mockDao.findByName(COMMUNITY_NAME)).thenReturn(Optional.of(community));
        when(mockUserService.getLoggedUser()).thenReturn(Optional.empty());

        cs.discountRating(COMMUNITY_NAME, RATING);
    }

    @Test
    public void testFindWithValidRequestAndSearchTermsAndCategories() {
        PaginationRequest request = new PaginationRequest(1, 10);
        String searchTerms = "test";
        List<String> categories = Arrays.asList("category1", "category2");

        List<Community> communities = new ArrayList<>();
        communities.add(new Community("test1", "description1", "publisher1", "developer1", LocalDateTime.now()));
        communities.add(new Community("test2", "description2", "publisher2", "developer2", LocalDateTime.now()));

        when(mockDao.find(anyInt(), anyInt(), anyString(), anyList(), any())).thenReturn(communities);
        when(mockDao.findCount(anyString(), anyList(), any())).thenReturn(communities.size());

        PaginatedDataWrapper<Community> result = cs.find(request, searchTerms, categories);

        Assert.assertEquals(2, result.getData().size());
        Assert.assertEquals(1, result.getPageNumber());
        Assert.assertEquals(2, result.getTotalCount());
        Assert.assertEquals(10, result.getPageSize());

    }

    @Test
    public void testFindWithNoCommunities() {
        PaginationRequest request = new PaginationRequest(1, 10);
        String searchTerms = "test";
        List<String> categories = Arrays.asList("category1", "category2");

        when(mockDao.find(anyInt(), anyInt(), anyString(), anyList(), any())).thenReturn(Collections.emptyList());
        when(mockDao.findCount(anyString(), anyList(), any())).thenReturn(0);

        PaginatedDataWrapper<Community> result = cs.find(request, searchTerms, categories);

        Assert.assertEquals(0, result.getData().size());
        Assert.assertEquals(1, result.getPageNumber());
        Assert.assertEquals(0, result.getTotalCount());
        Assert.assertEquals(10, result.getPageSize());

    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindWithNegativePageNumber() {
        PaginationRequest request = new PaginationRequest(-1, 10);
        String searchTerms = "test";
        List<String> categories = Arrays.asList("category1", "category2");

        cs.find(request, searchTerms, categories);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindWithZeroPageSize() {
        PaginationRequest request = new PaginationRequest(1, 0);
        String searchTerms = "test";
        List<String> categories = Arrays.asList("category1", "category2");

        cs.find(request, searchTerms, categories);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindWithPageNumberExceeded() {
        PaginationRequest request = new PaginationRequest(4, 10);
        String searchTerms = "test";
        List<String> categories = Arrays.asList("category1", "category2");

        List<Community> communities = new ArrayList<>();
        communities.add(new Community("test1", "description1", "publisher1", "developer1", LocalDateTime.now()));
        communities.add(new Community("test2", "description2", "publisher2", "developer2", LocalDateTime.now()));

        when(mockDao.find(anyInt(), anyInt(), anyString(), anyList(), any())).thenReturn(communities);
        when(mockDao.findCount(anyString(), anyList(), any())).thenReturn(communities.size());

        PaginatedDataWrapper<Community> result = cs.find(request, searchTerms, categories);
    }

    @Test
    public void testFindFollowedWithValidRequestAndUser() throws NoLoggedUserException {
        final User user = new User(USERNAME, PASSWORD, EMAIL, true, "es", true);
        user.setId(USER_ID);
        PaginationRequest request = new PaginationRequest(1, 10);
        Mockito.when(mockDao.findCount(null, List.of(), USER_ID)).thenReturn(2);
        List<Community> communities = Arrays.asList(new Community(), new Community());
        Mockito.when(mockDao.find(10, 0, null, List.of(), USER_ID)).thenReturn(communities);
        PaginatedDataWrapper<Community> result = cs.findFollowedCommunities(request, new ArrayList<>(), user);
        Assert.assertEquals(2, result.getTotalCount());

    }

    @Test
    public void testFindFollowedWithNoCommunities() throws NoLoggedUserException {
        final User user = new User(USERNAME, PASSWORD, EMAIL, true, "es", true);
        user.setId(USER_ID);
        PaginationRequest request = new PaginationRequest(1, 10);
        List<String> categories = Arrays.asList("category1", "category2");


        PaginatedDataWrapper<Community> result = cs.findFollowedCommunities(request, categories, user);

        Assert.assertEquals(0, result.getData().size());
        Assert.assertEquals(1, result.getPageNumber());
        Assert.assertEquals(0, result.getTotalCount());
        Assert.assertEquals(10, result.getPageSize());

    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindFollowedWithNegativePageNumber() throws NoLoggedUserException {
        final User user = new User(USERNAME, PASSWORD, EMAIL, true, "es", true);
        user.setId(USER_ID);
        PaginationRequest request = new PaginationRequest(-1, 10);
        List<String> categories = Arrays.asList("category1", "category2");

        cs.findFollowedCommunities(request, categories, user);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindFollowedWithZeroPageSize() throws NoLoggedUserException {
        final User user = new User(USERNAME, PASSWORD, EMAIL, true, "es", true);
        user.setId(USER_ID);
        PaginationRequest request = new PaginationRequest(1, 0);
        List<String> categories = Arrays.asList("category1", "category2");

        cs.findFollowedCommunities(request, categories, user);
    }

    @Test
    public void testAddCategoryWithValidCommunityAndCategory() throws NoSuchCommunityException {
        long id = 1L;
        String category = "category1";

        Community community = new Community(COMMUNITY_NAME, COMMUNITY_DESCRIPTION, COMMUNITY_PUBLISHER, COMMUNITY_DEVELOPER, RELEASE_DATE);
        community.setId(id);

        when(mockDao.findById(id)).thenReturn(Optional.of(community));

        cs.addCategory(id, category);
    }

    @Test(expected = NoSuchCommunityException.class)
    public void testAddCategoryWithInvalidCommunity() throws NoSuchCommunityException {
        long id = 1L;
        String category = "category1";

        when(mockDao.findById(id)).thenReturn(Optional.empty());

        cs.addCategory(id, category);
    }


    @Test
    public void testRemoveCategoryWithValidCommunityAndCategory() throws NoSuchCommunityException {
        long id = 1L;
        String category = "category1";

        Community community = new Community(COMMUNITY_NAME, COMMUNITY_DESCRIPTION, COMMUNITY_PUBLISHER, COMMUNITY_DEVELOPER, RELEASE_DATE);
        community.setId(id);

        when(mockDao.findById(id)).thenReturn(Optional.of(community));

        cs.removeCategory(id, category);
    }

    @Test(expected = NoSuchCommunityException.class)
    public void testRemoveCategoryWithInvalidCommunity() throws NoSuchCommunityException {
        long id = 1L;
        String category = "category1";

        when(mockDao.findById(id)).thenReturn(Optional.empty());

        cs.removeCategory(id, category);
    }

    @Test
    public void testModifyUserOnCommunityUserFollowsCommunity() throws NoLoggedUserException, NoSuchCommunityException {
        int communityId = 1;
        String communityName = "Test Community";

        User user = new User(USERNAME, PASSWORD, EMAIL, true, "es", true);
        user.setId(USER_ID);

        when(mockUserService.getLoggedUser()).thenReturn(Optional.of(user));
        when(mockDao.checkIfUserFollowsCommunity(user.getId(), communityId)).thenReturn(true);

        cs.modifyUserOnCommunity(communityId, communityName);
    }

    @Test
    public void testModifyUserOnCommunityUserDoesNotFollowCommunity() throws NoLoggedUserException, NoSuchCommunityException {
        int communityId = 1;
        String communityName = "Test Community";

        User user = new User(USERNAME, PASSWORD, EMAIL, true, "es", true);
        user.setId(USER_ID);

        when(mockUserService.getLoggedUser()).thenReturn(Optional.of(user));
        when(mockDao.checkIfUserFollowsCommunity(user.getId(), communityId)).thenReturn(false);

        cs.modifyUserOnCommunity(communityId, communityName);
    }

    @Test(expected = NoLoggedUserException.class)
    public void testModifyUserOnCommunityNoLoggedUser() throws NoLoggedUserException, NoSuchCommunityException {
        int communityId = 1;
        String communityName = "Test Community";

        when(mockUserService.getLoggedUser()).thenReturn(Optional.empty());

        cs.modifyUserOnCommunity(communityId, communityName);
    }


    @Test
    public void testEditCommunityInfoWithValidCommunity() throws NoSuchCommunityException {
        MultipartFile mockMultipartFile = Mockito.mock(MultipartFile.class);

        Community community = new Community(COMMUNITY_NAME, COMMUNITY_DESCRIPTION, COMMUNITY_PUBLISHER, COMMUNITY_DEVELOPER, RELEASE_DATE);
        community.setCategory(Arrays.asList(CommunityCategories.Action, CommunityCategories.Adventure));
        community.setId(COMMUNITY_ID);

        when(mockDao.findByName(COMMUNITY_NAME)).thenReturn(Optional.of(community));
        when(mockDao.findById(COMMUNITY_ID)).thenReturn(Optional.of(community));
        cs.editCommunityInfo(COMMUNITY_NAME, COMMUNITY_DESCRIPTION, COMMUNITY_PUBLISHER, COMMUNITY_DEVELOPER, mockMultipartFile, "RPG");
    }

    @Test(expected = NoSuchCommunityException.class)
    public void testEditCommunityInfoWithInvalidCommunity() throws NoSuchCommunityException {
        MultipartFile mockMultipartFile = Mockito.mock(MultipartFile.class);

        when(mockDao.findByName(COMMUNITY_NAME)).thenReturn(Optional.empty());

        cs.editCommunityInfo(COMMUNITY_NAME, COMMUNITY_DESCRIPTION, COMMUNITY_PUBLISHER, COMMUNITY_DEVELOPER, mockMultipartFile, "RPG");
    }

    @Test
    public void testCheckIfUserFollowsCommunityUserFollowsCommunity() throws NoLoggedUserException, NoSuchCommunityException {
        User user = new User(USERNAME, PASSWORD, EMAIL, true, "es", true);
        user.setId(USER_ID);

        when(mockUserService.getLoggedUser()).thenReturn(Optional.of(user));
        when(mockDao.checkIfUserFollowsCommunity(user.getId(), COMMUNITY_ID.intValue())).thenReturn(true);

        Boolean result = cs.checkIfUserFollowsCommunity(COMMUNITY_ID.intValue());

        Assert.assertTrue(result);
    }

    @Test
    public void testCheckIfUserFollowsCommunityUserDoesNotFollowCommunity() throws NoLoggedUserException, NoSuchCommunityException {
        User user = new User(USERNAME, PASSWORD, EMAIL, true, "es", true);
        user.setId(USER_ID);

        when(mockUserService.getLoggedUser()).thenReturn(Optional.of(user));
        when(mockDao.checkIfUserFollowsCommunity(user.getId(), COMMUNITY_ID.intValue())).thenReturn(false);

        Boolean result = cs.checkIfUserFollowsCommunity(COMMUNITY_ID.intValue());

        Assert.assertFalse(result);
    }

    @Test(expected = NoLoggedUserException.class)
    public void testCheckIfUserFollowsCommunityNoLoggedUser() throws NoLoggedUserException, NoSuchCommunityException {
        when(mockUserService.getLoggedUser()).thenReturn(Optional.empty());

        cs.checkIfUserFollowsCommunity(COMMUNITY_ID.intValue());
    }
}

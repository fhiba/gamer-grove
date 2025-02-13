package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.AlreadyFollowedException;
import ar.edu.itba.paw.exceptions.CommunityNotFollowedException;
import ar.edu.itba.paw.exceptions.IllegalPageException;
import ar.edu.itba.paw.exceptions.NoLoggedUserException;
import ar.edu.itba.paw.exceptions.NoSuchCommunityException;
import ar.edu.itba.paw.exceptions.NoSuchRatingException;
import ar.edu.itba.paw.exceptions.NotRatedCommunityException;
import ar.edu.itba.paw.exceptions.PageNotFoundException;
import ar.edu.itba.paw.exceptions.AlreadyRatedCommunityException;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.Rating;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.pagination.PaginatedDataWrapper;
import ar.edu.itba.paw.models.pagination.PaginationRequest;
import ar.edu.itba.paw.persistance.CommunityDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@Transactional(readOnly = true)
@Service
public class CommunityServiceImpl implements CommunityService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommunityServiceImpl.class);

    @Autowired
    private CommunityDao communityDao;
    @Autowired
    private UserService userService;
    @Autowired
    private FileService fileService;
    @Autowired
    private RatingService ratingService;

    @Transactional
    @Override
    public Optional<Community> createCommunity(final String name, final String description, final String categories,
            String developer, String publisher, LocalDateTime releaseDate, byte[] image)
            throws NoSuchCommunityException {
        Community community = communityDao.createCommunity(name, description, developer, publisher, releaseDate);
        if (!Objects.isNull(image) && image.length > 0)
            fileService.uploadCommunityImage(community.getName(), image);
        if (categories != null && !categories.isEmpty()) {
            addCategories(community.getId(), List.of(categories.split(",")));
        }
        Optional<Community> maybeCommunity = Optional.of(community);
        if (maybeCommunity.isEmpty()) {
            LOGGER.atError().setMessage("Failed to create new community").log();
        } else {
            LOGGER.atInfo().setMessage("Community {} created successfully").addArgument(() -> community.getName())
                    .log();
        }
        return maybeCommunity;
    }

    @Override
    public List<Community> getAllCommunities() {
        List<Community> communities = communityDao.findAllCommunities();
        if (communities.isEmpty())
            return Collections.emptyList();
        return communities;
    }

    @Override
    public Community findByName(final String communityName) throws NoSuchCommunityException {
        Optional<Community> possibleCommunity = communityDao
                .findByName(URLDecoder.decode(communityName, StandardCharsets.UTF_8));
        if (possibleCommunity.isEmpty()) {
            LOGGER.atError().setMessage("Community {} not found").addArgument(() -> communityName).log();
            throw new NoSuchCommunityException();
        }
        return possibleCommunity.get();
    }

    @Override
    public Community findById(final long communityId) throws NoSuchCommunityException {
        Optional<Community> maybeCommunity = communityDao.findById(communityId);
        if (maybeCommunity.isEmpty()) {
            LOGGER.atError().setMessage("Communuty with id {} not found").addArgument(() -> communityId).log();
            throw new NoSuchCommunityException();
        }
        return maybeCommunity.get();
    }

    @Override
    @Transactional
    public Rating updateRating(String communityName, float rating)
            throws NoSuchCommunityException, NoLoggedUserException, NotRatedCommunityException, NoSuchRatingException {
        Community community = communityDao.findByName(communityName).orElseThrow(NoSuchCommunityException::new);
        User user = userService.getLoggedUser().orElseThrow(NoLoggedUserException::new);

        Optional<Rating> rate = ratingService.getRatingById(user, community);
        if (rate.isEmpty()) {
            throw new NotRatedCommunityException();
        }

        Float diff = rating - rate.get().getRating();

        community = communityDao.updateRating(community, diff, 0);
        return ratingService.updateRating(user, community, rating);
    }

    @Override
    @Transactional
    public Boolean deleteRating(String communityName)
            throws NoLoggedUserException, NoSuchCommunityException, NotRatedCommunityException {
        Community community = communityDao.findByName(communityName).orElseThrow(NoSuchCommunityException::new);
        User user = userService.getLoggedUser().orElseThrow(NoLoggedUserException::new);

        Rating rating = ratingService.getRatingById(user, community).orElseThrow(NotRatedCommunityException::new);

        communityDao.updateRating(community, -rating.getRating(), -1);
        return ratingService.deleteRating(user, community);
    }

    @Override
    @Transactional(readOnly = true)
    public Rating getRatingFromLoggedUser(String communityName)
            throws NoSuchCommunityException, NoLoggedUserException, NoSuchRatingException {

        Community community = findByName(communityName);
        LOGGER.info("Community found: {}", community.getName());
        User user = userService.getLoggedUser().orElseThrow(NoLoggedUserException::new);
        LOGGER.info("User found: {}", user.getId());
        return ratingService.getRatingById(user, community).orElseThrow(NoSuchRatingException::new);
    }

    @Transactional
    @Override
    public Community discountRating(String communityName, Float rating)
            throws NoSuchCommunityException, NoLoggedUserException {
        Community community = findByName(communityName);
        Optional<User> user = userService.getLoggedUser();
        if (user.isEmpty()) {
            LOGGER.atError().setMessage("Error while trying deleting rating because there is no logged user").log();
            throw new NoLoggedUserException();
        }
        Boolean deleted = ratingService.deleteRating(user.get(), community);
        if (deleted)
            communityDao.updateRating(community, -rating, -1);
        LOGGER.atInfo().setMessage("Rating of community {} updated {}").addArgument(communityName)
                .addArgument(() -> community.getRatingCount()).log();
        return community;
    }

    @Override
    public PaginatedDataWrapper<Community> find(PaginationRequest request, final String searchTerms,
            List<String> categories, final Long userId) throws IllegalPageException, PageNotFoundException {

        if (request.getPageSize() < 1) {
            throw new IllegalArgumentException("Invalid Page size");
        }
        if (request.getPageNumber() < 1) {
            throw new IllegalPageException();
        }
        List<String> newList = null;
        if (!categories.isEmpty() && !categories.getFirst().isEmpty()) {
            newList = categories.stream().map(category -> category.replaceAll("([%_\\\\])", "\\\\$1")).toList();
        }
        int totalCount = communityDao.findCount(searchTerms.replaceAll("([%_\\\\])", "\\\\$1"),
                newList == null ? List.of() : newList, (userId == null || userId == 0) ? null : userId);

        int offset = (request.getPageNumber() - 1) * request.getPageSize();

        List<Community> data = communityDao.find(request.getPageSize(), offset,
                searchTerms.replaceAll("([%_\\\\])", "\\\\$1"), newList == null ? List.of() : newList, userId);
        PaginatedDataWrapper<Community> dataWrapper = new PaginatedDataWrapper<>(data, request.getPageNumber(),
                totalCount, request.getPageSize());
        if (request.getPageNumber() > dataWrapper.getTotalPages() && dataWrapper.getTotalPages() != 0) {
            throw new PageNotFoundException();
        }
        return dataWrapper;
    }

    @Override
    public PaginatedDataWrapper<Community> findFollowedCommunities(PaginationRequest request, List<String> categories,
            User user) throws NoLoggedUserException, PageNotFoundException, IllegalPageException {
        if (request.getPageSize() < 1) {
            throw new IllegalArgumentException("Invalid Page size");
        }
        if (request.getPageNumber() < 1) {
            throw new IllegalPageException();
        }

        List<String> newList = null;
        if (!categories.isEmpty() && !categories.getFirst().isEmpty()) {
            newList = categories.stream().map(category -> category.replaceAll("([%_\\\\])", "\\\\$1")).toList();
        }
        int totalCount = communityDao.findCount(null, newList == null ? List.of() : newList, user.getId());
        int offset = (request.getPageNumber() - 1) * request.getPageSize();

        List<Community> data = communityDao.find(request.getPageSize(), offset, null,
                newList == null ? List.of() : newList, user.getId());
        PaginatedDataWrapper<Community> dataWrapper = new PaginatedDataWrapper<>(data, request.getPageNumber(),
                totalCount, request.getPageSize());
        if (request.getPageNumber() > dataWrapper.getTotalPages() && dataWrapper.getTotalPages() != 0) {
            throw new PageNotFoundException();
        }
        return dataWrapper;
    }

    @Transactional
    @Override
    public void addCategory(long id, String category) throws NoSuchCommunityException {
        Optional<Community> community = communityDao.findById(id);
        if (community.isEmpty())
            throw new NoSuchCommunityException();
        communityDao.addCategory(community.get().getId(), category);
        LOGGER.atInfo().setMessage("Category {} added successfully to community {}").addArgument(category)
                .addArgument(id).log();
    }

    @Transactional
    @Override
    public void removeCategory(long id, String category) throws NoSuchCommunityException {
        Optional<Community> community = communityDao.findById(id);
        if (community.isEmpty())
            throw new NoSuchCommunityException();
        communityDao.removeCategory(community.get(), category);
        LOGGER.atInfo().setMessage("Category {} removed successfully to community {}").addArgument(category)
                .addArgument(id).log();
    }

    @Transactional
    @Override
    public void addCategories(long id, List<String> categories) throws NoSuchCommunityException {
        for (String category : categories) {
            addCategory(id, category);
        }
    }

    @Transactional
    @Override
    public Boolean unfollowCommunity(String communityName)
            throws NoSuchCommunityException, NoLoggedUserException, CommunityNotFollowedException {
        User user = userService.getLoggedUser().orElseThrow(NoLoggedUserException::new);
        Community community = findByName(communityName);
        if (!communityDao.checkIfUserFollowsCommunity(user.getId(), community.getId())) {
            LOGGER.atError().setMessage("User {} does not follow community {}").addArgument(() -> user.getUsername())
                    .addArgument(communityName).log();
            throw new CommunityNotFollowedException();
        }
        communityDao.unfollowCommunity(user.getId(), community.getId());
        return true;
    }

    @Transactional
    @Override
    public Boolean followCommunity(String communityName)
            throws NoLoggedUserException, NoSuchCommunityException, AlreadyFollowedException {
        User user = userService.getLoggedUser().orElseThrow(NoLoggedUserException::new);
        Community community = findByName(communityName);
        if (communityDao.checkIfUserFollowsCommunity(user.getId(), community.getId())) {
            LOGGER.atError().setMessage("User {} already follows community {}").addArgument(() -> user.getUsername())
                    .addArgument(communityName).log();
            throw new AlreadyFollowedException();
        }
        communityDao.followCommunity(user.getId(), community.getId(), communityName);
        return true;

    }

    @Transactional
    @Override
    public void modifyUserOnCommunity(int communityId, String communityName) throws NoLoggedUserException {
        Optional<User> maybeUser = userService.getLoggedUser();
        if (maybeUser.isEmpty()) {
            LOGGER.atError().setMessage("Error while trying follow/unfollow communtity because there is no logged user")
                    .log();
            throw new NoLoggedUserException();
        }
        User user = maybeUser.get();
        if (communityDao.checkIfUserFollowsCommunity(user.getId(), communityId)) {
            communityDao.unfollowCommunity(user.getId(), communityId);
        } else {
            communityDao.followCommunity(user.getId(), communityId, communityName);
        }
        LOGGER.atInfo().setMessage("User {} followed community {} successfully").addArgument(() -> user.getUsername())
                .addArgument(communityName).log();
    }

    @Transactional
    @Override
    public void updateCommunityImageId(long id, long imageId) {
        communityDao.updateCommunityImageId(id, imageId);
        LOGGER.atInfo().setMessage("Community {} updated image {} successfully").addArgument(id).addArgument(imageId)
                .log();
    }

    @Transactional
    @Override
    public void editCommunityInfo(String communityName, String description, String publisher, String developer,
            byte[] image, String categories) throws NoSuchCommunityException {
        String decodedName = URLDecoder.decode(communityName, StandardCharsets.UTF_8);
        communityDao.editCommunityInfo(decodedName, description, publisher, developer);
        if (!Objects.isNull(image) && image.length > 0)
            fileService.uploadCommunityImage(decodedName, image);

        Community community = findByName(decodedName);
        List<String> currentCategories = Objects.isNull(community.getCategory()) ? Collections.emptyList()
                : new ArrayList<>(community.getCategory());
        if (!Objects.isNull(categories)) {
            List<String> newCategories = List.of(categories.split(","));
            for (String category : newCategories) {
                if (!currentCategories.contains(category)) {
                    addCategory(community.getId(), category);
                } else {
                    currentCategories.remove(category);
                }
            }
        }
        for (String category : currentCategories) {
            removeCategory(community.getId(), category);
        }
    }

    @Override
    public Boolean checkIfUserFollowsCommunity(int communityId) throws NoLoggedUserException, NoSuchCommunityException {
        Optional<User> maybeUser = userService.getLoggedUser();
        if (maybeUser.isEmpty()) {
            LOGGER.atError().setMessage(
                    "Error while trying to check if logged user follows community because there is no logged user")
                    .log();
            throw new NoLoggedUserException();
        }
        User user = maybeUser.get();
        return communityDao.checkIfUserFollowsCommunity(user.getId(), communityId);
    }

    @Override
    public List<Community> getFollowedCommunities(User user) {
        List<Community> followedCommunities = communityDao.getFollowedCommunitiesLimitedBy(user.getId(), 15);
        if (followedCommunities.isEmpty())
            return Collections.emptyList();
        return followedCommunities;
    }

    @Override
    @Transactional
    public Rating giveRating(String communityName, Float rating)
            throws NoSuchCommunityException, NoLoggedUserException, AlreadyRatedCommunityException {
        Community community = findByName(communityName);
        User user = userService.getLoggedUser().orElseThrow(NoLoggedUserException::new);
        if (ratingService.getRatingById(user, community).isPresent()) {
            throw new AlreadyRatedCommunityException();
        }
        Rating r = ratingService.createRating(user, community, rating);

        communityDao.updateRating(community, rating, 1);
        return r;

    }

    @Override
    @Transactional
    public void updatePortrait(String communityName, byte[] image) throws NoSuchCommunityException {
        String decodedName = URLDecoder.decode(communityName, StandardCharsets.UTF_8);
        fileService.uploadCommunityImage(decodedName, image);

    }
}

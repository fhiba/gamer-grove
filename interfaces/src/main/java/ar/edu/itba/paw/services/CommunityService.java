package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.IllegalPageException;
import ar.edu.itba.paw.exceptions.NoLoggedUserException;
import ar.edu.itba.paw.exceptions.NoSuchCommunityException;
import ar.edu.itba.paw.exceptions.PageNotFoundException;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.pagination.PaginatedDataWrapper;
import ar.edu.itba.paw.models.pagination.PaginationRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CommunityService {
    Optional<Community> createCommunity(final String name, final String description, String categories,
            String developer, String publisher, LocalDateTime releaseDate, byte[] image)
            throws NoSuchCommunityException;

    List<Community> getAllCommunities();

    Community findByName(final String communityName) throws NoSuchCommunityException;

    Community findById(final long communityId) throws NoSuchCommunityException;

    Community updateRating(Long community_id, float rating) throws NoSuchCommunityException, NoLoggedUserException;

    Community discountRating(String communityName, Float rating) throws NoSuchCommunityException, NoLoggedUserException;

    void modifyUserOnCommunity(int communityId, String communityName)
            throws NoLoggedUserException, NoSuchCommunityException;

    Boolean checkIfUserFollowsCommunity(int communityId) throws NoLoggedUserException, NoSuchCommunityException;

    List<Community> getFollowedCommunities(User user);

    PaginatedDataWrapper<Community> find(PaginationRequest request, final String searchTerms, List<String> categories,
            Long userId) throws PageNotFoundException, IllegalPageException;

    PaginatedDataWrapper<Community> findFollowedCommunities(PaginationRequest request, List<String> categories,
            User user) throws NoLoggedUserException, PageNotFoundException, IllegalPageException;

    void addCategory(final long id, String category) throws NoSuchCommunityException;

    void removeCategory(final long id, String category) throws NoSuchCommunityException;

    void addCategories(final long id, List<String> categories) throws NoSuchCommunityException;

    void updateCommunityImageId(long id, long imageId);

    void editCommunityInfo(String communityName, String description, String publisher, String developer,
            byte[] image, String categories) throws NoSuchCommunityException;

}

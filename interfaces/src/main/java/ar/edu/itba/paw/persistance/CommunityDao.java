package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CommunityDao {

    Optional<Community> findById(final long id);

    Community createCommunity(String name, String description, String developer, String publisher, LocalDateTime releaseDate);

    List<Community> findAllCommunities();

    Optional<Community> findByName(String communityName);

    Community updateRating(Community community, float rating, Integer count);


    List<Community> find(int pageSize,int offset,String searchTerms, List<String> categories);

    Boolean addCategory(final long id, String category);
    Community addCategory(Community community, String category);

    Boolean removeCategory(final long id, String category);
    Community removeCategory(Community community, String category);

    List<String> getCategoriesOfCommunity(final long id);

    Boolean checkIfUserFollowsCommunity(long userId, int communityId);

    List<String> getAllCategories();

    void unfollowCommunity(long id, int communityId);


    void followCommunity(long id, int communityId,String communityName);

    List<Community> getFollowedCommunitiesPaginated(Integer pageSize, Integer offset, Long userId);

    List<Community> getFollowedCommunitiesLimitedBy(Long userId, Integer limit);
    void updateCommunityImageId(long id, long imageId);

    List<Community> getAllCommunitiesNoCat();

    void editCommunityInfo(String communityName, String description, String publisher, String developer);

    int findCount(String searchTerms, List<String> categories);

    Integer getFollowedCommunitiesCount(Long userId);


}

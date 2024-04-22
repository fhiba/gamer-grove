package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.models.Community;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CommunityDao {

    Optional<Community> findById(final long id);

    Community createCommunity(String name, String description, String developer, String publisher, LocalDateTime releaseDate);

    List<Community> findAllCommunities();

    Optional<Community> findByName(String communityName);

    List<Community> find(String searchTerms, List<String> categories);

    Boolean addCategory(final long id, String category);

    Boolean removeCategory(final long id, String category);

    List<String> getCategoriesOfCommunity(final long id);

    Boolean checkIfUserFollowsCommunity(long userId, int communityId);

    void unfollowCommunity(long id, int communityId);

    void followCommunity(long id, int communityId,String communityName);

    List<Community> getFollowedCommunities(long userId);

    List<Community> getAllCommunitiesNoCat();
}

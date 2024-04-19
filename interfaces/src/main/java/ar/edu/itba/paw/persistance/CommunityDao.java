package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.models.Community;

import java.util.List;
import java.util.Optional;

public interface CommunityDao {

    Optional<Community> findById(final long id);

    void createCommunity(String name, String description);

    List<Community> findAllCommunities();

    Optional<Community> findByName(String communityName);

    List<Community> find(String searchTerms);

    Boolean checkIfUserFollowsCommunity(long userId, int communityId);

    void unfollowCommunity(long id, int communityId);

    void followCommunity(long id, int communityId,String communityName);
}

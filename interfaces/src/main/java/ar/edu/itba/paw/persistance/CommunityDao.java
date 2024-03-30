package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.models.Community;

import java.util.List;
import java.util.Optional;

public interface CommunityDao {

    Optional<Community> findById(final long id);

    void createCommunity(String name);

    List<Community> findAllCommunities();

    Optional<Community> findByName(String communityName);
}

package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Community;

import java.util.List;
import java.util.Optional;

public interface CommunityService {
    void createCommunity(final String name,final String description);
    List<Community> getAllCommunities();

    Optional<Community> findByName(final String communityName);

    Community findById(final long communityId);
}

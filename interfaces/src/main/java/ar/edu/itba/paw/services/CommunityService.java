package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Community;

import java.util.List;
import java.util.Optional;

public interface CommunityService {
    void createCommunity(String name,String description);
    List<Community> getAllCommunities();

    Optional<Community> findByName(String communityName);
}

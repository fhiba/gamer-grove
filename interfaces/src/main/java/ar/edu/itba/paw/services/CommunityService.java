package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Community;

import java.util.List;


public interface CommunityService {
    void createCommunity(final String name,final String description);
    List<Community> getAllCommunities();

    Community findByName(final String communityName);

    Community findById(final long communityId);

    List<Community> find(final String searchTerms);
}

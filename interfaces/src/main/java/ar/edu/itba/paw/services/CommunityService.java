package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.NoLoggedUserException;
import ar.edu.itba.paw.exceptions.NoSuchCommunityException;
import ar.edu.itba.paw.models.Community;

import java.util.List;


public interface CommunityService {
    void createCommunity(final String name,final String description, String categories) throws NoSuchCommunityException;
    List<Community> getAllCommunities();

    Community findByName(final String communityName) throws NoSuchCommunityException;

    Community findById(final long communityId)throws NoSuchCommunityException;

    List<Community> find(final String searchTerms);

    void modifyUserOnCommunity(int communityId,String communityName) throws NoLoggedUserException;

    Boolean checkIfUserFollowsCommunity(int communityId) throws NoLoggedUserException;

    List<Community> getFollowedCommunities();
    List<Community> find(final String searchTerms, List<String> categories);

    void addCategory(final long id, String category) throws NoSuchCommunityException;

    void removeCategory(final long id, String category) throws NoSuchCommunityException;

    void addCategories(final long id, List<String> categories) throws NoSuchCommunityException;

    void removeCategories(final long id, List<String> categories) throws NoSuchCommunityException;

}

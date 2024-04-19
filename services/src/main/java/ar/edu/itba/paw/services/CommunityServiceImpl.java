package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.NoLoggedUserException;
import ar.edu.itba.paw.exceptions.NoSuchCommunityException;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistance.CommunityDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class CommunityServiceImpl implements CommunityService{
    @Autowired
    private CommunityDao communityDao;
    @Autowired
    private UserService userService;

    @Override
    public void createCommunity(final String name, final String description) {
        communityDao.createCommunity(name,description);
    }

    @Override
    public List<Community> getAllCommunities() {
        List<Community> communities = communityDao.findAllCommunities();
        if(communities.isEmpty())
            return Collections.emptyList();
        return communities;
    }

    @Override
    public Community findByName(final String communityName) throws NoSuchCommunityException {
        Optional<Community> possibleCommunity = communityDao.findByName(communityName);
        if(possibleCommunity.isEmpty())
            throw new NoSuchCommunityException("Community " + communityName+ " not found");
        return possibleCommunity.get();
    }

    @Override
    public Community findById(final long communityId) throws NoSuchCommunityException{
        Optional<Community> community = communityDao.findById(communityId);
        if(community.isEmpty())
            throw new NoSuchCommunityException("Community " + communityId+ " not found");
        return community.get();
    }

    @Override
    public List<Community> find(final String searchTerms) {
        return communityDao.find(searchTerms.replaceAll("([%_\\\\])", "\\\\$1"));
    }

    @Override
    public void modifyUserOnCommunity(int communityId,String communityName) throws NoLoggedUserException {
        Boolean followsCommunity = checkIfUserFollowsCommunity(communityId);
        User user = userService.getLoggedUser().get();
        if(followsCommunity){
            communityDao.unfollowCommunity(user.getId(), communityId);
        }
        else{
            communityDao.followCommunity(user.getId(), communityId,communityName);
        }

    }

    @Override
    public Boolean checkIfUserFollowsCommunity(int communityId) throws NoLoggedUserException {
        Optional<User> maybeUser = userService.getLoggedUser();
        if(maybeUser.isEmpty())
            throw new NoLoggedUserException("No logged user");
        User user = maybeUser.get();
        return communityDao.checkIfUserFollowsCommunity(user.getId(),communityId);
    }

    @Override
    public List<Community> getFollowedCommunities() {
        return List.of();
    }
}

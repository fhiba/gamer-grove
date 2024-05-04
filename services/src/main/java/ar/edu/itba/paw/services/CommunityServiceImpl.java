package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.NoLoggedUserException;
import ar.edu.itba.paw.exceptions.NoSuchCommunityException;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistance.CommunityDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
@Service
public class CommunityServiceImpl implements CommunityService{
    @Autowired
    private CommunityDao communityDao;
    @Autowired
    private UserService userService;

    @Transactional
    @Override
    public void createCommunity(final String name, final String description, final String categories, String developer, String publisher, LocalDateTime releaseDate) throws NoSuchCommunityException {
        Community community = communityDao.createCommunity(name,description,developer,publisher,releaseDate);

        if(categories != null && !categories.isEmpty()) {
            addCategories(community.getId(), List.of(categories.split(",")));
        }
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
        Optional<Community> maybeCommunity = communityDao.findById(communityId);
        if(maybeCommunity.isEmpty())
            throw new NoSuchCommunityException("Community " + communityId+ " not found");
        return maybeCommunity.get();
    }



    @Override
    public List<Community> find(final String searchTerms, List<String> categories) {

        List<String> newList = null;
        if(!categories.isEmpty() && !categories.getFirst().isEmpty()) {
            newList = categories.stream().map(category -> category.replaceAll("([%_\\\\])", "\\\\$1")).toList();
        }
        return communityDao.find(searchTerms.replaceAll("([%_\\\\])", "\\\\$1"), newList == null? List.of(): newList);
    }

    @Transactional
    @Override
    public void addCategory(long id, String category) throws NoSuchCommunityException {
        Optional<Community> community = communityDao.findById(id);
        if(community.isEmpty())
            throw new NoSuchCommunityException("Community " + id+ " not found");
        communityDao.addCategory(id,category);
    }

    @Transactional
    @Override
    public void removeCategory(long id, String category) throws NoSuchCommunityException {
        Optional<Community> community = communityDao.findById(id);
        if(community.isEmpty())
            throw new NoSuchCommunityException("Community " + id+ " not found");
        communityDao.removeCategory(id,category);
    }

    @Transactional
    @Override
    public void addCategories(long id, List<String> categories) throws NoSuchCommunityException {
       for (String category: categories){
           addCategory(id,category);
       }
    }

    @Transactional
    @Override
    public void removeCategories(long id, List<String> categories) throws NoSuchCommunityException {
        for (String category: categories){
            removeCategory(id,category);
        }
    }

    @Override
    public List<Community> getAllCommunitiesNoCat() {
        List<Community> communities = communityDao.getAllCommunitiesNoCat();
        if(communities.isEmpty())
            return Collections.emptyList();
        return communities;
    }

    @Transactional
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

    @Transactional
    @Override
    public void updateCommunityImageId(long id, long imageId) {
        communityDao.updateCommunityImageId(id,imageId);
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
    public List<Community> getFollowedCommunities(User user) {
        List<Community> followedCommunities = communityDao.getFollowedCommunities(user.getId());
        if(followedCommunities.isEmpty())
            return Collections.emptyList();
        return followedCommunities;
    }
}

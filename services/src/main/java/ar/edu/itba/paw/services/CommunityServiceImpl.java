package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Community;
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
    public Community findByName(final String communityName) {
        Optional<Community> possibleCommunity = communityDao.findByName(communityName);
        if(!possibleCommunity.isPresent())
            //corregir exception a la correcta
            throw new IllegalArgumentException("Community not found");
        return possibleCommunity.get();
    }

    @Override
    public Community findById(final long communityId) {
        Optional<Community> community = communityDao.findById(communityId);
        if(!community.isPresent())
            throw new IllegalArgumentException("Community not found");
        return community.get();
    }

    @Override
    public List<Community> find(final String searchTerms) {
        return communityDao.find(searchTerms.replaceAll("([%_\\\\])", "\\\\$1"));
    }
}

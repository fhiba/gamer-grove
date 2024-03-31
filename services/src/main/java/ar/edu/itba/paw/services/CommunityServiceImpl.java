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
    public Optional<Community> findByName(final String communityName) {
        return communityDao.findByName(communityName);
    }

    @Override
    public Community findById(final long communityId) {
        Optional<Community> community = communityDao.findById(communityId);
        if(!community.isPresent())
            throw new IllegalArgumentException("Community not found");
        return community.get();
    }
}

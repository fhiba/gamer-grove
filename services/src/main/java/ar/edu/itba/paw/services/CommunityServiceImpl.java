package ar.edu.itba.paw.services;

import ar.edu.itba.paw.persistance.CommunityDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommunityServiceImpl implements CommunityService{
    @Autowired
    private CommunityDao communityDao;
}

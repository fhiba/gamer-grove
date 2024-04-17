package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.AlreadyModException;
import ar.edu.itba.paw.exceptions.NoSuchCommunityException;
import ar.edu.itba.paw.exceptions.UserNotFoundException;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistance.ModderDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ModderServiceImpl implements ModderService{

    @Autowired
    private UserService us;
    @Autowired
    private ModderDao md;
    @Autowired
    private CommunityService cs;
    @Override
    public int addModder(int userId, int communityId) throws UserNotFoundException, NoSuchCommunityException, AlreadyModException {
        Optional<User> newMod = us.findById(userId);
        if(newMod.isEmpty()){
            throw new UserNotFoundException("User with id "+userId+" not found");
        }
        // Supuestamente el service me dice si existe o no la comunidad
        Community community = cs.findById(communityId);
        //Checkeo si existe el mod
        if(md.isModderOfCommunity(userId,communityId)){
            throw new AlreadyModException("User with id "+userId+" is already a mod of community with id "+communityId);
        }
        return md.addModder(userId,communityId);
    }

    @Override
    public boolean isModderOfCommunity(int userId, int communityId) {
        return md.isModderOfCommunity(userId,communityId);
    }

    @Override
    public int removeModder(int userId, int communityId) {
        return md.removeModder(userId,communityId);
    }
}

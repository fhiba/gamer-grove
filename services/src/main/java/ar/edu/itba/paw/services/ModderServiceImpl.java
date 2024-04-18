package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.AlreadyModException;
import ar.edu.itba.paw.exceptions.NoSuchCommunityException;
import ar.edu.itba.paw.exceptions.NoSuchPostException;
import ar.edu.itba.paw.exceptions.UserNotFoundException;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.Post;
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
    @Autowired
    private PostService ps;

    @Override
    public int addModder(long userId, long communityId) throws UserNotFoundException, NoSuchCommunityException, AlreadyModException {
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
    public boolean isModderOfCommunity(long userId, long communityId) {
        return md.isModderOfCommunity(userId,communityId);
    }

    @Override
    public int removeModder(long userId, long communityId) {
        return md.removeModder(userId,communityId);
    }

    @Override
    public int removePost(long postId) {
        return md.removePost(postId);
    }

    @Override
    public boolean canRemovePost(long userId, long postId) throws NoSuchPostException, NoSuchCommunityException {
        Post toDelete = ps.getPostById(postId);
        Community postFrom = cs.findByName(toDelete.getCommunity_name());

        return md.isModderOfCommunity(userId, postFrom.getId());
    }

}

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
    public int addModder(String username, long communityId) throws UserNotFoundException, NoSuchCommunityException, AlreadyModException {
        Optional<User> possibleNewMod = us.findByUsername(username);
        if(possibleNewMod.isEmpty()){

            throw new UserNotFoundException("User with id "+username+" not found");
        }
        User newMod = possibleNewMod.get();
        // Supuestamente el service me dice si existe o no la comunidad
        Community community = cs.findById(communityId);
        //Checkeo si existe el mod
        if(md.isModderOfCommunity(newMod.getId(), communityId)){

            throw new AlreadyModException("User with id "+newMod.getId()+" is already a mod of community with id "+communityId);
        }
        return md.addModder(newMod.getId(), communityId);
    }

    @Override
    public boolean isModderOfCommunity(long userId, long communityId) {
        return md.isModderOfCommunity(userId,communityId);
    }

    @Override
    public int removeModder(String username, long communityId) throws UserNotFoundException {
        Optional<User> possibleNewMod = us.findByUsername(username);
        if(possibleNewMod.isEmpty()){
            throw new UserNotFoundException("User with id "+username+" not found");
        }
        User newMod = possibleNewMod.get();
        if(md.isModderOfCommunity(newMod.getId(), communityId)){
            return md.removeModder(newMod.getId(), communityId);
        }
        return 0;
    }

    @Override
    public int removePost(long postId) {
        return md.removePost(postId);
    }

    @Override
    public boolean canRemovePost(long userId, long postId) throws NoSuchPostException, NoSuchCommunityException {

        Post toDelete = ps.getPostById(postId);
        Community postFrom = cs.findByName(toDelete.getCommunityName());

        return md.isModderOfCommunity(userId, postFrom.getId());
    }

    @Override
    public boolean canRemovePostAlternative(long postId) throws NoSuchPostException, NoSuchCommunityException, UserNotFoundException {
        Post toDelete;
        try {
            toDelete = ps.getPostById(postId);
        } catch (NoSuchPostException e) {
            return false;
        }
        Community postFrom;
        try {
            postFrom = cs.findByName(toDelete.getCommunityName());
        } catch (NoSuchCommunityException e) {
            return false;
        }
        Optional<User> possibleMod = us.getLoggedUser();
        if (possibleMod.isEmpty()) {
            return false;
        }

        return md.isModderOfCommunity(possibleMod.get().getId(), postFrom.getId());
    }


}

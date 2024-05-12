package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.AlreadyModException;
import ar.edu.itba.paw.exceptions.NoSuchCommunityException;
import ar.edu.itba.paw.exceptions.NoSuchPostException;
import ar.edu.itba.paw.exceptions.UserNotFoundException;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistance.ModderDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Transactional(readOnly = true)
@Service
public class ModderServiceImpl implements ModderService{

    private static final Logger LOGGER = LoggerFactory.getLogger(ModderServiceImpl.class);


    @Autowired
    private UserService us;
    @Autowired
    private ModderDao md;
    @Autowired
    private CommunityService cs;
    @Autowired
    private PostService ps;

    @Autowired
    private MailingService mailingService;

    @Transactional
    @Override
    public int addModder(String username, long communityId) throws UserNotFoundException, NoSuchCommunityException, AlreadyModException {
        Optional<User> possibleNewMod = us.findByUsername(username);
        if(possibleNewMod.isEmpty()){
            throw new UserNotFoundException("User with username "+username+" not found");
        }
        User newMod = possibleNewMod.get();
        // Supuestamente el service me dice si existe o no la comunidad
        Community community = cs.findById(communityId);
        //Checkeo si existe el mod
        if(isModderOfCommunity(newMod.getId(), communityId)){
            throw new AlreadyModException("User with id "+newMod.getId()+" is already a mod of community with id "+communityId);
        }
        return md.addModder(newMod.getId(), communityId);
    }

    @Override
    public boolean isModderOfCommunity(long userId, long communityId) {
        return md.isModderOfCommunity(userId,communityId) || us.isUserAdmin(userId);
    }

    @Transactional
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

    @Transactional
    @Override
    public int removePost(long postId) {
        int toRet = md.removePost(postId);
        notifyDeletion(postId);
        return toRet;
    }


    @Async
    public void notifyDeletion(Long postId) {
        Post post;
        try {
            post = ps.getPostById(postId);
        } catch (NoSuchPostException e) {
            LOGGER.debug("Post not found");
            return;
        }
        long authorId = post.getAuthorId();
        Optional<User> author = us.findById(authorId);

        if(author.isEmpty()){
            LOGGER.debug("Author not found");
            return;
        }
        User authorUser = author.get();
        mailingService.notifyPostDeletion(authorUser.getEmail(), authorUser.getUsername(), post.getId(), post.getTitle(), post.getCommunityName());
    }

    @Override
    public boolean canRemovePost(long userId, long postId) throws NoSuchPostException, NoSuchCommunityException {

        Post toDelete = ps.getPostById(postId);
        Community postFrom = cs.findByName(toDelete.getCommunityName());

        return isModderOfCommunity(userId, postFrom.getId());
    }
    @Override
    public boolean canEditCommunityInfo(String encodedCommunityName) throws NoSuchCommunityException, UserNotFoundException {
        Community community;
        try {
            community = cs.findByName(URLDecoder.decode(encodedCommunityName, StandardCharsets.UTF_8));
        } catch (Exception e){
            return false;
        }
        Optional<User> possibleMod = us.getLoggedUser();
        if (possibleMod.isEmpty()) {
            return false;
        }
        return isModderOfCommunity(possibleMod.get().getId(), community.getId());
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

        return isModderOfCommunity(possibleMod.get().getId(), postFrom.getId());
    }
}

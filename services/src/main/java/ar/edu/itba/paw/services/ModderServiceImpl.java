package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.AlreadyModException;
import ar.edu.itba.paw.exceptions.NoSuchCommunityException;
import ar.edu.itba.paw.exceptions.NoSuchPostException;
import ar.edu.itba.paw.exceptions.UserNotFoundException;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.Mod;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.pagination.PaginatedDataWrapper;
import ar.edu.itba.paw.models.pagination.PaginationRequest;
import ar.edu.itba.paw.persistance.ModderDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
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
    public Boolean addModder(String username, long communityId) throws UserNotFoundException, NoSuchCommunityException, AlreadyModException {
        Optional<User> possibleNewMod = us.findByUsername(username);
        if(possibleNewMod.isEmpty()){
            LOGGER.atError().setMessage("Failed to add modder {} in the community with id {} becaouse the user was not found").addArgument(username).addArgument(communityId).log();
            throw new UserNotFoundException("User with username "+username+" not found");
        }
        User newMod = possibleNewMod.get();
        // Supuestamente el service me dice si existe o no la comunidad
        Community community = cs.findById(communityId);
        //Checkeo si existe el mod
        if(isModderOfCommunity(newMod, community)){
            LOGGER.atWarn().setMessage("Failed to add modder: User {} is already mod in the community with id {}").addArgument(username).addArgument(communityId).log();
            throw new AlreadyModException("User with id "+newMod.getId()+" is already a mod of community with id "+communityId);
        }
        sendNewModNotification(newMod, community);
        if(Objects.nonNull(md.addModder(newMod,community))){
            LOGGER.atInfo().setMessage("New mod {} added to community {} successfully").addArgument(username).addArgument(communityId).log();
            return true;
        }else{
            LOGGER.atError().setMessage("Fail to add mod {} to community {} ").addArgument(username).addArgument(communityId).log();
            return false;
        }
    }

    @Async
    void sendNewModNotification(User to, Community community) {
        mailingService.notifyNewModerator(to.getEmail(), to.getUsername(), community.getName(), community.getEncodedName(), Locale.of(to.getLocale()));
    }

    @Override
    public Boolean isModderOfCommunity(User user, Community community) {
        return md.isModderOfCommunity(user, community) || user.getOwner();
    }

    @Transactional
    @Override
    public Boolean removeModder(String username, long communityId) throws UserNotFoundException, NoSuchCommunityException {
        Optional<User> possibleNewMod = us.findByUsername(username);
        if(possibleNewMod.isEmpty()){
            LOGGER.atError().setMessage("Failed to remove modder in the community with id {} becaouse the user {} was not found").addArgument(communityId).addArgument(username).log();
            throw new UserNotFoundException("User with id "+username+" not found");
        }
        User newMod = possibleNewMod.get();
        Community community = cs.findById(communityId);
        if(md.isModderOfCommunity(newMod, community)){
            md.removeModder(md.findByid(newMod,community).orElseThrow());
            sendRemovedModNotification(newMod, community);
            LOGGER.atInfo().setMessage("Mod {} removed from community {} successfully").addArgument(username).addArgument(communityId).log();
            return true;
        }
        LOGGER.atWarn().setMessage("Failed to remove mod: User {} is already not a mod in the community with id {}").addArgument(username).addArgument(communityId).log();
        return false;
    }

    @Async
    void sendRemovedModNotification(User to, Community community) {
        mailingService.notifyRemovedModerator(to.getEmail(), to.getUsername(), community.getName(), Locale.of(to.getLocale()));
    }

    @Override
    public Boolean canRemovePost(User user, long postId) throws NoSuchPostException, NoSuchCommunityException {
        Post toDelete = ps.getPostById(postId);
        Community community = cs.findByName(toDelete.getcommunity().getName());
        return isModderOfCommunity(user, community);
    }
    @Override
    public Boolean canEditCommunityInfo(String encodedCommunityName) {
        Community community;
        try {
            community = cs.findByName(URLDecoder.decode(encodedCommunityName, StandardCharsets.UTF_8));
        } catch (NoSuchCommunityException e){
            LOGGER.atWarn().setMessage("Failed to check if user can edit community with encoded name {} because community was not found").addArgument(encodedCommunityName).log();
            return false;
        }
        Optional<User> possibleMod = us.getLoggedUser();
        if (possibleMod.isEmpty()) {
            LOGGER.atWarn().setMessage("Failed to check if can edit community because there is no logged user").log();
            return false;
        }
        return isModderOfCommunity(possibleMod.get(), community);
    }

    @Override
    public PaginatedDataWrapper<Mod> getAllModPaginated(PaginationRequest request) {
        if( request.getPageSize() < 1){
            throw new IllegalArgumentException("Invalid Page size");
        }
        if(request.getPageNumber() <1 ){
            throw new IllegalArgumentException("Invalid Page number");
        }
        int totalCount = md.getTotalModders();
        if(request.getPageNumber() <1 ){
            throw new IllegalArgumentException("Invalid Page number");
        }
        int offset = (request.getPageNumber() - 1) * request.getPageSize();
        List<Mod> data = md.getAllModdersPaginated(request.getPageSize() , offset);
        PaginatedDataWrapper<Mod> dataWrapper = new PaginatedDataWrapper<>(data, request.getPageNumber(), totalCount, request.getPageSize());
        if(request.getPageNumber() > dataWrapper.getTotalPages() && dataWrapper.getTotalPages() != 0){
            throw new IllegalArgumentException("Invalid Page number");
        }
        return dataWrapper;
    }
    @Override
    public PaginatedDataWrapper<Mod> getModsByCommunityPaginated(String communityName, PaginationRequest request) throws NoSuchCommunityException {
        if( request.getPageSize() < 1){
            throw new IllegalArgumentException("Invalid Page size");
        }
        if(request.getPageNumber() <1 ){
            throw new IllegalArgumentException("Invalid Page number");
        }
        Community community = cs.findByName(communityName);
        int totalCount = md.getTotalModdersByCommunity(community.getId());
        if(request.getPageNumber() <1 ){
            throw new IllegalArgumentException("Invalid Page number");
        }
        int offset = (request.getPageNumber() - 1) * request.getPageSize();
        List<Mod> data = md.getModdersPaginatedByCommunity(community.getId(),request.getPageSize() , offset);
        PaginatedDataWrapper<Mod> dataWrapper = new PaginatedDataWrapper<>(data, request.getPageNumber(), totalCount, request.getPageSize());
        if(request.getPageNumber() > dataWrapper.getTotalPages() && dataWrapper.getTotalPages() != 0){
            throw new IllegalArgumentException("Invalid Page number");
        }
        return dataWrapper;
    }

    @Override
    public PaginatedDataWrapper<Mod> getModsByUsernamePaginated(String username, PaginationRequest request) throws UserNotFoundException {
        if( request.getPageSize() < 1){
            throw new IllegalArgumentException("Invalid Page size");
        }
        if(request.getPageNumber() <1 ){
            throw new IllegalArgumentException("Invalid Page number");
        }
        Optional<User> maybeUser = us.findByUsername(username);
        if(maybeUser.isEmpty())
            throw new UserNotFoundException("The user you filter don't exists");
        User user = maybeUser.get();
        int totalCount = md.getTotalModdersByUserId(user.getId());
        if(request.getPageNumber() <1 ){
            throw new IllegalArgumentException("Invalid Page number");
        }
        int offset = (request.getPageNumber() - 1) * request.getPageSize();
        List<Mod> data = md.getModdersPaginatedByUserId(user.getId(),request.getPageSize() , offset);
        PaginatedDataWrapper<Mod> dataWrapper = new PaginatedDataWrapper<>(data, request.getPageNumber(), totalCount, request.getPageSize());
        if(request.getPageNumber() > dataWrapper.getTotalPages() && dataWrapper.getTotalPages() != 0){
            throw new IllegalArgumentException("Invalid Page number");
        }
        return dataWrapper;
    }

    @Override
    public Optional<Mod> findMod(String username, String communityName) throws UserNotFoundException, NoSuchCommunityException {
        Optional<User> maybeUser = us.findByUsername(username);
        if(maybeUser.isEmpty()) {
            LOGGER.atWarn().setMessage("Failed to find user {}").addArgument(username).log();
            throw new UserNotFoundException("The user you filter don't exists");
        }
        User user = maybeUser.get();
        Community community = cs.findByName(communityName);
        return md.findByid(user,community);
    }


    @Override
    public Boolean canRemovePostAlternative(long postId) {
        Post toDelete;
        try {
            toDelete = ps.getPostById(postId);
        } catch (NoSuchPostException e) {
            LOGGER.atWarn().setMessage("Failed to find post with id {}").addArgument(postId).log();
            return false;
        }
        Community postFrom;
        try {
            postFrom = cs.findByName(toDelete.getcommunity().getName());
        } catch (NoSuchCommunityException e) {
            LOGGER.atWarn().setMessage("Failed to find community {}").addArgument(()-> toDelete.getcommunity().getName()).log();
            return false;
        }
        Optional<User> possibleMod = us.getLoggedUser();
        if (possibleMod.isEmpty()) {
            LOGGER.atWarn().setMessage("There is no logged user").log();
            return false;
        }

        return isModderOfCommunity(possibleMod.get(), postFrom);
    }
}

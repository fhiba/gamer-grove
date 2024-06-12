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

import java.util.Optional;

public interface ModderService {

    Boolean addModder(final String username,final long communityId) throws UserNotFoundException, NoSuchCommunityException, AlreadyModException;

    Boolean isModderOfCommunity(final User user,final Community community);

    Boolean removeModder(final String username, final long communityId) throws UserNotFoundException, NoSuchCommunityException;


    Boolean canRemovePost(final User userId, final long postId) throws NoSuchPostException, NoSuchCommunityException;// fijar si el post pertenece a la comnunidad y el mod tiene los perm necesarios

    Boolean canRemovePostAlternative(long postId) throws NoSuchPostException, NoSuchCommunityException, UserNotFoundException;

    Boolean canEditCommunityInfo(String encodedCommunityName) throws NoSuchCommunityException, UserNotFoundException;
    PaginatedDataWrapper<Mod> getAllModPaginated(PaginationRequest paginationRequest);

    PaginatedDataWrapper<Mod> getModsByCommunityPaginated(String communityName, PaginationRequest request) throws NoSuchCommunityException;

    PaginatedDataWrapper<Mod> getModsByUsernamePaginated(String username, PaginationRequest paginationRequest) throws UserNotFoundException;

    Optional<Mod> findMod(String username, String community) throws UserNotFoundException, NoSuchCommunityException;
}

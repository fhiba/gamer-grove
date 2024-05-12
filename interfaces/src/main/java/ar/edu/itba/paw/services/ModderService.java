package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.AlreadyModException;
import ar.edu.itba.paw.exceptions.NoSuchCommunityException;
import ar.edu.itba.paw.exceptions.NoSuchPostException;
import ar.edu.itba.paw.exceptions.UserNotFoundException;

public interface ModderService {

    int addModder(final String username,final long communityId) throws UserNotFoundException, NoSuchCommunityException, AlreadyModException;

    boolean isModderOfCommunity(final long userId, final long communityId);

    int removeModder(final String username, final long communityId) throws UserNotFoundException;

    int removePost(final long postId);
    boolean canRemovePost(final long userId, final long postId) throws NoSuchPostException, NoSuchCommunityException;// fijar si el post pertenece a la comnunidad y el mod tiene los perm necesarios

    boolean canRemovePostAlternative(long postId) throws NoSuchPostException, NoSuchCommunityException, UserNotFoundException;

    public boolean canEditCommunityInfo(String encodedCommunityName) throws NoSuchCommunityException, UserNotFoundException;
}

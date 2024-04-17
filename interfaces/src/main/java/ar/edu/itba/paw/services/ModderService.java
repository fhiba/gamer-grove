package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.AlreadyModException;
import ar.edu.itba.paw.exceptions.NoSuchCommunityException;
import ar.edu.itba.paw.exceptions.UserNotFoundException;

public interface ModderService {

    int addModder(final int userId,final int communityId) throws UserNotFoundException, NoSuchCommunityException, AlreadyModException;

    boolean isModderOfCommunity(final int userId, final int communityId);

    int removeModder(final int userId, final int communityId);
}

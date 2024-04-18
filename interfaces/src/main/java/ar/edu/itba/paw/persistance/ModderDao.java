package ar.edu.itba.paw.persistance;

import java.util.List;

public interface ModderDao {

    int addModder(final long userId,final long communityId);

    boolean isModderOfCommunity(final long userId, final long communityId);

    int removeModder(final long userId, final long communityId);

    int removePost(long postId);
}

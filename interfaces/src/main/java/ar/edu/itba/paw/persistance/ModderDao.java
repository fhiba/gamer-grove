package ar.edu.itba.paw.persistance;

import java.util.List;

public interface ModderDao {

    int addModder(final int userId,final int communityId);

    boolean isModderOfCommunity(final int userId, final int communityId);

    int removeModder(final int userId, final int communityId);
}

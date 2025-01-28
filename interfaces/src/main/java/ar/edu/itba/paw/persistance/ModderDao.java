package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.Mod;
import ar.edu.itba.paw.models.User;

import java.util.List;
import java.util.Optional;

public interface ModderDao {

    Mod addModder(User user, Community community);

    Boolean isModderOfCommunity(User user, Community community);

    Optional<Mod> findByid(User user, Community community);

    void removeModder(Mod mod);

    List<Mod> getAllModdersPaginated(int pageSize, int offset);

    List<Mod> getModdersPaginatedByCommunity(Long community_id, int pageSize, int offset);

    int getTotalModders();

    int getTotalModdersByCommunity(Long communityId);

    int getTotalModdersByUserId(Long id);

    List<Mod> getModdersPaginatedByUserId(Long id, int pageSize, int offset);

    List<Mod> findModsPaginated(String userName, Long communityId, int pageSize, int offset);

    Long findTotalMods(String userName, Long communityId);
}

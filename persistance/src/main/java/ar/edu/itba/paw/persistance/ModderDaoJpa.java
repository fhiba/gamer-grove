package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.models.Community;

import ar.edu.itba.paw.models.Mod;
import ar.edu.itba.paw.models.User;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
@Primary
public class ModderDaoJpa implements ModderDao {
    @PersistenceContext
    private EntityManager em;

    @Override
    public Mod addModder(User user, Community community) {
        Mod mod = new Mod(user, community, LocalDateTime.now());
        em.persist(mod);
        return mod;
    }

    @Override
    public Boolean isModderOfCommunity(User user, Community community) {
        TypedQuery<Mod> query = em
                .createQuery("from Mod as m where m.community.id = :communityId and m.user.id = :userId", Mod.class);
        query.setParameter("communityId", community.getId());
        query.setParameter("userId", user.getId());
        return !query.getResultList().isEmpty();
    }

    @Override
    public Optional<Mod> findByid(User user, Community community) {
        TypedQuery<Mod> query = em
                .createQuery("from Mod as m where m.community.id = :communityId and m.user.id = :userId", Mod.class);
        query.setParameter("communityId", community.getId());
        query.setParameter("userId", user.getId());
        return query.getResultList().stream().findFirst();
    }

    @Override
    public void removeModder(Mod mod) {
        em.remove(mod);
    }

    @Override
    public List<Mod> getAllModdersPaginated(int pageSize, int offset) {
        Query nativeQuery = em.createNativeQuery("SELECT user_id,community_id FROM modders ORDER BY since_date DESC");
        nativeQuery.setFirstResult(offset);
        nativeQuery.setMaxResults(pageSize);
        List<Object[]> resultList = nativeQuery.getResultList();
        // Create lists to hold the keys and values
        List<Long> userIds = new ArrayList<>();
        List<Long> communityIds = new ArrayList<>();
        for (Object[] row : resultList) {
            Long userId = ((Number) row[0]).longValue();
            Long communityId = ((Number) row[1]).longValue();
            userIds.add(userId);
            communityIds.add(communityId);
        }
        TypedQuery<Mod> query = em.createQuery(
                "from Mod as m where m.user.id IN :userIds and m.community.id in :communityIds order by m.sinceDate desc",
                Mod.class);
        query.setParameter("userIds", userIds);
        query.setParameter("communityIds", communityIds);
        return query.getResultList();
    }

    @Override
    public List<Mod> getModdersPaginatedByCommunity(Long communityId, int pageSize, int offset) {
        Query nativeQuery = em.createNativeQuery(
                "SELECT user_id FROM modders WHERE community_id = :communityId order by since_date desc");
        nativeQuery.setFirstResult(offset);
        nativeQuery.setMaxResults(pageSize);
        nativeQuery.setParameter("communityId", communityId);
        List<Long> resultList = ((Stream<Number>) nativeQuery.getResultStream()).map(Number::longValue).toList();

        TypedQuery<Mod> query = em.createQuery(
                "from Mod as m where m.user.id IN :ids and m.community.id = :communityId order by m.sinceDate desc",
                Mod.class);
        query.setParameter("ids", resultList);
        query.setParameter("communityId", communityId);
        return query.getResultList();
    }

    @Override
    public int getTotalModders() {
        Query query = em.createQuery("select count(*) from Mod");
        return ((Number) query.getSingleResult()).intValue();
    }

    @Override
    public int getTotalModdersByCommunity(Long communityId) {
        Query query = em.createQuery("select count(*) from Mod as m where m.community.id = :id")
                .setParameter("id", communityId);
        return ((Number) query.getSingleResult()).intValue();
    }

    @Override
    public int getTotalModdersByUserId(Long userId) {
        Query query = em.createQuery("select count(*) from Mod as m where m.user.id = :id")
                .setParameter("id", userId);
        return ((Number) query.getSingleResult()).intValue();
    }

    @Override
    public List<Mod> getModdersPaginatedByUserId(Long userId, int pageSize, int offset) {
        Query nativeQuery = em
                .createNativeQuery("SELECT user_id FROM modders WHERE user_id = :userId order by since_date desc");
        nativeQuery.setFirstResult(offset);
        nativeQuery.setMaxResults(pageSize);
        nativeQuery.setParameter("userId", userId);
        List<Long> resultList = ((Stream<Number>) nativeQuery.getResultStream()).map(Number::longValue).toList();

        TypedQuery<Mod> query = em.createQuery("from Mod as m where m.user.id IN :ids order by m.sinceDate desc",
                Mod.class);
        query.setParameter("ids", resultList);
        return query.getResultList();
    }

    @Override
    public List<Mod> findModsPaginated(String userName, Long communityId, int pageSize, int offset) {

        String sqlString = "SELECT m.user_id FROM modders as m JOIN users as u ON m.user_id = u.id WHERE u.username ILIKE :userName"
                + (Objects.isNull(communityId) ? "" : " AND m.community_id = :communityId")
                + " ORDER BY m.since_date desc";
        Query nativeQuery = em.createNativeQuery(
                sqlString);
        if (Objects.nonNull(communityId)) {
            nativeQuery.setParameter("communityId", communityId);
        }
        nativeQuery.setParameter("userName", "%" + userName + "%");
        nativeQuery.setFirstResult(offset);
        nativeQuery.setMaxResults(pageSize);

        List<Long> resultList = ((Stream<Number>) nativeQuery.getResultStream()).map(Number::longValue).toList();

        TypedQuery<Mod> query = em.createQuery(
                "from Mod as m where m.user.id IN :ids "
                        + (Objects.isNull(communityId) ? "" : " and m.community.id = :communityId")
                        + " order by m.sinceDate desc",
                Mod.class);
        query.setParameter("ids", resultList);
        if (Objects.nonNull(communityId)) {
            query.setParameter("communityId", communityId);
        }
        return query.getResultList();

    }

    @Override
    public Long findTotalMods(String userName, Long communityId) {
        String sqlString = "SELECT COUNT(m.user_id) FROM modders as m JOIN users as u ON m.user_id = u.id WHERE u.username ILIKE :userName "
                + (Objects.isNull(communityId) ? "" : "AND m.community_id = :communityId");
        Query nativeQuery = em.createNativeQuery(sqlString);
        if (Objects.nonNull(communityId)) {
            nativeQuery.setParameter("communityId", communityId);
        }
        nativeQuery.setParameter("userName", "%" + userName + "%");
        return ((Number) nativeQuery.getSingleResult()).longValue();
    }

}

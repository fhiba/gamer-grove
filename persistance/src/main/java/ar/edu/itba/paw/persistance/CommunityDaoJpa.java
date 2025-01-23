package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.CommunityCategories;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public class CommunityDaoJpa implements CommunityDao {
    @PersistenceContext
    private EntityManager em;

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(CommunityDaoJpa.class);

    @Override
    public Optional<Community> findById(long id) {
        return Optional.ofNullable(em.find(Community.class, id));
    }

    @Override
    public Community createCommunity(String name, String description, String developer, String publisher,
            LocalDateTime releaseDate) {
        Community c = new Community(name, description, developer, publisher, releaseDate);
        em.persist(c);
        return c;
    }

    @Override
    public List<Community> findAllCommunities() {
        TypedQuery<Community> query = em.createQuery("from Community", Community.class);
        return query.getResultList();
    }

    @Override
    public Optional<Community> findByName(String communityName) {
        return em.createQuery("from Community as c where c.name = :name", Community.class)
                .setParameter("name", communityName)
                .getResultStream().findFirst();
    }

    @Override
    public Community updateRating(Community community, float rating, Integer ratingCount) {
        community.setTotalRating(community.getTotalRating() + rating);
        community.setRatingCount(community.getRatingCount() + ratingCount);
        return community;
    }

    @Override
    public List<Community> find(int pageSize, int offset, String searchTerms, List<String> categories, Long userId) {

        LOGGER.info("Searching for communities: {}, {}, {}", searchTerms, categories.size(), userId);
        List<Long> idList = new QueryBuilder()
                .withSearchTerms(searchTerms)
                .withCategories(categories)
                .followedBy(userId == 0 ? null : userId)
                .build(pageSize, offset);
        TypedQuery<Community> query = em.createQuery("SELECT c FROM Community c WHERE c.id IN :ids", Community.class);
        query.setParameter("ids", idList);
        return query.getResultList();
    }

    private class QueryBuilder {
        private static final String SELECT = "SELECT c.id FROM Community c WHERE TRUE ";
        private static final String COUNT = "SELECT COUNT(c.id) FROM Community c WHERE TRUE ";
        private static final String SEARCH_TERM = "AND c.name ILIKE :searchTerms ";
        private static final String FOLLOWED_BY = "AND c.id IN (SELECT cu.community_id FROM community_user cu WHERE cu.user_id = :userId) ";
        private static final String CATEGORY = "AND c.id IN (SELECT cc.community_id FROM communities_categories cc WHERE cc.category in :categories GROUP BY cc.community_id HAVING COUNT(cc.community_id) = :categoriesCount) ";
        private final List<String> categories = new ArrayList<>();
        private String searchTerms = "";
        private Long userId = null;

        public QueryBuilder() {

        }

        public QueryBuilder withCategories(List<String> categories) {
            this.categories.addAll(categories);
            return this;
        }

        public QueryBuilder withSearchTerms(String searchTerms) {
            if (searchTerms == null || searchTerms.isEmpty())
                return this;

            this.searchTerms = searchTerms;
            return this;
        }

        public QueryBuilder followedBy(Long userId) {
            this.userId = userId;
            return this;
        }

        List<Long> build(Integer pageSize, Integer offset) {
            StringBuilder query = new StringBuilder(SELECT);
            if (!searchTerms.isEmpty()) {
                query.append(SEARCH_TERM);
            }
            if (!categories.isEmpty()) {
                query.append(CATEGORY);
            }
            if (userId != null) {
                query.append(FOLLOWED_BY);
            }
            Query q = em.createNativeQuery(query.toString());
            if (!searchTerms.isEmpty()) {
                q.setParameter("searchTerms", "%" + searchTerms + "%");
            }
            if (!categories.isEmpty()) {
                q.setParameter("categories", categories);
                q.setParameter("categoriesCount", categories.size());
            }
            if (userId != null) {
                q.setParameter("userId", userId);
            }
            q.setFirstResult(offset);
            q.setMaxResults(pageSize);
            return ((Stream<Number>) q.getResultStream()).map(Number::longValue).toList();
        }

        Integer buildCount() {
            StringBuilder query = new StringBuilder(COUNT);
            if (!searchTerms.isEmpty()) {
                query.append(SEARCH_TERM);
            }
            if (!categories.isEmpty()) {
                query.append(CATEGORY);
            }
            if (userId != null) {
                query.append(FOLLOWED_BY);
            }
            Query q = em.createNativeQuery(query.toString());
            if (!searchTerms.isEmpty()) {
                q.setParameter("searchTerms", "%" + searchTerms + "%");
            }
            if (!categories.isEmpty()) {
                q.setParameter("categories", categories);
                q.setParameter("categoriesCount", categories.size());
            }
            if (userId != null) {
                q.setParameter("userId", userId);
            }
            return ((Number) q.getSingleResult()).intValue();
        }
    }

    @Override
    public Boolean addCategory(long id, String category) {
        return em
                .createNativeQuery(
                        "INSERT INTO communities_categories (community_id, category) VALUES (:id, :category)")
                .setParameter("id", id)
                .setParameter("category", category)
                .executeUpdate() > 0;
    }

    @Override
    public Community addCategory(Community community, String category) {
        List<CommunityCategories> list = community.getCategoriesEnum();
        if (Objects.isNull(list))
            list = new ArrayList<>();
        list.add(CommunityCategories.fromString(category));
        community.setCategory(list);
        return community;
    }

    @Override
    public Boolean removeCategory(long id, String category) {
        return em
                .createNativeQuery(
                        "DELETE FROM communities_categories WHERE community_id = :id AND category = :category")
                .setParameter("id", id)
                .setParameter("category", category)
                .executeUpdate() > 0;
    }

    @Override
    public Community removeCategory(Community community, String category) {
        community.getCategoriesEnum().remove(CommunityCategories.fromString(category));
        return em.merge(community);
    }

    @Override
    public Boolean checkIfUserFollowsCommunity(long userId, long communityId) {
        return em
                .createNativeQuery(
                        "SELECT 1 FROM community_user WHERE user_id = :userId AND community_id = :communityId")
                .setParameter("userId", userId)
                .setParameter("communityId", communityId)
                .getResultStream().findFirst().isPresent();
    }

    @Override
    public void unfollowCommunity(long id, long communityId) {
        em.createNativeQuery("DELETE FROM community_user WHERE user_id = :id AND community_id = :communityId")
                .setParameter("id", id)
                .setParameter("communityId", communityId)
                .executeUpdate();
    }

    @Override
    public void followCommunity(long id, long communityId, String communityName) {
        em.createNativeQuery(
                "INSERT INTO community_user (user_id, community_id, community_name, community_role) VALUES (:id, :communityId, :communityName, :role)")
                .setParameter("id", id)
                .setParameter("communityId", communityId)
                .setParameter("communityName", communityName)
                .setParameter("role", 0)
                .executeUpdate();

    }

    @Override
    public List<Community> getFollowedCommunitiesLimitedBy(Long userId, Integer limit) {
        Query nativeQuery = em.createNativeQuery("SELECT community_id FROM community_user WHERE user_id = :userId")
                .setParameter("userId", userId)
                .setMaxResults(limit);

        @SuppressWarnings("unchecked")
        List<Long> resultList = ((Stream<Number>) nativeQuery.getResultStream()).map(Number::longValue).toList();

        TypedQuery<Community> query = em.createQuery("from Community as c where c.id IN :ids", Community.class);
        query.setParameter("ids", resultList);
        return query.getResultList();
    }

    @Override
    public void updateCommunityImageId(long id, long imageId) {
        em.createNativeQuery("UPDATE Community SET portrait_id = :portraitId WHERE id = :id")
                .setParameter("portraitId", imageId)
                .setParameter("id", id)
                .executeUpdate();
    }

    @Override
    public void editCommunityInfo(String communityName, String description, String publisher, String developer) {
        em.createQuery(
                "UPDATE Community SET  description = :description, publisher = :publisher, developer = :developer WHERE name = :communityName")
                .setParameter("communityName", communityName)
                .setParameter("description", description)
                .setParameter("publisher", publisher)
                .setParameter("developer", developer)
                .executeUpdate();
    }

    @Override
    public int findCount(String searchTerms, List<String> categories, Long userId) {
        return new QueryBuilder()
                .withSearchTerms(searchTerms)
                .withCategories(categories)
                .followedBy(userId)
                .buildCount();
    }
}

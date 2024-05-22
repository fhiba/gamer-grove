package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.CommunityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.*;

@Repository
public class CommunityDaoJdbc implements CommunityDao {

    private static final RowMapper<Community> ROW_MAPPER = (rs, rowNum) -> {
        Community community = new Community(rs.getLong("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getString("publisher"),
                rs.getString("developer"),
                rs.getTimestamp("release_date").toLocalDateTime());
        community.setPortrait_id(rs.getLong("portrait_id"));
        return community;
    };

    private static final RowMapper<Community> ROW_MAPPER_CATEGORIES = (rs, rowNum) -> {
        Community community = new Community(rs.getLong("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getString("publisher"),
                rs.getString("developer"),
                rs.getTimestamp("release_date").toLocalDateTime());
        community.setPortrait_id(rs.getLong("portrait_id"));
        if (rs.getString("categories") != null)
            community.setCategories(Arrays.stream(rs.getString("categories").split(",")).toList());
        return community;
    };

//    TODO:SAFE DELETE COMMUNITY USER
    private static final RowMapper<CommunityUser> ROW_MAPPER_USER = (rs, rowNum) -> new CommunityUser(rs.getInt("user_id"),
            rs.getInt("community_id"),
            rs.getInt("community_role"),
            rs.getString("community_name"));



    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;
    private final SimpleJdbcInsert jdbcInsertCategory;
    private final SimpleJdbcInsert jdbcInsertUser;


    @Autowired
    public CommunityDaoJdbc(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(ds).usingGeneratedKeyColumns("id").withTableName("community");
        jdbcInsertUser = new SimpleJdbcInsert(ds).withTableName("community_user");
        jdbcInsertCategory = new SimpleJdbcInsert(ds).withTableName("communities_categories");
    }

    @Override
    public Optional<Community> findById(long id) {
        final String query = "SELECT community.*, string_agg(cc.category, ',') as categories FROM community LEFT JOIN communities_categories as cc ON community.id = cc.community_id WHERE id = ? GROUP BY community.id, community.name, community.description, community.portrait_id ORDER BY community.name";
        final Object[] args = {id};
        final List<Community> list = jdbcTemplate.query(query, args, ROW_MAPPER_CATEGORIES);
        return list.stream().findFirst();
    }

    @Override
    public Community createCommunity(String name, String description, String developer, String publisher, LocalDateTime releaseDate) {
        Map<String, Object> args = new HashMap<>();
        args.put("name", name);
        args.put("description", description);
        args.put("portrait_id",null);
        args.put("developer",developer);
        args.put("publisher",publisher);
        args.put("release_date",releaseDate);
        long id = jdbcInsert.executeAndReturnKey(args).longValue();
        return new Community(id, name, description,publisher,developer,releaseDate);
    }

    @Override
    public List<Community> findAllCommunities() {
        return jdbcTemplate.query("SELECT community.*, string_agg(cc.category, ',') as categories" +
                " FROM community LEFT JOIN communities_categories as cc ON community.id = cc.community_id" +
                " GROUP BY community.id, community.name, community.description, community.portrait_id" +
                " ORDER BY community.name", ROW_MAPPER_CATEGORIES);
    }

    @Override
    public Optional<Community> findByName(String communityName) {
        return jdbcTemplate.query("SELECT community.*, string_agg(cc.category, ',') as categories" +
                        " FROM community LEFT JOIN communities_categories as cc ON community.id = cc.community_id" +
                        " WHERE name = ?" +
                        " GROUP BY community.id, community.name, community.description, community.portrait_id" +
                        " ORDER BY community.name"
                , new Object[]{communityName}, ROW_MAPPER_CATEGORIES).stream().findFirst();
    }

    @Override
    public List<String> getCategoriesOfCommunity(long id) {
        return jdbcTemplate.query("SELECT category FROM communities_categories WHERE community_id = ?", new Object[]{id}, (rs, rowNum) -> rs.getString("category"));
    }

    @Override
    public void updateCommunityImageId(long id, long imageId) {
        jdbcTemplate.update("UPDATE community SET portrait_id = ? WHERE id = ?", imageId, id);
    }

    @Override
    public List<Community> find(int pageSize,int offset,String searchTerms, List<String> categories) {
        QueryBuilder builder = new QueryBuilder().withSearchTerm(searchTerms).withCategories(categories);
        return builder.buildPaginated(pageSize,offset);
    }


    @Override
    public Boolean addCategory(long id, String category) {
        Map<String, Object> args = new HashMap<>();
        args.put("community_id", id);
        args.put("category", category);
        return jdbcInsertCategory.execute(args) > 0;
    }

    @Override
    public Boolean removeCategory(long id, String category) {
        return jdbcTemplate.update("DELETE FROM communities_categories WHERE community_id = ? AND category = ?", id, category) > 0;
    }


    private class QueryBuilder {
        private final static String SELECT = "SELECT community.*, string_agg(cc.category, ',') as categories FROM community LEFT JOIN communities_categories as cc ON community.id = cc.community_id";
        private final static String COUNT_SELECT = "SELECT COUNT(*) FROM community";
        private final static String SEARCH_TERM = " WHERE name ILIKE ?";
        private final static String CATEGORY = " AND community.id IN (SELECT cc.community_id FROM communities_categories as cc";
        private final static String CATEGORY_CONDITION = " WHERE cc.category LIKE ?";
        private final static String SECONDARY_CATEGORY_CONDITION = " OR cc.category LIKE ?";
        private final static String CATEGORY_END = " GROUP BY cc.community_id HAVING COUNT(cc.community_id) = ?)";
        private final static String END = " GROUP BY community.id, community.name, community.description, community.portrait_id ORDER BY community.name";
        private final static String PAGINATION = " LIMIT ? OFFSET ?";
        private final List<String> categories = new ArrayList<>();
        private String searchTerm = "";
        private int categoriesCount = 0;

        public QueryBuilder() {
        }

        public QueryBuilder withCategory(String category) {
            categories.add(category);
            categoriesCount++;
            return this;
        }

        public QueryBuilder withSearchTerm(String searchTerm) {
            if (searchTerm == null || searchTerm.isEmpty())
                return this;

            this.searchTerm = searchTerm;
            return this;
        }

        public QueryBuilder withCategories(List<String> categories) {
            if (categories == null || categories.isEmpty()) {
                return this;
            }
            this.categories.addAll(categories);
            categoriesCount = categories.size();
            return this;
        }

        List<Community> buildPaginated(int pageSize,int offset) {
            StringBuilder sb = new StringBuilder();
            List<Object> objects = new ArrayList<>();
            sb.append(SELECT);
            sb.append(SEARCH_TERM);
            long categoriesSize = categories.size();
            objects.add("%" + searchTerm + "%");

            if (categoriesCount > 0) {
                sb.append(CATEGORY);
                sb.append(CATEGORY_CONDITION);
                categoriesCount--;
                sb.append(SECONDARY_CATEGORY_CONDITION.repeat(categoriesCount));
                objects.addAll(categories);
                objects.add(categoriesSize);
                sb.append(CATEGORY_END);
            }
            sb.append(END);
            objects.add(pageSize);
            objects.add(offset);
            sb.append(PAGINATION);
            String query = sb.toString();

            return jdbcTemplate.query(query, objects.toArray(), ROW_MAPPER_CATEGORIES);
        }
        Integer buildCount() {
            StringBuilder sb = new StringBuilder();
            List<Object> objects = new ArrayList<>();
            sb.append(COUNT_SELECT);
            sb.append(SEARCH_TERM);
            long categoriesSize = categories.size();
            objects.add("%" + searchTerm + "%");
            if (categoriesCount > 0) {
                sb.append(CATEGORY);
                sb.append(CATEGORY_CONDITION);
                categoriesCount--;
                sb.append(SECONDARY_CATEGORY_CONDITION.repeat(categoriesCount));
                objects.addAll(categories);
                objects.add(categoriesSize);
                sb.append(CATEGORY_END);
            }
            String query = sb.toString();
            return jdbcTemplate.queryForObject(query, objects.toArray(), Integer.class);
        }
    }

    @Override
    public Boolean checkIfUserFollowsCommunity(long userId, int communityId) {
        return !jdbcTemplate.query("SELECT * FROM community_user WHERE user_id = ? AND community_id = ?", new Object[]{userId, communityId}, ROW_MAPPER_USER).isEmpty();
    }

    @Override
    public void unfollowCommunity(long id, int communityId) {
        jdbcTemplate.update("DELETE FROM community_user WHERE user_id = ? AND community_id = ?", id, communityId);
    }

    @Override
    public void followCommunity(long id, int communityId, String communityName) {
        Map<String, Object> args = new HashMap<>();
        args.put("community_id", communityId);
        args.put("user_id", id);
        args.put("community_role", 0);
        args.put("community_name", communityName);
        jdbcInsertUser.execute(args);
    }

    @Override
    public List<Community> getFollowedCommunities(long userId) {
        return jdbcTemplate.query("SELECT * FROM community WHERE id IN (SELECT community_id FROM community_user WHERE user_id = ?)", new Object[]{userId}, ROW_MAPPER);
    }

    @Override
    public List<Community> getAllCommunitiesNoCat() {
        return jdbcTemplate.query("SELECT * FROM community", ROW_MAPPER);
    }

    @Override
    public void editCommunityInfo(String communityName, String description, String publisher, String developer) {
        jdbcTemplate.update("UPDATE community SET description = ?, publisher = ?, developer = ? WHERE name = ?",new Object[]{description, publisher, developer, communityName});
    }

    @Override
    public int findCount(String searchTerms, List<String> categories) {
        QueryBuilder builder = new QueryBuilder().withSearchTerm(searchTerms).withCategories(categories);
        return builder.buildCount();

    }


}

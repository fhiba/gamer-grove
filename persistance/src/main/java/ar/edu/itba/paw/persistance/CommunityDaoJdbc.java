package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.models.Community;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.*;

@Repository
public class CommunityDaoJdbc implements CommunityDao{

    private static final RowMapper<Community> ROW_MAPPER = (rs, rowNum) -> {
            Community community = new  Community(rs.getLong("id"),
            rs.getString("name"),
            rs.getString("description"));
            community.setPortrait_id(rs.getLong("portrait_id"));
            return community;
    };


    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;
    private final SimpleJdbcInsert jdbcInsertCategory;

    @Autowired
    public CommunityDaoJdbc(final DataSource ds){
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(ds).usingGeneratedKeyColumns("id").withTableName("community");
        jdbcInsertCategory =  new SimpleJdbcInsert(ds).withTableName("communities_categories");
    }

    @Override
    public Optional<Community> findById(long id) {
        final String query = "SELECT * FROM community WHERE id = ?";
        final Object[] args = {id};
        final List<Community> list = jdbcTemplate.query(query, args, ROW_MAPPER);
        return list.stream().findFirst();
    }

    @Override
    public Community createCommunity(String name, String description) {
        Map<String, Object> args = new HashMap<>();
        args.put("name", name);
        args.put("description", description);
        long id = jdbcInsert.executeAndReturnKey(args).longValue();
        return new Community(id, name,  description);
    }

    @Override
    public List<Community> findAllCommunities() {
          return jdbcTemplate.query("SELECT * FROM community", ROW_MAPPER);
    }

    @Override
    public Optional<Community> findByName(String communityName) {
        return jdbcTemplate.query("SELECT * FROM community WHERE name = ?", new Object[]{communityName}, ROW_MAPPER).stream().findFirst();
    }

    @Override
    public List<String> getCategoriesOfCommunity(long id) {
       return jdbcTemplate.query("SELECT category FROM communities_categories WHERE community_id = ?", new Object[]{id}, (rs, rowNum) -> rs.getString("category"));
    }

    @Override
    public List<Community> find(String searchTerms, List<String> categories) {
        QueryBuilder builder = new QueryBuilder().withSearchTerm(searchTerms).withCategories(categories);
        return builder.build();
    }


    @Override
    public Boolean addCategory(long id, String category) {
        Map<String, Object> args= new HashMap<>();
        args.put("community_id", id);
        args.put("category", category);
        return jdbcInsertCategory.execute(args) > 0;
    }

    @Override
    public Boolean removeCategory(long id, String category) {
        return jdbcTemplate.update("DELETE FROM communities_categories WHERE community_id = ? AND category = ?", id, category) > 0;
    }



    private class QueryBuilder {
        private final static String SELECT = "SELECT * FROM community";
        private final static String COUNT_SELECT = "SELECT COUNT(*) FROM community";
        private final static String SEARCH_TERM = " WHERE name ILIKE ?";
        private final static String CATEGORY = " AND community.id IN (SELECT cc.community_id FROM communities_categories as cc";
        private final static String CATEGORY_CONDITION = " WHERE cc.category LIKE ?";
        private final static String SECONDARY_CATEGORY_CONDITION = " OR cc.category LIKE ?";
        private final static String CATEGORY_END = " GROUP BY cc.community_id HAVING COUNT(cc.community_id) = ?)";
        private final static String END = " ORDER BY community.name";
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
            if(searchTerm == null || searchTerm.isEmpty())
                return this;

            this.searchTerm = searchTerm;
            return this;
        }
        public QueryBuilder withCategories(List<String> categories) {
            if(categories == null || categories.isEmpty()) {
                return this;
            }
            this.categories.addAll(categories);
            categoriesCount = categories.size();
            return this;
        }

        List<Community> build() {
            StringBuilder sb = new StringBuilder();
            List<Object> objects = new ArrayList<>();
            sb.append(SELECT);
            sb.append(SEARCH_TERM);
            long categoriesSize = categories.size();
            objects.add("%" + searchTerm + "%");

            if(categoriesCount > 0) {
                sb.append(CATEGORY);
                sb.append(CATEGORY_CONDITION);
                categoriesCount--;
                sb.append(SECONDARY_CATEGORY_CONDITION.repeat(categoriesCount));
                objects.addAll(categories);
                objects.add(categoriesSize);
                sb.append(CATEGORY_END);
            }
            sb.append(END);
            String query = sb.toString();
            return jdbcTemplate.query(query, objects.toArray(), ROW_MAPPER);
        }
    }
}

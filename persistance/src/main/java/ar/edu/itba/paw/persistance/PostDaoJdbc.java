package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.models.GroovyPostHistory;
import ar.edu.itba.paw.models.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.RowMapper;
import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Repository
public class PostDaoJdbc implements PostDao{

    private static final RowMapper<Post> ROW_MAPPER = (rs, rowNum) -> new Post(rs.getLong("id"),
            rs.getString("title"),
            rs.getString("body"),
            rs.getLong("author_id"),
            rs.getString("community_name"),
            rs.getBoolean("media"),
            rs.getLong("media_id"),
            rs.getTimestamp("post_date").toLocalDateTime(),
            rs.getInt("grooviness"),
            rs.getBoolean("deleted"),
            rs.getString("category")
            );

//    private static final RowMapper<GroovyPostHistory> ROW_MAPPER_HISTORY = (rs, rowNum) -> new GroovyPostHistory(rs.getInt("user_id"),
//            rs.getInt("post_id"),
//            rs.getBoolean("groovy_type"));

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;
    private final SimpleJdbcInsert jdbcInsertGroovyHistory;


    @Autowired
    public PostDaoJdbc(final DataSource ds){
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(ds).usingGeneratedKeyColumns("id").withTableName("post");
        jdbcInsertGroovyHistory = new SimpleJdbcInsert(ds).withTableName("groovy_post_history");
    }

    @Override
    public Optional<Post> findById(final long id) {
        final List<Post> list = jdbcTemplate.query("SELECT * FROM post WHERE id = ?",new Object[]{id},ROW_MAPPER);
        return list.stream().findFirst();
    }

    @Override
    public List<Post> findAllPosts() {
        return jdbcTemplate.query("SELECT * FROM post ORDER BY post_date DESC",ROW_MAPPER);
    }

    @Override
    public Post createPost(final String title, final String body, final int author_id, final String community_name, final boolean media, final LocalDateTime now, final String category) {
        final Map<String,Object> values = new HashMap<>();
        values.put("title",title);
        values.put("body",body);
        values.put("author_id",author_id);
        values.put("community_name",community_name);
        values.put("media",media);
        values.put("post_date",now);
        values.put("grooviness",0);
        values.put("category", category);
        values.put("deleted",false);
        Number id = jdbcInsert.executeAndReturnKey(values);
        return new Post(id.longValue(), title, body, author_id, community_name, media, 0, now, 0,false, category);
    }

    @Override
    public List<Post> findPostsByCommunity(String communityName) {
        return jdbcTemplate.query("SELECT * FROM post WHERE community_name = ? ORDER BY post_date DESC",new Object[]{communityName},ROW_MAPPER);
    }

    @Override
    public List<Post> findByCategory(String category) {
        return jdbcTemplate.query("SELECT * FROM post WHERE category = ? ORDER BY post_date DESC",new Object[]{category},ROW_MAPPER);
    }

    @Override
    public void editGrooviness(long postId, int i) {
        jdbcTemplate.update("UPDATE post SET grooviness = grooviness + ? WHERE id = ?",i,postId);
    }

    @Override
    public void addToGroovy(long userId, long postId, boolean grooviness) {
        Map<String,Object> values = new HashMap<>();
        values.put("user_id",userId);
        values.put("post_id",postId);
        values.put("groovy_type",grooviness);
        jdbcInsertGroovyHistory.execute(values);
    }

    @Override
    public Optional<Boolean> checkGrooviness(long postId, long userId) {
        return jdbcTemplate.query("SELECT groovy_type FROM groovy_post_history WHERE post_id = ? AND user_id = ?",new Object[]{postId,userId},(rs,rowNum) -> rs.getBoolean("groovy_type")).stream().findFirst();
    }

    @Override
    public void insertIntoGroovyHistory(long postId, long id, boolean grooviness) {
        Map<String,Object> values = new HashMap<>();
        values.put("post_id",postId);
        values.put("user_id",id);
        values.put("groovy_type",grooviness);
        jdbcInsertGroovyHistory.execute(values);
    }

    @Override
    public void deleteGrooviness(long postId, long id) {
        jdbcTemplate.update("DELETE FROM groovy_post_history WHERE post_id = ? AND user_id = ?",postId,id);
    }

    @Override
    public void updateGroovyHistory(long postId, long id, boolean b) {
        jdbcTemplate.update("UPDATE groovy_post_history SET groovy_type = ? WHERE post_id = ? AND user_id = ?",b,postId,id);
    }


}

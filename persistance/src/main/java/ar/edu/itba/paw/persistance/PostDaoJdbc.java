package ar.edu.itba.paw.persistance;

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
            rs.getLong("community_id"),
            rs.getBoolean("media"),
            rs.getLong("media_id"),
            rs.getTimestamp("post_date").toLocalDateTime(),
            rs.getInt("groovines"));

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;


    @Autowired
    public PostDaoJdbc(final DataSource ds){
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(ds).usingGeneratedKeyColumns("id").withTableName("post");
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
    public void createPost(String title, String body, int author_id, int community_id, boolean media, LocalDateTime now) {
        final Map<String,Object> values = new HashMap<>();
        values.put("title",title);
        values.put("body",body);
        values.put("author_id",author_id);
        values.put("community_id",community_id);
        values.put("media",media);
        values.put("post_date",now);
        values.put("groovines",0);
        jdbcInsert.executeAndReturnKey(values);
    }
}

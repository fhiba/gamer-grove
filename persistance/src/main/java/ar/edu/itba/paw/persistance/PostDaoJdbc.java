package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.models.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.RowMapper;
import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;


@Repository
public class PostDaoJdbc implements PostDao{

    private static final RowMapper<Post> ROW_MAPPER = (rs, rowNum) -> new Post(rs.getLong("id"),
            rs.getString("title"),
            rs.getString("body"),
            rs.getLong("author_id"),
            rs.getLong("sub_id"),
            rs.getBoolean("media"),
            rs.getLong("media_id"),
            rs.getTimestamp("date").toLocalDateTime(),
            rs.getInt("grooviness"));

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;


    @Autowired
    public PostDaoJdbc(final DataSource ds){
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(ds).usingGeneratedKeyColumns("id").withTableName("users");
    }

    @Override
    public Optional<Post> findById(final long id) {
        final List<Post> list = jdbcTemplate.query("SELECT * FROM post WHERE id = ?",new Object[]{id},ROW_MAPPER);
        return list.stream().findFirst();
    }
}

package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.models.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Repository
public class CommentDaoJdbc implements CommentDao{
    private static final RowMapper<Comment> ROW_MAPPER = (rs, rowNum) -> new Comment(
            rs.getLong("id"),
            rs.getLong("post_id"),
            rs.getString("username"),
            rs.getLong("parent_id"),
            rs.getString("body"),
            rs.getTimestamp("comment_date").toLocalDateTime()
    );
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    @Autowired
    public CommentDaoJdbc(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(ds).usingGeneratedKeyColumns("id").withTableName("comment");
    }

    @Override
    public Comment createComment(long postId, String body, String username, LocalDateTime dateTime, long userId) {
        final Map<String,Object> values = new HashMap<>();
        values.put("post_id",postId);
        values.put("body",body);
        values.put("comment_date",dateTime);
        values.put("author_id",userId);
        values.put("grooviness",0);
        Number id = jdbcInsert.executeAndReturnKey(values);
        return new Comment(id.longValue(),postId,username,-1,body,dateTime);
    }

    @Override
    public List<Comment> getPostComments(long postId) {
        return jdbcTemplate.query("SELECT * FROM comment JOIN users u on u.id = comment.author_id WHERE post_id = ? ORDER BY comment_date DESC",new Object[]{postId},ROW_MAPPER);
    }
}

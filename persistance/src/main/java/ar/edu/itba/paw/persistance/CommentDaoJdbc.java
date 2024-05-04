package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.GroovyCommentHistory;
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
import java.util.Optional;


@Repository
public class CommentDaoJdbc implements CommentDao{
    private static final RowMapper<Comment> ROW_MAPPER = (rs, rowNum) -> new Comment(
            rs.getLong("id"),
            rs.getLong("post_id"),
            rs.getString("username"),
            rs.getLong("parent_id"),
            rs.getString("body"),
            rs.getTimestamp("comment_date").toLocalDateTime(),
            rs.getInt("grooviness"),
            rs.getBoolean("deleted")
    );

    private static final RowMapper<GroovyCommentHistory> ROW_MAPPER_HISTORY = (rs, rowNum) -> new GroovyCommentHistory(
            rs.getInt("comment_id"),
            rs.getInt("user_id"),
            rs.getInt("post_id"),
            rs.getBoolean("grooviness")
    );

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;
    private final SimpleJdbcInsert jdbcInsertHistory;

    @Autowired
    public CommentDaoJdbc(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(ds).usingGeneratedKeyColumns("id").withTableName("comment");
        jdbcInsertHistory = new SimpleJdbcInsert(ds).withTableName("groovy_comment_history");
    }

    @Override
    public Comment createComment(long postId, String body, String username, LocalDateTime dateTime, long userId) {
        final Map<String,Object> values = new HashMap<>();
        values.put("post_id",postId);
        values.put("body",body);
        values.put("comment_date",dateTime);
        values.put("author_id",userId);
        values.put("grooviness",0);
        values.put("deleted",false);
        Number id = jdbcInsert.executeAndReturnKey(values);
        return new Comment(id.longValue(),postId,username,-1,body,dateTime,0,false);
    }

    @Override
    public List<Comment> getPostComments(long postId) {
        return jdbcTemplate.query("SELECT * FROM comment JOIN users u on u.id = comment.author_id WHERE post_id = ? ORDER BY comment_date DESC",new Object[]{postId},ROW_MAPPER);
    }

    @Override
    public Optional<Comment> getCommentById(long commentId) {
        return jdbcTemplate.query("SELECT * FROM comment JOIN users u on u.id = comment.author_id WHERE comment.id = ?",new Object[]{commentId},ROW_MAPPER).stream().findFirst();
    }

    @Override
    public void editGrooviness(long commentId, int i) {
        jdbcTemplate.update("UPDATE comment SET grooviness = grooviness + ? WHERE id = ?",i,commentId);
    }

    @Override
    public Optional<Boolean> getGroovyTypeFromComment(long commentId, long id, long postId) {
        return jdbcTemplate.query("SELECT groovy_type FROM groovy_comment_history WHERE comment_id = ? AND user_id = ? AND post_id = ?",new Object[]{commentId,id,postId},(rs,rowNum) -> rs.getBoolean("groovy_type")).stream().findFirst();
    }

    @Override
    public void insertGroovinessIntoComment(long commentId, long id, long postId, boolean grooviness) {
        Map<String,Object> values = new HashMap<>();
        values.put("comment_id",commentId);
        values.put("user_id",id);
        values.put("post_id",postId);
        values.put("groovy_type",grooviness);
        jdbcInsertHistory.execute(values);
    }

    @Override
    public void deleteGrooviness(long commentId, long id, long postId) {
        System.out.println(
        jdbcTemplate.update("DELETE FROM groovy_comment_history WHERE comment_id = ? AND user_id = ? AND post_id = ?", new Object[]{commentId,id,postId})
        );
    }

    @Override
    public void updateGroovyHistory(long commentId, long id, long postId, boolean grooviness) {
        jdbcTemplate.update("UPDATE groovy_comment_history SET groovy_type = ? WHERE comment_id = ? AND user_id = ? AND post_id = ?",new Object[]{grooviness,commentId,id,postId});
    }

    @Override
    public List<Comment> getGroovedComments(long postId, long id) {
        return jdbcTemplate.query("SELECT comment.*,u.username FROM comment JOIN users u on u.id = comment.author_id JOIN groovy_comment_history ON comment.id = groovy_comment_history.comment_id WHERE comment.post_id = ? AND groovy_comment_history.user_id = ? AND groovy_type = true",new Object[]{postId,id},ROW_MAPPER);
    }

    @Override
    public List<Comment> getDownGroovedComments(long postId, long id) {
        return jdbcTemplate.query("SELECT comment.*,u.username FROM comment JOIN users u on u.id = comment.author_id JOIN groovy_comment_history ON comment.id = groovy_comment_history.comment_id WHERE comment.post_id = ? AND groovy_comment_history.user_id = ? AND groovy_type = false", new Object[]{postId, id}, ROW_MAPPER);
    }

    @Override
    public int deleteComment(long commentId) {
        return jdbcTemplate.update("UPDATE comment SET deleted = true WHERE id = ?",commentId);
    }
}

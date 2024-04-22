package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.models.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class FileDaoJdbc implements FileDao{

    private static final RowMapper<File> FILE_ROW_MAPPER = (rs, rowNum) -> new File(
            rs.getLong("id"),
            rs.getBytes("bytes")
    );

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private SimpleJdbcInsert jdbcInsertPostImage;

    @Autowired
    public FileDaoJdbc(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds).usingGeneratedKeyColumns("id").withTableName("media");
        this.jdbcInsertPostImage = new SimpleJdbcInsert(ds).withTableName("post_images");
    }

    @Override
    public Optional<File> getFile(long imageId) {
        return jdbcTemplate.query("SELECT * FROM media WHERE id = ?", new Object[]{imageId},FILE_ROW_MAPPER).stream().findFirst();
    }

    @Override
    public Optional<File> uploadImage(byte[] file) {
        Map<String,Object> values = new HashMap<>();
        values.put("bytes",file);
        Number image_id = jdbcInsert.executeAndReturnKey(values);
        System.out.println(image_id.longValue());
        return Optional.of(new File(image_id.longValue(),file));
    }

    @Override
    public Optional<File> uploadUserImage(long userId, byte[] file) {
        return Optional.empty();
    }

    @Override
    public Optional<File> updateCommunityImage(long portraidId, byte[] file) {
        return jdbcTemplate.update("UPDATE media SET bytes = ? WHERE id = ?",file,portraidId) == 1 ? Optional.of(new File(portraidId,file)) : Optional.empty();
    }

    @Override
    public void uploadPostImage(long postId, long imageId) {
        Map<String,Object> values = new HashMap<>();
        values.put("post_id",postId);
        values.put("image_id",imageId);
        jdbcInsertPostImage.execute(values);

    }

    @Override
    public Optional<File> updateUserImage(long userIdImage, byte[] imageId) {
        return jdbcTemplate.update("UPDATE media SET bytes = ? WHERE id = ?",imageId,userIdImage) == 1? Optional.of(new File(userIdImage,imageId)) : Optional.empty();

    }
}

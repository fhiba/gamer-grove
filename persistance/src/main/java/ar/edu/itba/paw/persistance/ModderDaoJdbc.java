package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.models.Community;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Repository
public class ModderDaoJdbc implements ModderDao{

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;
    @Autowired
    public ModderDaoJdbc(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds).withTableName("modders");
    }

    @Override
    public int addModder(int userId, int communityId) {
        Map<String, Object> args = new HashMap<>();
        args.put("user_id", userId);
        args.put("community_id", communityId);
        return jdbcInsert.execute(args);
    }

    @Override
    public boolean isModderOfCommunity(int userId, int communityId) {
        return jdbcTemplate.query("SELECT * FROM modders WHERE user_id = ? AND community_id = ?", new Object[]{userId, communityId}, (rs, rowNum) -> rs.getInt("user_id")).stream().findFirst().isPresent();
    }

    @Override
    public int removeModder(int userId, int communityId) {
        return jdbcTemplate.update("DELETE FROM modders WHERE user_id = ? AND community_id = ?", userId, communityId);
    }
}

package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.models.Community;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class CommunityDaoJdbc implements CommunityDao{

    private static final RowMapper<Community> ROW_MAPPER = (rs, rowNum) -> new Community(rs.getLong("id"),
            rs.getString("name"),
            rs.getLong("portrait_id"));

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;


    @Autowired
    public CommunityDaoJdbc(final DataSource ds){
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(ds).usingGeneratedKeyColumns("id").withTableName("community");
    }

    @Override
    public Optional<Community> findById(long id) {
        final String query = "SELECT * FROM community WHERE id = ?";
        final Object[] args = {id};
        final List<Community> list = jdbcTemplate.query(query, args, ROW_MAPPER);
        return list.stream().findFirst();
    }

    @Override
    public void createCommunity(String name) {
        Map<String, Object> args = new HashMap<>();
        args.put("name", name);
        jdbcInsert.execute(args);
    }

    @Override
    public List<Community> findAllCommunities() {
          return jdbcTemplate.query("SELECT * FROM community", ROW_MAPPER);
    }

    @Override
    public Optional<Community> findByName(String communityName) {
        return jdbcTemplate.query("SELECT * FROM community WHERE name = ?", new Object[]{communityName}, ROW_MAPPER).stream().findFirst();
    }
}

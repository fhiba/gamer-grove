package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.models.Community;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

@Repository
public class CommunityDaoImpl implements CommunityDao{

    private static final RowMapper<Community> ROW_MAPPER = (rs, rowNum) -> new Community(rs.getLong("id"),
            rs.getString("name"),
            rs.getLong("portrait_id"));

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;


    @Autowired
    public CommunityDaoImpl(final DataSource ds){
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(ds).usingGeneratedKeyColumns("id").withTableName("users");
    }

    @Override
    public Optional<Community> findById(long id) {
        final String query = "SELECT * FROM sub WHERE id = ?";
        final Object[] args = {id};
        final List<Community> list = jdbcTemplate.query(query, args, ROW_MAPPER);
        return list.stream().findFirst();
    }
}

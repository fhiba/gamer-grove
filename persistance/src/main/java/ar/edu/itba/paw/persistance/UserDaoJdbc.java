package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserDaoJdbc implements UserDao{

    private static final RowMapper<User> ROW_MAPPER = (rs, rowNum) -> new User(rs.getLong("id"),
                                                                                rs.getString("username"),
                                                                                rs.getString("password"),
                                                                                rs.getString("email"));
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;
    @Autowired
    public UserDaoJdbc(final DataSource ds){
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(ds).usingGeneratedKeyColumns("id").withTableName("users");
    }

    @Override
    public Optional<User> findById(final long id) {
        final List<User> list = jdbcTemplate.query("SELECT * FROM users WHERE id = ?",new Object[]{id},ROW_MAPPER);
        return list.stream().findFirst();
    }

    @Override
    public Optional<User> findByUsername(final String username) {
        final List<User> list = jdbcTemplate.query("SELECT * FROM users WHERE username = ?",new Object[]{username},ROW_MAPPER);
        return list.stream().findFirst();
    }


    @Override
    public Optional<User> findByEmail(final String email) {
        final List<User> list = jdbcTemplate.query("SELECT * FROM users WHERE email = ?",new Object[]{email},ROW_MAPPER);
        return list.stream().findFirst();
    }

    @Override
    public User create(final String username, final String email, final String password) {
        final Map<String,Object> userData = new HashMap<>();
        userData.put("username",username);
        userData.put("email",email);
        userData.put("password",password);
        userData.put("owner", false);
        Number id = jdbcInsert.executeAndReturnKey(userData);
        return new User(id.longValue(), username, password, email);
    }
}

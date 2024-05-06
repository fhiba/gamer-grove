package ar.edu.itba.paw.persistance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Optional;
import java.util.Map;

@Repository
public class TokenDaoJdbc implements TokenDao{

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    RowMapper<String> tokenMapper = (rs, rowNum) -> rs.getString("value");
    RowMapper<Long> idMapper = (rs, rowNum) -> rs.getLong("user_id");

    @Autowired
    public TokenDaoJdbc(final DataSource ds){
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(ds).withTableName("token");
    }

    @Override
    public Boolean verifyValidationToken(String token) {
        return jdbcTemplate.query("SELECT value FROM token where value = ? and type = 'Validation'", new Object[]{token}, tokenMapper).stream().findFirst().isPresent();
    }

    @Override
    public Boolean verifyResetToken(String token) {
        return jdbcTemplate.query("SELECT value FROM token where value = ? and type = 'ResetPass'", new Object[]{token}, tokenMapper).stream().findFirst().isPresent();
    }

    @Override
    public Optional<Long> getIdFromToken(String token, String type) {
        return jdbcTemplate.query("SELECT user_id FROM token where value = ? and type = ?", new Object[]{token, type}, idMapper).stream().findFirst();
    }

    @Override
    public Boolean createValidationToken(Long userId, String token) {
        Map<String, Object> map = new HashMap<>();
        map.put("user_id", userId);
        map.put("value", token);
        map.put("type", "Validation");
        return jdbcInsert.execute(map) > 0;
    }

    @Override
    public Boolean createResetToken(Long userId, String token) {
        Map<String, Object> map = new HashMap<>();
        map.put("user_id", userId);
        map.put("value", token);
        map.put("type", "ResetPass");
        return jdbcInsert.execute(map) > 0;
    }

    @Override
    public void deleteValidationTokens(Long userId) {
        jdbcTemplate.update("DELETE FROM token where user_id = ? and type = 'Validation'", new Object[]{userId});
    }

    @Override
    public void deleteResetTokens(Long userId) {
        jdbcTemplate.update("DELETE FROM token where user_id = ? and type = 'ResetPass'", new Object[]{userId});

    }
}

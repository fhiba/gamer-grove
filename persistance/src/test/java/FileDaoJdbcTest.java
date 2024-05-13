import ar.edu.itba.paw.models.File;
import ar.edu.itba.paw.persistance.FileDaoJdbc;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.HexFormat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
public class FileDaoJdbcTest {
    final static byte[] TEST = HexFormat.ofDelimiter(":")
            .parseHex("e0:4f:d0:20:ea:3a:69:10:a2:d8:08:00:2b:30:30:9d");

    @Autowired
    private FileDaoJdbc fileDao;

    @Autowired
    private DataSource ds;

    private JdbcTemplate jdbcTemplate;

    private  SimpleJdbcInsert jdbcInsert;

    private SimpleJdbcInsert jdbcInsertPostImage;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds).usingGeneratedKeyColumns("id").withTableName("media");
        this.jdbcInsertPostImage = new SimpleJdbcInsert(ds).withTableName("post_images");
        fileDao.uploadImage(TEST);
    }
    @After
    public void tearDown() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "media");
    }

    @Test
    public void testUploadImage() {
        final File file = fileDao.uploadImage(TEST).get();
        assertNotNull(file);
        assertEquals(2, JdbcTestUtils.countRowsInTable(jdbcTemplate, "media"));
        assertEquals(TEST, file.getFile());
    }

    @Test
    public void testGetFile() {
        final File file2 = fileDao.getFile(1).get();
        assertNotNull(file2);
        assertEquals(1, file2.getImageId());
    }



}

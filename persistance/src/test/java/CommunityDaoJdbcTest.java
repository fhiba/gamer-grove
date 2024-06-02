import ar.edu.itba.paw.models.Community;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
public class CommunityDaoJdbcTest {

    final static String NAME = "community";
    final static String DESC = "This is a test community";
    final static String Category = "RPG";
    final static String Category4 = "Hack and Slash";
    final static String developer = "falsedeveloper";
    final static String publisher = "falsepub";
    final static LocalDateTime releaseDate = LocalDateTime.now();

//    @Autowired
//    private DataSource ds;
//
//    @Autowired
//    private CommunityDaoJdbc communityDao;
//
//    private JdbcTemplate jdbcTemplate;
//
//
//    @Before
//    public void setUp() {
//        jdbcTemplate = new JdbcTemplate(ds);
//    }
//
//    @Test
//    public void testCreate() {
//        final Community community = communityDao.createCommunity(NAME, DESC, developer, publisher, releaseDate);
//        assertNotNull(community);
//        assertEquals(NAME, community.getName());
//        assertEquals(DESC, community.getDescription());
//        assertEquals(developer, community.getDeveloper());
//        assertEquals(publisher, community.getPublisher());
//        assertEquals(releaseDate, community.getReleaseDate());
//        assertEquals(4, JdbcTestUtils.countRowsInTable(jdbcTemplate, "community"));
//    }
//
//
//
//    @Test
//    public void addCategory() {
//        assertTrue(communityDao.addCategory(1, Category4));
//    }
//
//    @Test(expected = DuplicateKeyException.class)
//    public void addAlreadyOwnedCategory() {
//        communityDao.addCategory(1, Category);
//    }
//
//    @Test
//    public void removeCategory() {
//        assertEquals(true, communityDao.removeCategory(1, Category));
//    }
//
//    @Test
//    public void removeNonExistentCategory() {
//        assertFalse(communityDao.removeCategory(1, Category4));
//    }
//
//    @Test
//    public void getCategoriesOfCommunity() {
//        assertEquals(2, communityDao.getCategoriesOfCommunity(1).size());
//    }
//
//    @Test
//    public void getCategoriesOfNonExistentCommunity() {
//        assertEquals(0, communityDao.getCategoriesOfCommunity(4).size());
//    }

}


import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.persistance.CommunityDaoJdbc;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;

import javax.sql.DataSource;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class CommunityDaoJdbcTest {

    final static String NAME = "community";
    final static String DESC = "This is a test community";
    final static int POPULATED_COMMUNITIES = 2;
    final static String POPULATED_NAME = "test";
    final static String Category = "RPG";
    final static String Category2 = "Shooter";
    final static String Category3 = "Action";
    final static String Category4 = "Hack and Slash";
    @Autowired
    private DataSource ds;

    @Autowired
    private CommunityDaoJdbc communityDao;

    private JdbcTemplate jdbcTemplate;


    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
    }

//    @Test
//    public void testCreate() {
//        final Community community = communityDao.createCommunity(NAME, DESC, developer, publisher, releaseDate);
//        assertNotNull(community);
//        assertEquals(NAME, community.getName());
//        assertEquals(DESC, community.getDescription());
//        assertEquals(2, JdbcTestUtils.countRowsInTable(jdbcTemplate, "community"));
//    }


    @Test
    public void testFindByName() {
        final Optional<Community> community2 = communityDao.findByName(POPULATED_NAME);
        assertTrue(community2.isPresent());
    }

    @Test()
    public void testFindByNameFails() {
        final Optional<Community> community = communityDao.findByName(NAME);
        assertFalse(community.isPresent());
    }

    //Should work, but hsqldb does not support ILIKE, if you change it to LIKE it will pass this tests
    //Nevertheless, the code is correct, as it works as expected.
    @Test
    public void findWithoutTermsOrCategories() {
        assertEquals(POPULATED_COMMUNITIES, communityDao.find(null, null).size());
    }

    //should find two since 2 communities have test in their name
    @Test
    public void findWithNameOnly() {
        assertEquals(2, communityDao.find(POPULATED_NAME, null).size());
    }

    //Should find 1 since only one community has the category Action
    @Test
    public void findActionCategoryOnlyMember() {
        assertEquals(1, communityDao.find(null, List.of(Category3)).size());
    }

    //There are 2 members of RPG in populator
    @Test
    public void findRPGCategoryMembers() {
        assertEquals(2, communityDao.find(null, List.of(Category)).size());
    }

    //Only one community has both RPG and Shooter as categories
    @Test
    public void findRPGAndShooterCategoryMembers() {
        assertEquals(1, communityDao.find(null, List.of(Category, Category2)).size());
    }

    //No community has both RPG and Hack and Slash and Shooter as categories
    @Test
    public void findRPGAndShooterAndHackAndSlashCategoryMembers() {
        assertEquals(0, communityDao.find(null, List.of(Category, Category2, Category4)).size());
    }

    //Only first community has RPG as category and name with test in it
    @Test
    public void findRPGCategoryAndNameWithTest() {
        assertEquals(1, communityDao.find(POPULATED_NAME, List.of(Category)).size());
    }

    @Test
    public void addCategory() {
        assertTrue(communityDao.addCategory(1, Category4));
    }

    @Test(expected = DuplicateKeyException.class)
    public void addAlreadyOwnedCategory() {
        communityDao.addCategory(1, Category);
    }

    @Test
    public void removeCategory() {
        assertEquals(true, communityDao.removeCategory(1, Category));
    }

    @Test
    public void removeNonExistentCategory() {
        assertFalse(communityDao.removeCategory(1, Category4));
    }

    @Test
    public void getCategoriesOfCommunity() {
        assertEquals(2, communityDao.getCategoriesOfCommunity(1).size());
    }

    @Test
    public void getCategoriesOfNonExistentCommunity() {
        assertEquals(0, communityDao.getCategoriesOfCommunity(4).size());
    }

}


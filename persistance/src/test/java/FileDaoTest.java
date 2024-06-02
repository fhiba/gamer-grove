import ar.edu.itba.paw.models.File;
import ar.edu.itba.paw.persistance.FileDaoJpa;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.OrderWith;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.event.annotation.BeforeTestExecution;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigInteger;
import java.util.HexFormat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
@Rollback
public class FileDaoTest {

    @Autowired
    private FileDaoJpa fileDao;

    @PersistenceContext
    EntityManager em;


    final static byte[] TEST = HexFormat.ofDelimiter(":")
            .parseHex("e0:4f:d0:20:ea:3a:69:10:a2:d8:08:00:2b:30:30:9d");

    final static byte[] TESTUPDATE = HexFormat.ofDelimiter("")
            .parseHex("e04fd020ea3a6910a2d808002b30309d");



    @Test
    public void testGetFile() {
        File file = fileDao.getFile(10).get();
        assertNotNull(file);
        assertEquals(10, file.getImageId().intValue());
    }

    @Test
    @Rollback
    public void testUploadImage() {
        final File file = fileDao.uploadImage(TEST).get();
        em.flush();
        assertNotNull(file);
        assertEquals(TEST, file.getFile());
    }

    @Test
    @Rollback
    public void testUploadPostImage() {
        fileDao.uploadPostImage(10, 10);
        em.flush();
        assertEquals(1, ((Number) em.createNativeQuery("SELECT COUNT(*) FROM post_images").getSingleResult()).intValue());
    }


}

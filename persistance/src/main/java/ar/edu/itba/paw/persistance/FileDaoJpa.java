package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.models.File;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

@Repository
public class FileDaoJpa implements FileDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<File> getFile(long imageId) {
       return Optional.ofNullable(em.find(File.class, imageId));
    }

    @Override
    public Optional<File> uploadImage(byte[] file) {
        File newFile = new File(file);
        em.persist(newFile);
        return Optional.of(newFile);
    }

    @Override
    public Optional<File> updateCommunityImage(long portraidId, byte[] file) {
        em.createQuery("UPDATE File f SET f.file = :file WHERE f.imageId = :imageId")
                .setParameter("file", file)
                .setParameter("imageId", portraidId)
                .executeUpdate();
        return Optional.of(new File(portraidId, file));
    }

    @Override
    public void uploadPostImage(long postId, long imageId) {
        em.createNativeQuery("INSERT INTO post_images (post_id, image_id) VALUES (:postId, :imageId)")
                .setParameter("postId", postId)
                .setParameter("imageId", imageId)
                .executeUpdate();
    }

    @Override
    public Optional<File> updateUserImage(long userId, byte[] image) {
        em.createQuery("UPDATE File f SET f.file = :file WHERE f.imageId = :imageId")
                .setParameter("file", image)
                .setParameter("imageId", userId)
                .executeUpdate();
        return Optional.of(new File(userId, image));
    }
}

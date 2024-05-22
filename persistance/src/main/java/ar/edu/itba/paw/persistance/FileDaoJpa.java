package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.models.File;
import ar.edu.itba.paw.models.Post;
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
        File newFile = new File();
        em.persist(newFile);
        return Optional.of(newFile);
    }

    @Override
    public Optional<File> updateFile(File file, byte[] bytes) {
        file.setFile(bytes);
        em.merge(file);
        return Optional.of(file);
    }

    @Override
    public void uploadPostImage(long postId, long imageId) {
        em.createNativeQuery("INSERT INTO post_images (post_id, image_id) VALUES (:postId, :imageId)")
                .setParameter("postId", postId)
                .setParameter("imageId", imageId)
                .executeUpdate();
    }

}

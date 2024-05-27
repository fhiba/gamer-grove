package ar.edu.itba.paw.persistance;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.File;
import ar.edu.itba.paw.models.Post;

import java.util.Optional;

public interface FileDao {

    Optional<File> getFile(long imageId);

    Optional<File> uploadImage(byte[] file);

    Optional<File> updateFile(File file , byte[] bytes);

    void uploadPostImage(long postId, long imageId);
}

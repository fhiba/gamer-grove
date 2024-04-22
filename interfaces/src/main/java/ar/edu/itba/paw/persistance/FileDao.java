package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.models.File;

import java.util.Optional;

public interface FileDao {

    Optional<File> getFile(long imageId);

    Optional<File> uploadImage(byte[] file);

    Optional<File> uploadUserImage(long userId, byte[] file);

    Optional<File> updateCommunityImage(long portraidId, byte[] file);


    void uploadPostImage(long postId, long imageId);
}

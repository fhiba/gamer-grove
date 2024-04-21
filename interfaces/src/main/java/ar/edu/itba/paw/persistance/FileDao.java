package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.models.File;

import java.util.Optional;

public interface FileDao {

    Optional<File> getFile(long imageId);

    Optional<File> uploadCommunityImage(long communityId, byte[] file);

    Optional<File> uploadUserImage(long userId, byte[] file);

    Optional<File> updateCommunityImage(long portraidId, byte[] file);


}

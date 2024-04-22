package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.NoLoggedUserException;
import ar.edu.itba.paw.exceptions.NoSuchCommunityException;
import ar.edu.itba.paw.models.File;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface FileService {

    Optional<File> getFile(long imageId);

    Optional<File> uploadCommunityImage(String communityId, MultipartFile file) throws NoSuchCommunityException;

    Optional<File> uploadUserImage(long userId, MultipartFile file) throws NoLoggedUserException;

    void uploadPostImage(MultipartFile file, long id);
}

package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.NoLoggedUserException;
import ar.edu.itba.paw.exceptions.NoSuchCommunityException;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.File;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistance.FileDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

@Transactional(readOnly = true)
@Service
public class FileServiceImpl implements FileService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileServiceImpl.class);

    @Autowired
    private FileDao fd;

    @Autowired
    private CommunityService cs;

    @Autowired
    private UserService us;

    @Override
    public Optional<File> getFile(long imageId) {
        return fd.getFile(imageId);
    }

    @Transactional
    @Override
    public Optional<File> uploadCommunityImage(String communityId, MultipartFile file) throws NoSuchCommunityException {
        Community community = cs.findByName(communityId);
        Optional<File> image;
        if (Objects.isNull(community.getPortrait())) {
            try {
                image = fd.uploadImage(file.getBytes());
                image.ifPresent(value -> cs.updateCommunityImageId(community.getId(), value.getImageId()));
            } catch (IOException e) {
                LOGGER.atError().setMessage("Error uploading image").log();
                throw new RuntimeException(e);
            }
        } else {
            try {
                image = fd.updateFile(community.getPortrait(), file.getBytes());
            } catch (IOException e) {
                LOGGER.atError().setMessage("Error updating image").log();
                throw new RuntimeException(e);
            }
        }
        if (image.isPresent()) {
            LOGGER.atInfo().setMessage("New image {} upload successfully to community {}")
                    .addArgument(() -> image.get().getImageId()).addArgument(communityId).log();
        }
        return image;
    }

    @Transactional
    @Override
    public Optional<File> uploadUserImage(byte[] file) throws NoLoggedUserException {
        Optional<User> maybeUser = us.getLoggedUser();
        if (maybeUser.isEmpty()) {
            LOGGER.atError().setMessage("Error while uploading user image because there is no logged user").log();
            throw new NoLoggedUserException();
        }
        User user = maybeUser.get();
        Optional<File> image;
        if (Objects.isNull(user.getImage())) {
            image = fd.uploadImage(file);
            image.ifPresent(value -> us.updateImage(user, value));
        } else {
            image = fd.updateFile(user.getImage(), file);
        }
        if (image.isPresent()) {
            LOGGER.atInfo().setMessage("New image {} upload successfully to user {}")
                    .addArgument(() -> image.get().getImageId()).addArgument(() -> user.getUsername()).log();
        }
        return image;
    }

    @Transactional
    @Override
    public void uploadPostImage(MultipartFile file, long id) {
        try {
            Optional<File> postImage = fd.uploadImage(file.getBytes());
            if (postImage.isPresent()) {
                fd.uploadPostImage(id, postImage.get().getImageId());
                LOGGER.atInfo().setMessage("New image {} upload successfully to post {}")
                        .addArgument(() -> postImage.get().getImageId()).addArgument(id).log();
            }
        } catch (IOException e) {
            LOGGER.atError().setMessage("Error uploading post image").log();
            throw new RuntimeException(e);
        }
    }
}

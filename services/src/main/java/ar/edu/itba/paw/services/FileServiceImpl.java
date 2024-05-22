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
public class FileServiceImpl implements FileService{

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
        if(Objects.isNull(community.getPortrait())) {
            try {
                image = fd.uploadImage(file.getBytes());
                //TODO MAYBE CAN BE REFACTORED
                image.ifPresent(value -> cs.updateCommunityImageId(community.getId(), value.getImageId()));
            } catch (IOException e) {
                LOGGER.debug("Error uploading image",e);
                throw new RuntimeException(e);
            }
        } else {
            try {
                image = fd.updateFile(community.getPortrait(), file.getBytes());
            } catch (IOException e) {
                LOGGER.debug("Error updating image",e);
                throw new RuntimeException(e);
            }
        }

        return image;
    }

    @Transactional
    @Override
    public Optional<File> uploadUserImage(MultipartFile file) throws NoLoggedUserException {
        Optional<User> maybeUser = us.getLoggedUser();
        if(maybeUser.isEmpty() ){
            throw new NoLoggedUserException("No user logged in");
        }
        User user = maybeUser.get();

        Optional<File> image;
        if(Objects.isNull(user.getImage())) {
            try {
                image = fd.uploadImage(file.getBytes());
                //TODO MAYBE CAN BE REFACTORED
                image.ifPresent(value -> us.updateImageId(user.getId(), value.getImageId()));
            } catch (IOException e) {
                LOGGER.debug("Error uploading image",e);
                throw new RuntimeException(e);
            }
        } else {
            try {
                image = fd.updateFile(user.getImage(), file.getBytes());
            } catch (IOException e) {
                LOGGER.debug("Error updating image",e);
                throw new RuntimeException(e);
            }
        }
        return image;
    }

    @Transactional
    @Override
    public void uploadPostImage(MultipartFile file, long id) {
        try {
            Optional<File> postImage = fd.uploadImage(file.getBytes());
            postImage.ifPresent(value -> fd.uploadPostImage(id, value.getImageId()));
        } catch (IOException e) {
            LOGGER.debug("Error uploading post image",e);
            throw new RuntimeException(e);
        }
    }
}

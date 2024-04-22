package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.NoLoggedUserException;
import ar.edu.itba.paw.exceptions.NoSuchCommunityException;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.File;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistance.FileDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import javax.imageio.IIOException;
import java.io.IOException;
import java.util.Optional;
@Service
public class FileServiceImpl implements FileService{

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

    @Override
    public Optional<File> uploadCommunityImage(String communityId, MultipartFile file) throws NoSuchCommunityException {
        Community UpdateProfileImage = cs.findByName(communityId);
        Optional<File> image;
        if (UpdateProfileImage.getPortrait_id() == 0) {
            try {
                image = fd.uploadImage(file.getBytes());
                if(image.isPresent())
                    cs.updateCommunityImageId(UpdateProfileImage.getId(),image.get().getImageId());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                image = fd.updateCommunityImage(UpdateProfileImage.getPortrait_id(), file.getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
       //No se debe cambiar el portraid id una vez creado ya que se lo actualiza en la tabla de images

        return image;
    }

    @Override
    public Optional<File> uploadUserImage(MultipartFile file) throws NoLoggedUserException {
        Optional<User> UpdateProfileImageMaybe = us.getLoggedUser();
        if(UpdateProfileImageMaybe.isEmpty() ){
            throw new NoLoggedUserException("No user logged in");
        }
        User UpdateProfileImage = UpdateProfileImageMaybe.get();
        Optional<File> image= Optional.empty();
        if (UpdateProfileImage.getPortraid_id() == 0) {
            try {
                image = fd.uploadImage(file.getBytes());
                if(image.isPresent())
                    us.updateImageId(UpdateProfileImage.getId(),image.get().getImageId());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                image = fd.updateUserImage(UpdateProfileImage.getPortraid_id(), file.getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return image;
    }

    @Override
    public void uploadPostImage(MultipartFile file, long id) {
        try {
            Optional<File> postImage = fd.uploadImage(file.getBytes());
            if(postImage.isPresent())
                fd.uploadPostImage(id, postImage.get().getImageId());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.NoSuchCommunityException;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.File;
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
    @Override
    public Optional<File> getFile(long imageId) {
        return fd.getFile(imageId);
    }

    @Override
    public Optional<File> uploadCommunityImage(String communityId, MultipartFile file) throws NoSuchCommunityException {
        Community UpdateProfileImage = cs.findByName(communityId);
        Optional<File> image;
        if (UpdateProfileImage.getPortrait_id() == -1) {
            try {
                image = fd.uploadCommunityImage(UpdateProfileImage.getId(), file.getBytes());
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

        return image;
    }

    @Override
    public Optional<File> uploadUserImage(long userId,MultipartFile file) {
        return Optional.empty();
    }
}

package ar.edu.itba.paw.services;

import ar.edu.itba.paw.persistance.PostDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostServiceImpl implements PostService{

    @Autowired
    private PostDao postDao;


}

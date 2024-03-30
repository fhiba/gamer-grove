package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.models.Post;

import java.util.Optional;

public interface PostDao {

    Optional<Post> findById(final long id);

}

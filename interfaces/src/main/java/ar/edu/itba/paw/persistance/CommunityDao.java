package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.models.Community;

import java.util.Optional;

public interface CommunityDao {

    Optional<Community> findById(final long id);

}

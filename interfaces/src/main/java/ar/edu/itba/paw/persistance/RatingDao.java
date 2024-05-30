package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.Rating;
import ar.edu.itba.paw.models.User;

import java.util.Optional;

public interface RatingDao {

    Rating createRating(User user, Community community, Float rating);

    Optional<Rating> getRatingById(User user, Community community);
}

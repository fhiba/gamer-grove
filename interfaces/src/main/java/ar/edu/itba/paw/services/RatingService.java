package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.Rating;
import ar.edu.itba.paw.models.User;

import java.util.Optional;

public interface RatingService {

    Rating createRating(User user, Community community, Float rating);

    Optional<Rating> getRatingById(User user, Community community);

    Boolean deleteRating(User user, Community community);
}

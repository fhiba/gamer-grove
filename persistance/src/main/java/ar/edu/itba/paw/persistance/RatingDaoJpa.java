package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.Rating;
import ar.edu.itba.paw.models.User;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

@Repository
@Primary
public class RatingDaoJpa implements RatingDao{

    @PersistenceContext
    private EntityManager em;

    @Override
    public Rating createRating(User user, Community community, Float rating) {
        Rating newRating = new Rating(user, community, rating);
        em.persist(newRating);
        return newRating;
    }

    @Override
    public Optional<Rating> getRatingById(User user, Community community) {
        Optional<Rating> rating = Optional.ofNullable(em.find(Rating.class, new Rating.RatingKey(user, community)));
        return rating;
    }
}

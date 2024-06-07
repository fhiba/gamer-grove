package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.Rating;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistance.RatingDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class RatingServiceImpl implements RatingService{

    @Autowired
    private RatingDao ratingDao;
    @Override
    @Transactional
    public Rating createRating(User user, Community community, Float rating) {
        return ratingDao.createRating(user, community, rating);
    }

    @Override
    public Optional<Rating> getRatingById(User user, Community community) {
        return ratingDao.getRatingById(user, community);
    }

    @Override
    public Boolean deleteRating(User user, Community community) {
        Optional<Rating> maybeRating = getRatingById(user,community);
        if(maybeRating.isPresent()) {
            ratingDao.deleteRating(maybeRating.get());
            return true;
        }
        return false;
    }
}

package ar.edu.itba.paw.models;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "ratings")
@IdClass(Rating.RatingKey.class)
public class Rating {

    @Id
    @ManyToOne(targetEntity = User.class)
    private User user;

    @Id
    @ManyToOne(targetEntity = Community.class)
    private Community community;
    @Column(name = "rating", nullable = false)
    private Float rating;

    public Rating(User user, Community community, Float rating) {
        this.user = user;
        this.community = community;
        this.rating = rating;
    }

    public Rating() {
        // Hibernate only
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Community getCommunity() {
        return community;
    }

    public void setCommunity(Community community) {
        this.community = community;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public static class RatingKey implements Serializable {
        private User user;
        private Community community;

        public RatingKey() {
            // Hibernate only
        }

        public RatingKey(User user, Community community) {
            this.user = user;
            this.community = community;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            RatingKey ratingKey = (RatingKey) o;
            return Objects.equals(user.getId(), ratingKey.user.getId())
                    && Objects.equals(community.getId(), ratingKey.community.getId());
        }

        @Override
        public int hashCode() {
            return Objects.hash(user, community);
        }
    }
}

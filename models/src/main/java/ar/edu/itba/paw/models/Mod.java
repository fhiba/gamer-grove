package ar.edu.itba.paw.models;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "modders")
@IdClass(Mod.ModKey.class)
public class Mod {

    @Id
    @ManyToOne(targetEntity = User.class)
    private User user;

    @Id
    @ManyToOne(targetEntity = Community.class)
    private Community community;

    @Column(name = "since_date")
    private LocalDateTime sinceDate;

    public Mod(){

    }

    public Mod(User user, Community community, LocalDateTime sinceDate) {
        this.user = user;
        this.community = community;
        this.sinceDate = sinceDate;
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

    public LocalDateTime getSinceDate() {
        return sinceDate;
    }

    public void setSinceDate(LocalDateTime sinceDate) {
        this.sinceDate = sinceDate;
    }

    public static class ModKey implements Serializable{
        private User user;
        private Community community;

        public ModKey(){}
        public ModKey(User user, Community community) {
            this.user = user;
            this.community = community;
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ModKey modKey = (ModKey) o;
            return Objects.equals(user.getId(), modKey.user.getId()) && Objects.equals(community.getId(), modKey.community.getId());
        }

        @Override
        public int hashCode() {
            return Objects.hash(user, community);
        }
    }
}

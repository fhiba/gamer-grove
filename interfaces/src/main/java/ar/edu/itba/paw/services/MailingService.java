package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.User;
import org.springframework.scheduling.annotation.Async;

import java.time.LocalDateTime;
import java.util.List;

public interface MailingService {

    @Async
    void sendNewPostNotifications(List<User> to, Post post, User postAuthor);

    @Async
    void sendNewCommentNotification(User to, Post post, LocalDateTime date);
}

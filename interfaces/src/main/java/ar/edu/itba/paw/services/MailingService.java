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

    @Async
    void sendResetPasswordEmail(String to, String name, String token);

    @Async
    void sendValidationEmail(String to, String name, String token);

    @Async
    void notifyPostDeletion(String to, String name, Long postId, String postTitle, String communityName);

    @Async
    void notifyCommentDeletion(String to, String name, Long postId, String postTitle, String communityName, String commentBody);

    @Async
    void notifyNewModerator(String to, String name, String communityName, String communityEncoded);

    @Async
    void notifyRemovedModerator(String to, String name, String communityName);
}

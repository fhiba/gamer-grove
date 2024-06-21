package ar.edu.itba.paw.services;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;
import javax.mail.MessagingException;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;


@Service
@PropertySource("classpath:mail.properties")
public class MailingServiceImpl implements MailingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailingServiceImpl.class);


    @Value("${MAIL_PROTOCOL}")
    private String MAIL_PROTOCOL;
    @Value("${MAIL_USERNAME}")
    private String MAIL_USERNAME;
    @Value("${MAIL_PASSWORD}")
    private String MAIL_PASSWORD;
    @Value("${MAIL_HOST}")
    private String MAIL_HOST;
    @Value("${MAIL_PORT}")
    private String MAIL_PORT;
    @Value("${MAIL_HAS_AUTH}")
    private String MAIL_HAS_AUTH;
    @Value("${MAIL_STARTTLS_ENABLE}")
    private String MAIL_STARTTLS_ENABLE;
    @Value("${MAIL_DEBUG}")
    private String MAIL_DEBUG;

    @Autowired
    private MessageSource messageSource;

    private final static String devbase = "http://localhost:8080/";
    private final static String base = "http://pawserver.it.itba.edu.ar/paw-2024a-09/";

    Properties getProps() {
        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", MAIL_HOST);    // host
        properties.put("mail.smtp.port", MAIL_PORT);// port
        properties.put("mail.smtp.auth", MAIL_HAS_AUTH);// auth
        properties.put("mail.smtp.starttls.enable",MAIL_STARTTLS_ENABLE ); //TLS

        return properties;
    }


    @Autowired
    private SpringTemplateEngine thymeleafTemplateEngine;

    private void sendHtmlMessage(String to, String subject, String htmlBody, File attachment) {
        Session session = Session.getInstance(getProps(), new javax.mail.Authenticator(){
            protected PasswordAuthentication getPasswordAuthentication(){
                return new PasswordAuthentication(MAIL_USERNAME,MAIL_PASSWORD);
            }
        });
        try {
            MimeMessage message = new MimeMessage(session);
            MimeMessageHelper helper = new MimeMessageHelper(message, false);
            helper.setTo(to);
            helper.setSubject(subject);

            if(!Objects.isNull(htmlBody)) {
                helper.setText(htmlBody, true);
            }
            Transport.send(helper.getMimeMessage());

        }catch (MessagingException e) {
            LOGGER.atWarn().setMessage("Error sending email: {}").addArgument(()->e.getMessage()).log();
        }
        LOGGER.atInfo().setMessage("Mail sent successfully, subject: {}").addArgument(subject).log();
    }

    @Async
    @Override
    public void sendNewPostNotifications(List<User> to, Post post, User postAuthor) {
       to.forEach(receiver -> {
           sendNewPostNotification(receiver.getEmail(), receiver.getUsername(), post, postAuthor, Locale.of(receiver.getLocale()));
       });
    }

    @Override
    public void sendNewCommentNotification(User to, Post post, LocalDateTime date, Locale locale) {
        Map<String, Object> vars = new HashMap<>();
        vars.put("date", date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        vars.put("username", to.getUsername());
        vars.put("base", base);
        vars.put("post_id", post.getId());
        vars.put("post_title", post.getTitle());
        Context thymeleafContext = new Context();
        thymeleafContext.setVariables(vars);
        thymeleafContext.setLocale(locale);
        String htmlBody = thymeleafTemplateEngine.process("postCommentNotification", thymeleafContext);
        sendHtmlMessage(to.getEmail(), messageSource.getMessage("email.newCommentPostNotification.subject", null, locale), htmlBody, null);

    }

    private void sendNewPostNotification(String to, String name, Post post, User postAuthor, Locale locale) {
        Map<String,Object> vars = new HashMap<>();
        vars.put("community", post.getcommunity().getName());
        vars.put("username", name);
        vars.put("post_title", post.getTitle());
        vars.put("post_id", post.getId());
        vars.put("post_author", postAuthor.getUsername());
        vars.put("base", base);
        Context thymeleafContext = new Context();
        thymeleafContext.setVariables(vars);
        thymeleafContext.setLocale(locale);
        String htmlBody = thymeleafTemplateEngine.process("postNotification", thymeleafContext);
        sendHtmlMessage(to, messageSource.getMessage("email.newPostNotification.subject",new Object[] {thymeleafContext.getVariable("community")}, locale), htmlBody, null);
    }

    @Async
    @Override
    public void sendResetPasswordEmail(String to, String name, String token, Locale locale) {
        Map<String, Object> vars = new HashMap<>();
        vars.put("username", name);
        vars.put("token", token);
        vars.put("base", base);
        Context thymeleafContext = new Context();
        thymeleafContext.setVariables(vars);
        thymeleafContext.setLocale(locale);
        String htmlBody = thymeleafTemplateEngine.process("resetPassword", thymeleafContext);
        sendHtmlMessage(to, messageSource.getMessage("email.resetPassword.subject", null, locale), htmlBody, null);
    }

    @Async
    @Override
    public void sendValidationEmail(String to, String name, String token, Locale locale) {
        Map<String, Object> vars = new HashMap<>();
        vars.put("username", name);
        vars.put("token", token);
        vars.put("base", base);
        Context thymeleafContext = new Context();
        thymeleafContext.setVariables(vars);
        thymeleafContext.setLocale(locale);
        String htmlBody = thymeleafTemplateEngine.process("verifyAccount", thymeleafContext);
        sendHtmlMessage(to, messageSource.getMessage("email.validateAccount.subject", null, locale), htmlBody, null);
    }

    @Async
    @Override
    public void notifyPostDeletion(String to, String name, Long postId, String postTitle, String communityName, Locale locale) {

        Map<String, Object> vars = new HashMap<>();
        vars.put("username", name);
        vars.put("post_title", postTitle);
        vars.put("community", communityName);
        vars.put("post_id", postId);
        vars.put("base", base);
        Context thymeleafContext = new Context();
        thymeleafContext.setVariables(vars);

        thymeleafContext.setLocale(locale);
        String htmlBody = thymeleafTemplateEngine.process("postDeletion", thymeleafContext);
        sendHtmlMessage(to, messageSource.getMessage("email.postDeletion.subject", null, locale), htmlBody, null);
    }

    @Async
    @Override
    public void notifyCommentDeletion(String to, String name,  Long postId, String postTitle, String communityName, String commentBody, Locale locale) {
        Map<String, Object> vars = new HashMap<>();
        vars.put("username", name);
        vars.put("post_title", postTitle);
        vars.put("community", communityName);
        vars.put("comment_body", commentBody);
        vars.put("post_id", postId);
        vars.put("base", base);
        Context thymeleafContext = new Context();
        thymeleafContext.setVariables(vars);
        thymeleafContext.setLocale(locale);
        String htmlBody = thymeleafTemplateEngine.process("commentDeletion", thymeleafContext);
        sendHtmlMessage(to, messageSource.getMessage("email.commentDeletion.subject", null, locale), htmlBody, null);
    }

    @Async
    @Override
    public void notifyNewModerator(String to, String name, String communityName, String communityEncoded, Locale locale) {
        Map<String, Object> vars = new HashMap<>();
        vars.put("username", name);
        vars.put("community", communityName);
        vars.put("base", base);
        vars.put("community_encoded", communityEncoded);
        Context thymeleafContext = new Context();
        thymeleafContext.setVariables(vars);
        thymeleafContext.setLocale(locale);
        String htmlBody = thymeleafTemplateEngine.process("newModNotification", thymeleafContext);
        sendHtmlMessage(to, messageSource.getMessage("email.newModNotification.subject", null, locale), htmlBody, null);
    }

    @Async
    @Override
    public void notifyRemovedModerator(String to, String name, String communityName, Locale locale) {
        Map<String, Object> vars = new HashMap<>();
        vars.put("username", name);
        vars.put("community", communityName);
        vars.put("base", base);
        Context thymeleafContext = new Context();
        thymeleafContext.setVariables(vars);
        thymeleafContext.setLocale(locale);
        String htmlBody = thymeleafTemplateEngine.process("removeModNotification", thymeleafContext);
        sendHtmlMessage(to, messageSource.getMessage("email.removeModNotification.subject", null, locale), htmlBody, null);
    }
}

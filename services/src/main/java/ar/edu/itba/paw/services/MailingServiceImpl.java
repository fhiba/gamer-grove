package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.User;
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

    private final static String base = "http://localhost:8080/";
    private final static String prodBase = "http://pawserver.it.itba.edu.ar/paw-2024a-09/";

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
            System.out.println("Error sending email");
            System.out.println(e.getMessage());
        }
    }

    @Async
    @Override
    public void sendNewPostNotifications(List<User> to, Post post, User postAuthor) {
       to.forEach(receiver -> {
           sendNewPostNotification(receiver.getEmail(), receiver.getUsername(), post, postAuthor);
       });
    }

    @Override
    public void sendNewCommentNotification(User to, Post post, LocalDateTime date) {
        Map<String, Object> vars = new HashMap<>();
        vars.put("date", date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        vars.put("username", to.getUsername());
        vars.put("base", base);
        vars.put("post_id", post.getId());
        vars.put("post_title", post.getTitle());
        Context thymeleafContext = new Context();
        thymeleafContext.setVariables(vars);
        String htmlBody = thymeleafTemplateEngine.process("postcommentnotification", thymeleafContext);
        sendHtmlMessage(to.getEmail(), messageSource.getMessage("email.newCommentPostNotification.subject", null, Locale.getDefault()), htmlBody, null);

    }

    private void sendNewPostNotification(String to, String name, Post post, User postAuthor) {
        Map<String,Object> vars = new HashMap<>();
        vars.put("community", post.getCommunityName());
        vars.put("username", name);
        vars.put("post_title", post.getTitle());
        vars.put("post_id", post.getId());
        vars.put("post_author", postAuthor.getUsername());
        vars.put("base", base);
        Context thymeleafContext = new Context();

        thymeleafContext.setVariables(vars);

        String htmlBody = thymeleafTemplateEngine.process("postnotification", thymeleafContext);

        sendHtmlMessage(to, messageSource.getMessage("email.newPostNotification.subject",new Object[] {thymeleafContext.getVariable("community")}, Locale.getDefault()), htmlBody, null);
    }

}

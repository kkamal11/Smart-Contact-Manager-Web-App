package com.kamal.scm_app.service.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);


    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.properties.domain_name}")
    private String domainName;

    public void sendEmail(String to, String subject, String body){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to); //can be an array or more
        message.setSubject(subject);
        message.setText(body);
        message.setFrom(domainName);

        try {
            mailSender.send(message);
        }
        catch (MailException mailException){
            System.out.println(mailException.getMessage());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public void sendRegisterEmailWithHtml(String name, String to, String subject, String body ) throws MessagingException {
        logger.info("In EmailService class. Just entered sendRegisterEmailWithHtml function");
        String htmlContent = "Hi %s, <br /><br />" +
                "Welcome to <b>SCM - by Kamal</b>.<br /><br />" +
                "<p>Please click on link below to verify your account and to enable login.</p><br />" +
                body +
                "<br /><br /><br /><br />With regards,<br />" +
                "<p color='blue'>Kamal Kishor</p>";
        htmlContent = String.format(htmlContent, name);

        MimeMessage message = mailSender.createMimeMessage();
        message.setRecipients(MimeMessage.RecipientType.TO, to);
        message.setSubject(subject);
        message.setContent(htmlContent, "text/html; charset=utf-8");
        try {
            logger.info("EmailService.sendRegisterEmailWithHtml ==> Sending mail to {}", to);
            mailSender.send(message);
        }
        catch (MailException mailException){
            System.out.println(mailException.getMessage());
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    public void sendEmailWithHtml(String htmlContent, String to, String subject) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        message.setRecipients(MimeMessage.RecipientType.TO, to);
        message.setSubject(subject);
        message.setContent(htmlContent, "text/html; charset=utf-8");
        try {
            logger.info("EmailService.sendEmailWithHtml ==> Sending mails");
            mailSender.send(message);
        }
        catch (MailException mailException){
            System.out.println(mailException.getMessage());
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
    public void sendEmailWithAttachment(){

    }
}

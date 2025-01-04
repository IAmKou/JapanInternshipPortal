package com.example.jip.services;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Slf4j
@Service
public class EmailServices {

    @Autowired
    private JavaMailSender mailSender;

    private String senderEmail = "csgit47@gmail.com";
    private String senderPassword = "svvuwvdjrbiucehn";

    public String sendVerificationCode(String recipientEmail) {
        if (recipientEmail == null || recipientEmail.trim().isEmpty()) {
            return "ERROR_EMPTY_EMAIL";
        }

        String verifyCode = generateVerifyCode();

        // Set up mail properties
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        // Create the session
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        try {
            // Create the message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
            message.setSubject("Account Verification");
            message.setText("Your verification code is: " + verifyCode);

            // Send the message
            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            return "ERROR_SENDING_EMAIL";
        }

        return verifyCode;
    }

    public String sendEmail(String recipientEmail, String accountUsername, String plainPassword) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
            message.setSubject("Your Account Details");

            String emailBody = String.format(
                    "Your account username: %s\nYour temporary password: %s\n\n" +
                            "Please log in and change your password immediately for security.",
                    accountUsername, plainPassword
            );

            message.setText(emailBody);

            Transport.send(message);
            return "Email sent successfully to " + recipientEmail;
        } catch (MessagingException e) {
            e.printStackTrace();
            return "Failed to send email to " + recipientEmail + ": " + e.getMessage();
        }
    }

    public void sendEmailCreateAssignment(String recipientEmail, String className) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
            message.setSubject("New Assignment");

            String emailBody = String.format(
                    "There is a new assignment in your class: %s\n" +
                            "Go check it out.",
                    className
            );

            message.setText(emailBody);

            log.info("Sending email to: {} for class: {}", recipientEmail, className);
            Transport.send(message);
            log.info("Email sent successfully to: {}", recipientEmail);

        } catch (MessagingException e) {
            log.error("Failed to send email to: {} due to: {}", recipientEmail, e.getMessage());
        }
    }

    public void sendEmailSubmittedAssignment(String recipientEmail, String studentName, String className) {
        // Email configuration
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        // Authenticate
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        try {
            // Create the email content
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
            message.setSubject("Your Account Details");

            // Email body
            String emailBody = String.format(
                    "There is a new submitted assignment in your class: %s\n of: %s\n\n" +
                            "Go check it out.",
                    className, studentName
            );

            message.setText(emailBody);

            // Send the email
            Transport.send(message);

        } catch (MessagingException e) {
            e.printStackTrace();

        }
    }

    public void sendEmailApplication(String recipientEmail) {
        // Email configuration
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        // Authenticate
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        try {
            // Create the email content
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
            message.setSubject("Your Account Details");

            // Email body
            String emailBody = "There is a new application send check it out.";

            message.setText(emailBody);

            // Send the email
            Transport.send(message);

        } catch (MessagingException e) {
            e.printStackTrace();

        }
    }


    private String generateVerifyCode() {
        int code = (int) (Math.random() * 1000000);  // Generates a 6-digit random code
        return String.format("%06d", code);  // Ensure it's always 6 digits
    }
}

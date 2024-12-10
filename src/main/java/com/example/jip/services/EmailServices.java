package com.example.jip.services;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Properties;
@Service
public class EmailServices {

    @Autowired
    private JavaMailSender mailSender;

    private String senderEmail = "csgit47@gmail.com";
    private String senderPassword = "svvuwvdjrbiucehn";

    public String sendVerificationCode(String recipientEmail) {
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
            return null;
        }

        return verifyCode;
    }

    public String sendEmail(String recipientEmail, String accountUsername, String encryptedPassword) {
        // Email configuration
        String fromEmail = "your-email@example.com";
        String emailPassword = "your-email-password"; // Use an app-specific password if necessary
        String smtpHost = "smtp.example.com"; // e.g., smtp.gmail.com
        int smtpPort = 587;

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", smtpHost);
        properties.put("mail.smtp.port", smtpPort);

        // Authenticate
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, emailPassword);
            }
        });

        try {
            // Create the email content
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
            message.setSubject("Your Account Details");

            // Email body
            String emailBody = String.format(
                    "Your account is: %s\nAnd password is: %s\n\n" +
                            "Remember to change your account password after logging in successfully.",
                    accountUsername, encryptedPassword
            );

            message.setText(emailBody);

            // Send the email
            Transport.send(message);
            return "Email sent successfully to " + recipientEmail;
        } catch (MessagingException e) {
            e.printStackTrace();
            return "Failed to send email to " + recipientEmail + ": " + e.getMessage();
        }
    }

    private String generateVerifyCode() {
        int code = (int) (Math.random() * 1000000);  // Generates a 6-digit random code
        return String.format("%06d", code);  // Ensure it's always 6 digits
    }
}

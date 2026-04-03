package com.secure.utils;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailUtil {
    public static void sendOTP(String recipientEmail, String otp) {
        final String username = "sohantirole792@gmail.com";
        final String password = "wsvl mqzy rmno lwjf"; // Use App Password for Gmail

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Your Secure File Download OTP");
            message.setText("Your OTP for downloading the is: " + otp + "\nValid for 5 minutes.");
            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
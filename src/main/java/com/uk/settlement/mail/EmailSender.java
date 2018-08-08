package com.uk.settlement.mail;

import com.uk.settlement.config.domain.Config;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

import static javax.mail.Session.getDefaultInstance;

public class EmailSender {

    private final Config config;

    public EmailSender(final Config config) {
        this.config = config;
    }

    public void sendEmail(String body) {
        String senderUsername = config.emailDetails().senderUsername();
        String senderPassword = config.emailDetails().senderPassword();
        String[] recipientEmailAddresses = config.emailDetails().recipientEmailAddresses();

        Properties props = System.getProperties();
        String host = "smtp.gmail.com";
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.user", senderUsername);
        props.put("mail.smtp.password", senderPassword);
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");

        Session session = getDefaultInstance(props);
        MimeMessage message = new MimeMessage(session);

        try {
            message.setFrom(new InternetAddress(senderUsername));

            InternetAddress[] toAddresses = new InternetAddress[recipientEmailAddresses.length];

            // To get the array of addresses
            for (int i = 0; i < recipientEmailAddresses.length; i++) {
                toAddresses[i] = new InternetAddress(recipientEmailAddresses[i]);
            }

            for (final InternetAddress toAddress : toAddresses) {
                message.addRecipient(Message.RecipientType.TO, toAddress);
            }

            message.setSubject(config.emailDetails().subject());
            message.setText(body);
            Transport transport = session.getTransport("smtp");
            transport.connect(host, senderUsername, senderPassword);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        } catch (MessagingException me) {
            me.printStackTrace();
        }
    }
}

package com.uk.settlement.mail;

import com.uk.settlement.config.domain.Config;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Arrays;
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
            message.setFrom(EmailSender.toInternetAddress(senderUsername));

            Arrays.stream(recipientEmailAddresses)
                    .map(EmailSender::toInternetAddress)
                    .forEach(address -> addRecipient(message, address));

            message.setSubject(config.emailDetails().subject());
            message.setText(body);
            Transport transport = session.getTransport("smtp");
            transport.connect(host, senderUsername, senderPassword);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private void addRecipient(final MimeMessage message, final InternetAddress address) {
        try {
            message.addRecipient(Message.RecipientType.TO, address);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private static InternetAddress toInternetAddress(final String emailAddress) {
        try {
            return new InternetAddress(emailAddress);
        } catch (AddressException e) {
            throw new RuntimeException(e);
        }
    }
}

package unicam.it.idshackhub.service.email.smtp;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.activation.DataHandler;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

import java.util.Properties;

public class SmtpService implements Smtp{
    private final String systemEmail;
    private final String systemPassword;
    private final String smtpHost;

    public SmtpService(){
        Dotenv dotenv = Dotenv.load();
        this.systemEmail = dotenv.get("SYSTEM_EMAIL");
        this.systemPassword = dotenv.get("SYSTEM_PASSWORD");
        this.smtpHost = dotenv.get("SYSTEM_SMTP_HOST");
    }

    @Override
    public void sendEmailWithAttachment(String recipient, String subject, String body,
                                        byte[] attachmentData, String attachmentName) throws Exception {

        Properties prop = new Properties();
        prop.put("mail.smtp.host", smtpHost);
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.auth", "true");

        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(systemEmail, systemPassword);
            }
        });

        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(systemEmail));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
        message.setSubject(subject);

        Multipart multipart = new MimeMultipart("alternative");

        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setContent(body, "text/html; charset=utf-8");
        multipart.addBodyPart(textPart);

        MimeBodyPart calendarPart = new MimeBodyPart();

        calendarPart.setHeader("Content-Class", "urn:content-classes:calendarmessage");

        jakarta.mail.util.ByteArrayDataSource source = new jakarta.mail.util.ByteArrayDataSource(
                attachmentData,
                "text/calendar;charset=UTF-8;method=REQUEST"
        );

        calendarPart.setDataHandler(new DataHandler(source));

        multipart.addBodyPart(calendarPart);

        message.setContent(multipart);
        Transport.send(message);
    }
}

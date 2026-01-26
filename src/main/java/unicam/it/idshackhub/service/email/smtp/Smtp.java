package unicam.it.idshackhub.service.email.smtp;

public interface Smtp {
    void sendEmailWithAttachment(String recipient, String subject, String body,
                                 byte[] attachmentData, String attachmentName) throws Exception;
}

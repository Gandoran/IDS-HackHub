package unicam.it.idshackhub.service.email.smtp;

/**
 * Infrastructure interface for handling email communications.
 * Abstracts the SMTP protocol details to provide a clean way to send
 * messages with binary attachments.
 */
public interface Smtp {
    /**
     * Dispatches an email containing an attachment to a specified recipient.
     * This is typically used to send generated invitations or reports.
     * * @param recipient Destination email address.
     * @param subject Subject line of the email.
     * @param body Main content of the message.
     * @param attachmentData Binary content of the file to attach.
     * @param attachmentName The filename that will be displayed to the recipient.
     */
    void sendEmailWithAttachment(String recipient, String subject, String body,
                                 byte[] attachmentData, String attachmentName) throws Exception;
}

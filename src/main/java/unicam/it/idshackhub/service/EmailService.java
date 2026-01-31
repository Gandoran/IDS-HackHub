package unicam.it.idshackhub.service;

import org.springframework.stereotype.Service;
import unicam.it.idshackhub.model.user.User;
import unicam.it.idshackhub.service.email.icalendar.ICalendarDetails;
import unicam.it.idshackhub.service.email.icalendar.ICalendarGenerator;
import unicam.it.idshackhub.service.email.smtp.SmtpService;

import java.util.concurrent.CompletableFuture;

/**
 * Service component responsible for managing email notifications and calendar invitations.
 * <p>
 * This service handles the generation of iCalendar (.ics) files and orchestrates
 * the transmission of emails with attachments via the internal SMTP service.
 * </p>
 */
@Service
public class EmailService {
    private final SmtpService smtpService;
    private final ICalendarGenerator ICalendarGenerator;

    public EmailService() {
        smtpService = new SmtpService();
        ICalendarGenerator = new ICalendarGenerator();
    }

    /**
     * Asynchronously generates an iCalendar (.ics) attachment and sends an invitation email to the specified user.
     * <p>
     * This method executes the file generation and email transmission task in a separate thread
     * using {@link CompletableFuture}. This ensures that the calling process is not blocked
     * while waiting for the SMTP server response or file generation.
     * </p>
     * <p>
     * Any exceptions occurring during the process are caught and logged to the standard error stream
     * to prevent crashing the background thread.
     * </p>
     *
     * @param event    the object containing the details of the event (title, time, description)
     * needed to generate the calendar file.
     * @param receiver the user who will receive the invitation email.
     */
    public boolean sendEmailWithCalendar(ICalendarDetails event, User receiver) {
        CompletableFuture.runAsync(() -> {
            try {
                byte[] icsData = ICalendarGenerator.generateIcsFile(event,receiver.getEmail());
                String subject = "Invito: " + event.getTitle();
                String body = buildEmailBody(event);
                smtpService.sendEmailWithAttachment(
                        receiver.getEmail(),
                        subject,
                        body,
                        icsData,
                        "invite.ics"
                );
            } catch (Exception e) {
                System.err.println("Error during the sending " + e.getMessage());
            }
        });
        return true;
    }

    private String buildEmailBody(ICalendarDetails event) {
        return "<html><body>" +
                "<h2>Ciao!</h2>" +
                "<p>Sei stato invitato all'evento <b>" + event.getTitle() + "</b>.</p>" +
                "<p>Trovi i dettagli nel calendario in allegato.</p>" +
                "<p><i>Rispondi usando i pulsanti sopra.</i></p>" +
                "</body></html>";
    }
}

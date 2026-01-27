package unicam.it.idshackhub.service;

import org.springframework.stereotype.Service;
import unicam.it.idshackhub.model.hackathon.Hackathon;
import unicam.it.idshackhub.model.message.Message;
import unicam.it.idshackhub.model.user.User;
import unicam.it.idshackhub.model.user.role.permission.Permission;
import unicam.it.idshackhub.service.email.icalendar.ICalendarDetails;
import unicam.it.idshackhub.service.email.icalendar.ICalendarGenerator;
import unicam.it.idshackhub.service.email.smtp.SmtpService;

import java.util.concurrent.CompletableFuture;

import static unicam.it.idshackhub.service.PermissionChecker.checkPermission;

@Service
public class MentorService {
    private final SmtpService smtpService;
    private final ICalendarGenerator ICalendarGenerator;
    private final MessageService messageService;

    public MentorService(MessageService messageService){
        smtpService = new SmtpService();
        ICalendarGenerator = new ICalendarGenerator();
        this.messageService = messageService;

    }

    public void manageRequest(User mentor, Hackathon hackathon, Message message, boolean accept) {
        if (!checkPermission(mentor, Permission.Can_Manage_Help_Request, hackathon)) {
            throw new RuntimeException("Permission denied");
        }
        messageService.processReply(message.getId(), accept, mentor);
    }

    public void sendCallEmail(User mentor, ICalendarDetails event, Hackathon hackathon, User receiver) {
        if (!checkPermission(mentor, Permission.Can_Send_Email, hackathon)) {
            throw new RuntimeException("Permission denied");
        }
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

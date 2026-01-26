package unicam.it.idshackhub.service;

import unicam.it.idshackhub.model.hackathon.Hackathon;
import unicam.it.idshackhub.model.user.User;
import unicam.it.idshackhub.model.user.role.permission.Permission;
import unicam.it.idshackhub.service.email.icalendar.ICalendarDetails;
import unicam.it.idshackhub.service.email.icalendar.ICalendarGenerator;
import unicam.it.idshackhub.service.email.smtp.SmtpService;

import java.util.concurrent.CompletableFuture;

import static unicam.it.idshackhub.service.PermissionChecker.checkPermission;

public class MentorService {
    private final SmtpService smtpService;
    private final ICalendarGenerator ICalendarGenerator;

    public MentorService(){
        smtpService = new SmtpService();
        ICalendarGenerator = new ICalendarGenerator();
    }

    public void inviteUser(User mentor, ICalendarDetails event, Hackathon hackathon, User receiver) {
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

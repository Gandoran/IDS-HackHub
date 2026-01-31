package unicam.it.idshackhub.service;

import org.springframework.stereotype.Service;
import unicam.it.idshackhub.model.hackathon.Hackathon;
import unicam.it.idshackhub.model.message.Message;
import unicam.it.idshackhub.model.user.User;
import unicam.it.idshackhub.model.user.role.permission.Permission;
import unicam.it.idshackhub.service.email.icalendar.ICalendarDetails;

import static unicam.it.idshackhub.service.PermissionChecker.checkPermission;

/**
 * Provides operations related to Mentor-level use cases.
 * <p>
 *     This service manages Help Requests and sends emails to Mentors.
 * </p>
 */
@Service
public class MentorService {
    private final MessageService messageService;
    private final EmailService emailService;

    public MentorService(MessageService messageService, EmailService emailService) {
        this.messageService = messageService;
        this.emailService = emailService;
    }

    /**
     * Manages a Help Request.
     * Can either accept or reject it.
     *
     */
    public Message manageRequest(User mentor, Hackathon hackathon, Message message, boolean accept) {
        if (!checkPermission(mentor, Permission.Can_Manage_Help_Request, hackathon)) {
            throw new RuntimeException("Permission denied");
        }
        return messageService.processReply(message.getId(), accept, mentor);
    }

    /**
     * Sends an email to a given User containing an ICS file with the event details.
     *
     */
    public boolean sendCallEmail(User mentor, ICalendarDetails event, Hackathon hackathon, User receiver) {
        if (!checkPermission(mentor, Permission.Can_Send_Email, hackathon)) {
            throw new RuntimeException("Permission denied");
        }
        return emailService.sendEmailWithCalendar(event, receiver);
    }
}

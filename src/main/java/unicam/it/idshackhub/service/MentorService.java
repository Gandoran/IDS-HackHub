package unicam.it.idshackhub.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import unicam.it.idshackhub.exception.PermissionDeniedException;
import unicam.it.idshackhub.model.hackathon.Hackathon;
import unicam.it.idshackhub.model.message.Message;
import unicam.it.idshackhub.model.user.User;
import unicam.it.idshackhub.model.user.role.permission.Permission;
import unicam.it.idshackhub.repository.HackathonRepository;
import unicam.it.idshackhub.repository.UserRepository;
import unicam.it.idshackhub.service.email.icalendar.ICalendarDetails;

import java.time.LocalDateTime;

import static unicam.it.idshackhub.service.utils.EntityUtils.getEntity;
import static unicam.it.idshackhub.service.utils.PermissionChecker.checkPermission;

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
    private final UserRepository userRepository;
    private final HackathonRepository hackathonRepository;

    public MentorService(MessageService messageService, EmailService emailService, UserRepository userRepository,
                         HackathonRepository hackathonRepository){
        this.messageService = messageService;
        this.emailService = emailService;
        this.userRepository = userRepository;
        this.hackathonRepository = hackathonRepository;
    }

    /**
     * Manages a Help Request.
     * Can either accept or reject it.
     *
     */
    @Transactional
    public Message manageRequest(Long mentorId, Long hackathonId, Long messageId, boolean accept) {
        User mentor = getEntity(userRepository, mentorId, "Mentor");
        Hackathon hackathon = getEntity(hackathonRepository, hackathonId, "Hackathon");
        if (!checkPermission(mentor, Permission.Can_Manage_Help_Request, hackathon)) {
            throw new PermissionDeniedException("Permission denied");
        }
        return messageService.processReply(messageId, accept, mentor, hackathon);
    }

    /**
     * Sends an email to a given User containing an ICS file with the event details.
     *
     */
    public boolean sendCallEmail(Long mentorId, String title, String description, String virtualRoom, LocalDateTime startTime,
                                 LocalDateTime endTime, Long hackathonId, Long receiverId) {
        User mentor  = getEntity(userRepository, mentorId, "Mentor");
        Hackathon hackathon = getEntity(hackathonRepository, hackathonId, "Hackathon");
        User receiver = getEntity(userRepository, receiverId, "Receiver");
        ICalendarDetails event = new ICalendarDetails(title, description, virtualRoom, startTime, endTime);
        if (!checkPermission(mentor, Permission.Can_Send_Email, hackathon)) {
            throw new PermissionDeniedException("Permission denied");
        }
        return emailService.sendEmailWithCalendar(event, receiver);
    }
}

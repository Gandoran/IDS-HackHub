package unicam.it.idshackhub.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import unicam.it.idshackhub.model.hackathon.Hackathon;
import unicam.it.idshackhub.model.message.Message;
import unicam.it.idshackhub.model.message.MessageType;
import unicam.it.idshackhub.model.user.User;
import unicam.it.idshackhub.model.user.role.ContextRole;
import unicam.it.idshackhub.model.user.role.permission.Permission;
import unicam.it.idshackhub.repository.HackathonRepository;
import unicam.it.idshackhub.repository.SubmissionRepository;
import unicam.it.idshackhub.repository.UserRepository;

import static unicam.it.idshackhub.service.EntityUtils.getEntity;
import static unicam.it.idshackhub.service.PermissionChecker.checkPermission;

/**
 * Provides operations related to Organizer-level use cases.
 * <p>
 *     This service implements the use case of inviting a
 *     normal user to join the Hackathon as a Judge or Mentor.
 * </p>
 */
@Service
public class OrganizerService {
    private final MessageService messageService;
    private final UserRepository userRepository;
    private final HackathonRepository hackathonRepository;

    @Autowired
    public OrganizerService(MessageService messageService, UserRepository userRepository,
                            HackathonRepository hackathonRepository) {
        this.messageService = messageService;
        this.userRepository = userRepository;
        this.hackathonRepository = hackathonRepository;
    }


    /**
     * Invites a normal user to join the Hackathon as a Judge or Mentor.
     *
     */
    @Transactional
    public Message inviteStaff(Long organizerId, Long recipientId, Long hackathonId, ContextRole role) {
        User organizer = getEntity(userRepository, organizerId, "Organizer");
        User recipient = getEntity(userRepository, recipientId, "Recipient");
        Hackathon hackathon = getEntity(hackathonRepository, hackathonId, "Hackathon");

        if (role != ContextRole.H_Judge && role != ContextRole.H_Mentor) {
            throw new IllegalArgumentException("Invalid role for invitation. Only Judge or Mentor allowed.");
        }

        if(!checkPermission(organizer, Permission.Can_Invite_Staff, hackathon)){
            throw new RuntimeException("Permission denied: You are not authorized to invite staff.");
        }

        if(!hackathon.isActionAllowed(Permission.Can_Invite_Staff)) {
            throw new RuntimeException("Action not allowed in the current Hackathon phase.");
        }

        if(recipient.getRoleByContext(hackathon).isPresent()) {
            throw new RuntimeException("The user " + recipient.getUsername() + " is already involved in this Hackathon.");
        }

        return messageService.sendStaffInvite(
                organizer,
                recipient,
                MessageType.INVITE_STAFF_REQUEST,
                "You have been invited to join the staff as " + role.name(),
                hackathon.getId(),
                role
        );
    }
}

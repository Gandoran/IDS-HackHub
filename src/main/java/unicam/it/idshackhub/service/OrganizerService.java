package unicam.it.idshackhub.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import unicam.it.idshackhub.exception.InvalidOperationException;
import unicam.it.idshackhub.exception.PermissionDeniedException;
import unicam.it.idshackhub.model.hackathon.Hackathon;
import unicam.it.idshackhub.model.hackathon.state.HackathonStatus;
import unicam.it.idshackhub.model.message.Message;
import unicam.it.idshackhub.model.message.MessageType;
import unicam.it.idshackhub.model.team.HackathonTeam;
import unicam.it.idshackhub.model.user.User;
import unicam.it.idshackhub.model.user.role.ContextRole;
import unicam.it.idshackhub.model.user.role.permission.Permission;
import unicam.it.idshackhub.repository.HackathonRepository;
import unicam.it.idshackhub.repository.SubmissionRepository;
import unicam.it.idshackhub.repository.UserRepository;

import static unicam.it.idshackhub.service.utils.EntityUtils.getEntity;
import static unicam.it.idshackhub.service.utils.PermissionChecker.checkPermission;

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
    private final SubmissionRepository submissionRepository;
    private final PayPalService payPalService;

    @Autowired
    public OrganizerService(MessageService messageService, UserRepository userRepository,
                            HackathonRepository hackathonRepository, SubmissionRepository submissionRepository,
                            PayPalService payPalService) {
        this.messageService = messageService;
        this.userRepository = userRepository;
        this.hackathonRepository = hackathonRepository;
        this.submissionRepository = submissionRepository;
        this.payPalService = payPalService;
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
            throw new PermissionDeniedException("Permission denied: You are not authorized to invite staff.");
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

    /**
     * Proclaims the winner of a Hackathon.
     * The winner is selected through the {@link SubmissionRepository}
     * and the payment is processed through PayPal.
     * <p>
     *     The team with the best vote is declared the winner.
     * </p>
     *
     */
    @Transactional
    public HackathonTeam proclaimWinner(Long organizerId, Long hackathonId) {
        User organizer = getEntity(userRepository, organizerId, "Organizer");
        Hackathon hackathon = getEntity(hackathonRepository, hackathonId, "Hackathon");

        if (!checkPermission(organizer, Permission.Can_Proclaim_Winner, hackathon)) {
            throw new PermissionDeniedException("Permission denied");
        }
        if (!hackathon.isActionAllowed(Permission.Can_Proclaim_Winner)) {
            throw new InvalidOperationException("Hackathon not in the correct state");
        }

        HackathonTeam winnerTeam = submissionRepository.findWinner(hackathon.getId());
        if (winnerTeam == null) throw new InvalidOperationException("No winner found.");

        hackathon.setWinner(winnerTeam);
        hackathon.setStatus(HackathonStatus.ARCHIVED);
        hackathonRepository.save(hackathon);
        try {
            String orderId = payPalService.initiatePayment(hackathon.getPrize(), winnerTeam.getMainTeam().getPayPalAccount());
            payPalService.confirmPayment(orderId);
            hackathon.setStatus(HackathonStatus.ARCHIVED);
            hackathonRepository.save(hackathon);
        } catch (Exception e) {
            throw new InvalidOperationException("Winner selected but Payment failed: " + e.getMessage());
        }
        return winnerTeam;
    }

    /**
     * Forces the Hackathon to update its state.
     */
    public void forceUpdate(Long hackathonId) {
        Hackathon hackathon = hackathonRepository.findById(hackathonId).orElseThrow();
        hackathon.updateState();
        hackathonRepository.save(hackathon);
    }
}

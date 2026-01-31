package unicam.it.idshackhub.service.strategy;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import unicam.it.idshackhub.model.hackathon.Hackathon;
import unicam.it.idshackhub.model.message.ActionStatus;
import unicam.it.idshackhub.model.message.Message;
import unicam.it.idshackhub.model.message.MessageType;
import unicam.it.idshackhub.model.message.StaffInvite;
import unicam.it.idshackhub.model.user.User;
import unicam.it.idshackhub.model.user.assignment.Assignment;
import unicam.it.idshackhub.model.user.role.ContextRole;
import unicam.it.idshackhub.repository.HackathonRepository;
import unicam.it.idshackhub.repository.MessageRepository;
import unicam.it.idshackhub.repository.UserRepository;

/**
 * Strategy for managing staff invitations (Judges and Mentors) for a Hackathon.
 * The business logic handles the addition of the user to the hackathon staff
 * and updates their assignments upon acceptance.
 */
@Component
@RequiredArgsConstructor
public class StaffInvitationStrategy implements MessageStrategy {

    private final HackathonRepository hackathonRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    @Override
    public MessageType getSupportedType() {
        return MessageType.INVITE_STAFF_REQUEST;
    }

    /**
     * Processes the acceptance of a staff invitation.
     * Checks if the user is already involved in the hackathon, assigns the
     * specific role (Judge or Mentor), and persists the updated associations.
     */
    @Override
    @Transactional
    public void executeAccept(Message message) {
        StaffInvite invite = (StaffInvite) message;

        Long hackathonId = invite.getReferenceId();
        User newStaffMember = invite.getRecipient();
        ContextRole role = invite.getRole();

        Hackathon hackathon = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new RuntimeException("Hackathon not found"));

        if (newStaffMember.getRoleByContext(hackathon).isPresent()) {
            executeResponse(message, "Error: You are already involved in this Hackathon.", ActionStatus.REJECTED);
            return;
        }

        if(role == ContextRole.H_Judge){
            addJudge(hackathon, newStaffMember);
        }else if(role == ContextRole.H_Mentor){
            addMentor(hackathon, newStaffMember);
        } else {
            throw new IllegalStateException("Invalid role in invitation: " + role);
        }
        hackathonRepository.save(hackathon);
        executeResponse(message, "You are now a " + role.name() + " for " + hackathon.getTitle(), ActionStatus.ACCEPTED);    }

    /**
     * Processes the rejection of a staff invitation.
     * Notifies the sender that the invitation was declined without making changes to the hackathon staff.
     */
    @Override
    public void executeReject(Message message) {
        executeResponse(message, "You declined the invitation.", ActionStatus.REJECTED);    }

    /**
     * Creates and saves a response message to notify the original sender of the outcome.
     * Maps the status and content to a new message of the opposite type.
     */
    @Override
    public void executeResponse(Message original, String content, ActionStatus status) {
        Message response = new Message(
                original.getRecipient(),
                original.getSender(),
                content,
                original.getType().getOpposite(),
                status,
                original.getReferenceId()
        );
        messageRepository.save(response);
    }

    private void addJudge(Hackathon hackathon, User judge){
        judge.addAssignment(new Assignment(hackathon, ContextRole.H_Judge));
        hackathon.getStaff().setJudge(judge);
        userRepository.save(judge);
    }

    private void addMentor(Hackathon hackathon, User mentor){
        mentor.addAssignment(new Assignment(hackathon, ContextRole.H_Mentor));
        hackathon.getStaff().getMentors().add(mentor);
        userRepository.save(mentor);
    }
}
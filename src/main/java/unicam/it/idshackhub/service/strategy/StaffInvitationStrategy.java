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

    @Override
    @Transactional
    public void executeAccept(Message message) {
        StaffInvite invite = (StaffInvite) message;

        Long hackathonId = invite.getReferenceId();
        User newStaffMember = invite.getRecipient();
        ContextRole role = invite.getRole();

        Hackathon hackathon = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new RuntimeException("Hackathon not found"));

        // Controllo che l'utente non si sia iscritto all'hackathon mentre l'invito è in pending
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

    @Override
    public void executeReject(Message message) {
        executeResponse(message, "You declined the invitation.", ActionStatus.REJECTED);    }

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
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
        Long hackathonId = message.getReferenceId();
        User newStaffMember = message.getRecipient();

        StaffInvite invite = (StaffInvite) message;
        ContextRole role = invite.getRole();
        Hackathon hackathon = hackathonRepository.findByIdRegistration(hackathonId)
                .orElseThrow(() -> new RuntimeException("Hackathon not found"));
        if(role == ContextRole.H_Judge){
            addJudge(hackathon, newStaffMember);
        }else if(role == ContextRole.H_Mentor){
            addMentor(hackathon, newStaffMember);
        }
        hackathonRepository.save(hackathon);
        executeResponse(message, newStaffMember.getUsername() + " has been set as staff member.", ActionStatus.ACCEPTED);
    }

    @Override
    public void executeReject(Message message) {
        executeResponse(message, "Sorry, your request was rejected.", ActionStatus.REJECTED);
    }

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
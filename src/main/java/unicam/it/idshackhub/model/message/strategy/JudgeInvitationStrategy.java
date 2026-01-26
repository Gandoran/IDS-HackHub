package unicam.it.idshackhub.model.message.strategy;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import unicam.it.idshackhub.model.hackathon.Hackathon;
import unicam.it.idshackhub.model.message.ActionStatus;
import unicam.it.idshackhub.model.message.Message;
import unicam.it.idshackhub.model.message.MessageType;
import unicam.it.idshackhub.model.user.User;
import unicam.it.idshackhub.model.user.assignment.Assignment;
import unicam.it.idshackhub.model.user.role.ContextRole;
import unicam.it.idshackhub.repository.HackathonRepository;
import unicam.it.idshackhub.repository.MessageRepository;
import unicam.it.idshackhub.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class JudgeInvitationStrategy implements MessageStrategy {

    private final HackathonRepository hackathonRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    @Override
    public MessageType getSupportedType() {
        return MessageType.INVITE_JUDGE_REQUEST;
    }

    @Override
    @Transactional
    public void executeAccept(Message message) {
        Long hackathonId = message.getReferenceId();
        User newJudge = message.getRecipient();

        Hackathon hackathon = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new RuntimeException("Hackathon not found"));

        newJudge.addAssignment(new Assignment(hackathon, ContextRole.H_Judge));

        if(hackathon.getStaff() != null) {
            hackathon.getStaff().setJudge(newJudge);
        }

        userRepository.save(newJudge);
        hackathonRepository.save(hackathon);

        executeResponse(message, newJudge.getUsername() + " has been set as judge.", ActionStatus.ACCEPTED);
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
}
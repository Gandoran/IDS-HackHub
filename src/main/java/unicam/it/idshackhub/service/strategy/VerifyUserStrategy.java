package unicam.it.idshackhub.service.strategy;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import unicam.it.idshackhub.model.message.ActionStatus;
import unicam.it.idshackhub.model.message.Message;
import unicam.it.idshackhub.model.message.MessageType;
import unicam.it.idshackhub.model.user.User;
import unicam.it.idshackhub.model.user.role.GlobalRole;
import unicam.it.idshackhub.repository.MessageRepository;
import unicam.it.idshackhub.repository.UserRepository;

/**
 * Strategy for managing user verification requests.
 * It elevates a standard user to the Verified User global role upon approval.
 */
@Component
@RequiredArgsConstructor
public class VerifyUserStrategy implements MessageStrategy {

    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    @Override
    public MessageType getSupportedType() {
        return MessageType.VERIFY_USER_REQUEST;
    }

    /**
     * Processes the acceptance of a verification request.
     * Updates the sender's global role to G_VerifiedUser and persists the change in the repository.
     */
    @Override
    @Transactional
    public void executeAccept(Message message) {
        User userToVerify = message.getSender();

        userToVerify.setGlobalRole(GlobalRole.G_VerifiedUser);
        userRepository.save(userToVerify);

        executeResponse(message, "Your verification request was approved!", ActionStatus.ACCEPTED);
    }

    /**
     * Processes the rejection of a verification request.
     * Sends a notification to the user stating that the request was not approved.
     */
    @Override
    public void executeReject(Message message) {
        executeResponse(message, "Your request was rejected.", ActionStatus.REJECTED);
    }

    /**
     * Generates a feedback message for the user regarding their verification status.
     * Persists the response in the message history.
     */
    @Override
    public void executeResponse(Message original, String content, ActionStatus status) {
        Message response = new Message (
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

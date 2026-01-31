package unicam.it.idshackhub.service.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import unicam.it.idshackhub.model.message.ActionStatus;
import unicam.it.idshackhub.model.message.Message;
import unicam.it.idshackhub.model.message.MessageType;
import unicam.it.idshackhub.repository.MessageRepository;

/**
 * Strategy for handling help requests, typically sent by participants.
 * This strategy manages the simple notification flow to confirm or deny
 * assistance, acting as a bridge between the requester and the support staff.
 */
@Component
@RequiredArgsConstructor
public class HelpRequestStrategy implements MessageStrategy {

    private final MessageRepository messageRepository;

    @Override
    public MessageType getSupportedType() {
        return MessageType.HELP_REQUEST;
    }

    /**
     * Processes the acceptance of a help request.
     * Notifies the requester that their plea for assistance has been acknowledged
     * and that a Mentor will be assigned to support them.
     */
    @Override
    public void executeAccept(Message message) {
        executeResponse(message, "Your request has been accepted. A Mentor will contact you soon.", ActionStatus.ACCEPTED);
    }

    /**
     * Processes the rejection of a help request.
     * Informs the sender that the request could not be fulfilled at this time.
     */
    @Override
    public void executeReject(Message message) {
        executeResponse(message, "Your request has been rejected.", ActionStatus.REJECTED);
    }

    /**
     * Encapsulates the creation of the feedback message.
     * Reverses the sender/recipient roles and persists the response with the
     * appropriate status and content.
     */
    @Override
    public void executeResponse(Message original, String content, ActionStatus status){
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

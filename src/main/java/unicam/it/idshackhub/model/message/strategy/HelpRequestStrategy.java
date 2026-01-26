package unicam.it.idshackhub.model.message.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import unicam.it.idshackhub.model.message.ActionStatus;
import unicam.it.idshackhub.model.message.Message;
import unicam.it.idshackhub.model.message.MessageType;
import unicam.it.idshackhub.repository.MessageRepository;

@Component
@RequiredArgsConstructor
public class HelpRequestStrategy implements MessageStrategy {

    private final MessageRepository messageRepository;

    @Override
    public MessageType getSupportedType() {
        return MessageType.HELP_REQUEST;
    }

    @Override
    public void executeAccept(Message message) {
        // TODO integrare sistema calendar
        // Per ora invia solo un messaggio di conferma
        executeResponse(message, "Your request has been accepted. A Mentor will contact you soon.", ActionStatus.ACCEPTED);
    }

    @Override
    public void executeReject(Message message) {
        executeResponse(message, "Your request has been rejected.", ActionStatus.REJECTED);
    }

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

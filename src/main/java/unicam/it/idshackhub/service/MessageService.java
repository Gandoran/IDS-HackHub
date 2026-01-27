package unicam.it.idshackhub.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import unicam.it.idshackhub.model.message.ActionStatus;
import unicam.it.idshackhub.model.message.Message;
import unicam.it.idshackhub.model.message.MessageType;
import unicam.it.idshackhub.model.message.StaffInvite;
import unicam.it.idshackhub.service.strategy.MessageStrategy;
import unicam.it.idshackhub.model.user.User;
import unicam.it.idshackhub.model.user.role.ContextRole;
import unicam.it.idshackhub.model.user.role.permission.Permission;
import unicam.it.idshackhub.repository.MessageRepository;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Handles operations related to {@link Message} entities
 * like sending and processing messages.
 */
@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final Map<MessageType, MessageStrategy> strategyMap;

    @Autowired
    public MessageService(MessageRepository messageRepository, List<MessageStrategy> strategies) {
        this.messageRepository = messageRepository;
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(MessageStrategy::getSupportedType, Function.identity()));
    }

    /**
     * Sends a new message to a recipient.
     * Validates the sender and message type before saving the message.
     *
     * @param sender
     * @param recipient
     * @param type
     * @param content
     * @param referenceId
     */
    @Transactional
    public void sendMessage(User sender, User recipient, MessageType type, String content, Long referenceId) {
        Message message = new Message(
                sender,
                recipient,
                content,
                type,
                ActionStatus.PENDING,
                referenceId
        );
        validateMessageStatus(message);
        validateSender(message, sender);
        validateRecipient(message, recipient);
        messageRepository.save(message);
    }

    /**
     * Sends a new Staff invite message to a recipient.
     * Validates the sender and message type before saving the message.
     *
     * @param sender
     * @param recipient
     * @param type
     * @param content
     * @param referenceId
     */
    @Transactional
    public void sendStaffInvite(User sender, User recipient, MessageType type, String content, Long referenceId, ContextRole role) {
        StaffInvite message = new StaffInvite(
                sender,
                recipient,
                content,
                type,
                ActionStatus.PENDING,
                referenceId,
                role
        );
        validateMessageStatus(message);
        validateSender(message, sender);
        validateRecipient(message, recipient);
        messageRepository.save(message);
    }

    /**
     * Processes a reply to a message.
     * Validates the message status and recipient before executing the reply logic.
     *
     * @param messageId
     * @param accepted
     * @param currentUser
     */
    @Transactional
    public void processReply(Long messageId, boolean accepted, User currentUser) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException("Message not found: " + messageId + "."));
        validateMessageStatus(message);
        validateRecipient(message, currentUser);
        MessageStrategy strategy = strategyMap.get(message.getType());
        if (strategy == null) {
            throw new IllegalStateException("Strategy not found for message type: " + message.getType() + ".");}
        if (accepted)
        {strategy.executeAccept(message);}
        else {strategy.executeReject(message);}
        messageRepository.save(message);
    }

    private void validateRecipient(Message message, User recipient) {
        if (message.getType() == MessageType.VERIFY_USER_REQUEST) {
            if (!PermissionChecker.checkPermission(recipient, Permission.Can_Manage_Verified_Request)) {
                throw new RuntimeException("Permission denied");
            }
        }
        if (!recipient.equals(message.getRecipient())) {
            throw new RuntimeException("Permission denied");
        }
        // TODO Recipient non deve già far parte dell'hackathon
    }

    private void validateSender(Message message, User sender) {
        if(message.getType() == MessageType.VERIFY_USER_REQUEST) {
            if(!PermissionChecker.checkPermission(sender, Permission.Can_Create_Verified_Request)) {
                throw new RuntimeException("Permission denied");
            }
        }
        if (!sender.equals(message.getSender())) {
            throw new RuntimeException("Permission denied");
        }
    }

    private void validateMessageStatus(Message message) {
        if (message.getActionStatus() == ActionStatus.ACCEPTED ||
                message.getActionStatus() == ActionStatus.REJECTED) {
            throw new IllegalStateException("Message already processed: " + message.getId() + ".");
        }
    }
}

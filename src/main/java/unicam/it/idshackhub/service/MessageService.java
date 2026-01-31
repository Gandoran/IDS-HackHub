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

import static unicam.it.idshackhub.service.PermissionChecker.checkPermission;

/**
 * Provides operations related to Message lifecycle management.
 * <p>
 *     This service manages the creation and processing of
 *     messages sent both within and outside an Hackathon context.
 * </p>
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
     * Sends a message to a specific recipient.
     * A message can be of various types, depending on the context
     * (eg. Verification Request, Help Request).
     *
     */
    @Transactional
    public Message sendMessage(User sender, User recipient, MessageType type, String content, Long referenceId) {
        validateMessageConsistency(type, recipient);
        Message message = new Message(
                sender,
                recipient,
                content,
                type,
                ActionStatus.PENDING,
                referenceId
        );
        validateAndSave(message, sender);
        return message;
    }

    /**
     * Sends a Staff Invite to a specific recipient.
     * A Staff Invite can be of two types: Judge Invite and Mentor Invite.
     *
     */
    @Transactional
    public Message sendStaffInvite(User sender, User recipient, MessageType type, String content, Long referenceId, ContextRole role) {
        if (recipient == null) throw new IllegalArgumentException("Staff invites require a recipient.");
        StaffInvite message = new StaffInvite(
                sender,
                recipient,
                content,
                type,
                ActionStatus.PENDING,
                referenceId,
                role
        );
        validateAndSave(message, sender);
        return message;
    }


    /**
     * Processes a reply to a message.
     * This is only for messages that require a reply (eg. Verification Request).
     *
     */
    @Transactional
    public Message processReply(Long messageId, boolean accepted, User currentUser) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException("Message not found: " + messageId));

        validateMessageStatus(message);
        validateAccessToReply(message, currentUser);

        MessageStrategy strategy = strategyMap.get(message.getType());
        if (strategy == null) {
            throw new IllegalStateException("No strategy found for message type: " + message.getType());
        }

        if (accepted) {
            strategy.executeAccept(message);
        } else {
            strategy.executeReject(message);
        }
        return messageRepository.save(message);
    }

    // -- PRIVATE METHODS --

    private void validateAndSave(Message message, User sender) {
        validateMessageStatus(message);
        if (!sender.equals(message.getSender())) {
            throw new RuntimeException("Permission denied: Sender mismatch.");
        }
        messageRepository.save(message);
    }


    private void validateMessageConsistency(MessageType type, User recipient) {
        boolean isBroadcastType = isBroadcastType(type);

        if (isBroadcastType && recipient != null) {
            throw new IllegalArgumentException("Message type " + type + " must NOT have a specific recipient (Broadcast).");
        }
        if (!isBroadcastType && recipient == null) {
            throw new IllegalArgumentException("Message type " + type + " REQUIRES a specific recipient.");
        }
    }


    private void validateAccessToReply(Message message, User currentUser) {
        if (message.getRecipient() != null) {
            if (!currentUser.equals(message.getRecipient())) {
                throw new RuntimeException("Permission denied: You are not the recipient.");
            }
            return;
        }

        Permission requiredPermission = getRequiredPermissionForBroadcast(message.getType());
        if (requiredPermission != null) {
            if (!checkPermission(currentUser, requiredPermission)) {
                throw new RuntimeException("Permission denied: You don't have permission to manage this type of message.");
            }
        }
    }

    private void validateMessageStatus(Message message) {
        if (message.getActionStatus() != ActionStatus.PENDING) {
            throw new IllegalStateException("Message already processed. ID: " + message.getId());
        }
    }


    private boolean isBroadcastType(MessageType type) {
        return switch (type) {
            case VERIFY_USER_REQUEST, HELP_REQUEST -> true;
            default -> false;
        };
    }

    private Permission getRequiredPermissionForBroadcast(MessageType type) {
        return switch (type) {
            case VERIFY_USER_REQUEST -> Permission.Can_Manage_Verified_Request;
            case HELP_REQUEST -> Permission.Can_Manage_Help_Request;
            default -> null;
        };
    }
}
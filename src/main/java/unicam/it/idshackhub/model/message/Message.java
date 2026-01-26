package unicam.it.idshackhub.model.message;

import jakarta.persistence.*;
import lombok.*;
import unicam.it.idshackhub.model.user.User;

/**
 * Represents a message sent between two users.
 * The message can be sent by a user or by the system.
 * The message can represent different specific actions, such as
 * inviting a user to join a specific Team, verifying a user's account, etc.
 */
@Entity
@Getter @Setter @NoArgsConstructor
public class Message {

    /**
     * Unique identifier for the message.
     */
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The user who sent the message.
     */
    @ManyToOne @JoinColumn(name = "sender_id")
    private User sender;

    /**
     * The user who is the recipient of the message (if any).
     */
    @ManyToOne @JoinColumn(name = "recipient_id", nullable = true)
    private User recipient;

    /**
     * The content of the message.
     */
    private String content;

    /**
     * The type of message being sent.
     */
    @Enumerated(EnumType.STRING)
    private MessageType type;

    /*+
     * The status of the action represented by the message, if any.
     */
    @Enumerated(EnumType.STRING)
    private ActionStatus actionStatus;

    /**
     * The ID of the entity (Hackathon, User, etc.) referenced by the message, if any.
     */
    private Long referenceId;

    public Message(User sender, User recipient, String content, MessageType type, ActionStatus actionStatus, Long referenceId) {
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
        this.type = type;
        this.actionStatus = actionStatus;
        this.referenceId = referenceId;
    }

}
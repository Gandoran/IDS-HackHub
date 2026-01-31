package unicam.it.idshackhub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import unicam.it.idshackhub.model.message.ActionStatus;
import unicam.it.idshackhub.model.message.Message;
import unicam.it.idshackhub.model.message.MessageType;

import java.util.List;

/**
 * Spring Data repository for {@link Message} entities.
 */
public interface MessageRepository extends JpaRepository<Message,Long> {

    /**
     * Retrieves all messages matching the specified type and action status where no specific recipient is defined.
     * <p>
     * This method is typically used to fetch broadcast messages, system notifications, or open requests
     * (such as unassigned help requests) that are not directed to a single user but are intended
     * for a general audience or a group of moderators.
     * </p>
     *
     * @param type   the category of the message (e.g., {@code HELP_REQUEST}, {@code NOTIFICATION}).
     * @param status the current processing status of the message (e.g., {@code PENDING}, {@code COMPLETED}).
     * @return a {@link List} of {@link Message} entities that match the given criteria and have a {@code null} recipient.
     */
    List<Message> findAllByTypeAndActionStatusAndRecipientIsNull(MessageType type, ActionStatus status);

}

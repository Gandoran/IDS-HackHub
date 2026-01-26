package unicam.it.idshackhub.model.message.strategy;

import unicam.it.idshackhub.model.message.ActionStatus;
import unicam.it.idshackhub.model.message.Message;
import unicam.it.idshackhub.model.message.MessageType;

public interface MessageStrategy {

    /**
     * Returns the message type supported by this strategy.
     * @return the message type supported by this strategy.
     */
    MessageType getSupportedType();

    /**
     * Executes the acceptance logic for the given message.
     * @param message
     */
    void executeAccept(Message message);

    /**
     * Executes the rejection logic for the given message.
     * @param message
     */
    void executeReject(Message message);

    /**
     * Executes the response logic for the given message.
     * @param message
     * @param content
     * @param status
     */
    void executeResponse(Message message, String content, ActionStatus status);
}
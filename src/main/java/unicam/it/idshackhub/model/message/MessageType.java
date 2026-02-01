package unicam.it.idshackhub.model.message;

/**
 * Lists the different types of messages supported by the application.
 */
public enum MessageType {
    // Richieste Operative
    VERIFY_USER_REQUEST,
    INVITE_STAFF_REQUEST,
    HELP_REQUEST,
    INVITE_USER_REQUEST,
    JOIN_TEAM_REQUEST,

    // Risposte
    VERIFY_USER_RESPONSE,
    INVITE_STAFF_RESPONSE,
    HELP_RESPONSE,
    INVITE_USER_RESPONSE,
    JOIN_TEAM_RESPONSE;

    /**
     * Returns the opposite message type (REQUEST -> RESPONSE).
     * @return the opposite message type.
     */
    public MessageType getOpposite() {
        return switch (this) {
            case VERIFY_USER_REQUEST -> VERIFY_USER_RESPONSE;
            case INVITE_STAFF_REQUEST -> INVITE_STAFF_RESPONSE;
            case HELP_REQUEST -> HELP_RESPONSE;
            case INVITE_USER_REQUEST -> INVITE_USER_RESPONSE;
            case JOIN_TEAM_REQUEST -> JOIN_TEAM_RESPONSE;
            default -> null;
        };
    }
}
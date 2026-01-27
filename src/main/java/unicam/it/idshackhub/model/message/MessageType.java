package unicam.it.idshackhub.model.message;

/**
 * Lists the different types of messages supported by the application.
 */
public enum MessageType {
    // Richieste Operative
    VERIFY_USER_REQUEST,      // Sostituisce la vecchia classe Request
    INVITE_STAFF_REQUEST,  // Un Organizer invita un Giudice o un Mentore
    HELP_REQUEST,           // Un TeamMember di un hackathon chiede aiuto ai Mentor

    // Risposte
    VERIFY_USER_RESPONSE,
    INVITE_STAFF_RESPONSE,
    HELP_RESPONSE;

    /**
     * Returns the opposite message type (REQUEST -> RESPONSE).
     * @return the opposite message type.
     */
    public MessageType getOpposite() {
        return switch (this) {
            case VERIFY_USER_REQUEST -> VERIFY_USER_RESPONSE;
            case INVITE_STAFF_REQUEST -> INVITE_STAFF_RESPONSE;
            case HELP_REQUEST -> HELP_RESPONSE;
            default -> null;
        };
    }
}
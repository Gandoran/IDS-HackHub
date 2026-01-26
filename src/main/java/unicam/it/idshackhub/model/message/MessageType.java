package unicam.it.idshackhub.model.message;

/**
 * Lists the different types of messages supported by the application.
 */
public enum MessageType {
    // Richieste Operative
    VERIFY_USER_REQUEST,      // Sostituisce la vecchia classe Request
    INVITE_JUDGE_REQUEST,  // Un Organizer invita un Judge
    INVITE_MENTOR_REQUEST,  // Un Organizer invita un Mentor
    HELP_REQUEST,           // Un TeamMember di un hackathon chiede aiuto ai Mentor

    // Risposte
    VERIFY_USER_RESPONSE,
    INVITE_JUDGE_RESPONSE,
    INVITE_MENTOR_RESPONSE,
    HELP_RESPONSE;

    /**
     * Returns the opposite message type (REQUEST -> RESPONSE).
     * @return the opposite message type.
     */
    public MessageType getOpposite() {
        return switch (this) {
            case VERIFY_USER_REQUEST -> VERIFY_USER_RESPONSE;
            case INVITE_JUDGE_REQUEST -> INVITE_JUDGE_RESPONSE;
            case INVITE_MENTOR_REQUEST -> INVITE_MENTOR_RESPONSE;
            case HELP_REQUEST -> HELP_RESPONSE;
            default -> null;
        };
    }
}
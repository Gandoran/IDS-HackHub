package unicam.it.idshackhub.model.message;

/**
 * Lists the possible statuses for a message action.
 */
public enum ActionStatus {
    PENDING,    // In attesa di risposta
    ACCEPTED,   // Accettato
    REJECTED,   // Rifiutato
    NONE        // Messaggio informativo (nessuna azione richiesta)
}
package unicam.it.idshackhub.model.user.assignment;

/**
 * Defines the contract for an entity to act as a permission scope.
 * <p>
 * In this system, a "Context" represents a boundary within which a User possesses specific rights.
 * Any object implementing this interface (e.g., a Hackathon event, a Project Team)
 * can have users assigned to it with specific roles.
 * </p>
 */
public interface Context {

    /**
     * Retrieves the unique ID of the context.
     *
     * @return the context ID.
     */
    long getId();
}
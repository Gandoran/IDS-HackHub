package unicam.it.idshackhub.model.user.assignment;

import lombok.Getter;
import lombok.Setter;

/**
 * Abstract base implementation for all context entities.
 * <p>
 * This class provides a standard identification mechanism (ID) for any entity
 * that acts as a scope for user roles, such as {@link unicam.it.idshackhub.model.team.Team}
 * or {@link unicam.it.idshackhub.model.hackathon.Hackathon}.
 * It simplifies the comparison and retrieval of contexts within assignments.
 * </p>
 */
@Setter
@Getter
public abstract class BaseContext implements Context {

    /**
     * The unique identifier for this context entity.
     */
    private long id;
}
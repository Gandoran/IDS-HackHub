package unicam.it.idshackhub.model.user.assignment;

import jakarta.persistence.*;
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
@Entity
@Inheritance(strategy = InheritanceType.JOINED) // Crea una tabella base_context e unisce le tabelle figlie tramite ID
@Table(name = "base_context")
@Getter
@Setter
public abstract class BaseContext implements Context {

    /**
     * The unique identifier for this context entity.
     */
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
}
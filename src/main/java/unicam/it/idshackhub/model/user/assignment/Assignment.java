package unicam.it.idshackhub.model.user.assignment;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import unicam.it.idshackhub.model.user.User;
import unicam.it.idshackhub.model.user.role.*;

/**
 * Represents a specific association between a Context and a Role.
 * <p>
 * This class is the core component of the context-aware permission system.
 * Instead of users having static roles (e.g., "Admin" everywhere), an {@link Assignment}
 * grants a user a specific {@link Role} (e.g., "Team Leader") only within a specific
 * {@link BaseContext} (e.g., "Team Alpha").
 * </p>
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Assignment {

    /**
     * The unique identifier for this Assignment entity.
     */
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private long id;

    /**
     * The specific environment or entity where this assignment is valid (e.g., a specific Hackathon or Team).
     */
    @ManyToOne @JoinColumn(name = "context_id", nullable = false)
    private BaseContext context;


    /**
     * The role granted to the user within the specified context.
     */
    @ManyToOne @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING) private ContextRole role;

    public Assignment(BaseContext context, ContextRole role) {
        this.context = context;
        this.role = role;
    }
}
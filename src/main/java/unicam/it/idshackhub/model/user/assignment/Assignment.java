package unicam.it.idshackhub.model.user.assignment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import unicam.it.idshackhub.model.user.role.Role;

/**
 * Represents a specific association between a Context and a Role.
 * <p>
 * This class is the core component of the context-aware permission system.
 * Instead of users having static roles (e.g., "Admin" everywhere), an {@link Assignment}
 * grants a user a specific {@link Role} (e.g., "Team Leader") only within a specific
 * {@link BaseContext} (e.g., "Team Alpha").
 * </p>
 */
@Getter
@Setter
@AllArgsConstructor
public class Assignment {

    /**
     * The specific environment or entity where this assignment is valid (e.g., a specific Hackathon or Team).
     */
    private BaseContext context;

    /**
     * The role granted to the user within the specified context.
     */
    private Role role;
}
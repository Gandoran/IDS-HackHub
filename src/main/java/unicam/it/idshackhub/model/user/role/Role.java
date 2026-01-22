package unicam.it.idshackhub.model.user.role;

import unicam.it.idshackhub.model.user.role.permission.Permission;

import java.util.List;

/**
 * Defines the contract for any role within the application.
 * <p>
 * This interface abstracts the concept of a "Role" so that the system can treat
 * Global roles, Hackathon roles, and Team roles uniformly. Any implementation of this
 * interface acts as a container for a specific set of {@link Permission}s.
 * </p>
 */
public interface Role {

    /**
     * Retrieves the list of permissions granted by this role.
     *
     * @return a list of {@link Permission} objects.
     */
    List<Permission> getListPermission();

    /**
     * Checks if this role includes a specific permission.
     *
     * @param permission the permission to verify.
     * @return {@code true} if the role grants the permission; {@code false} otherwise.
     */
    boolean hasPermission(Permission permission);
}
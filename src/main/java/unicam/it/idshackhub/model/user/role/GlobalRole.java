package unicam.it.idshackhub.model.user.role;

import unicam.it.idshackhub.model.user.role.permission.Permission;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Defines roles that apply across the entire platform scope.
 * <p>
 * Global roles determine a user's baseline capabilities within the system, independent of
 * specific contexts like teams or events. For example, a {@code G_SystemAdmin} has powers
 * everywhere, while a {@code G_VerifiedUser} has unlocked creation capabilities.
 * </p>
 */
public enum GlobalRole implements Role {

    /**
     * A user identity that has been verified. Can create Hackathons and Teams.
     */
    G_VerifiedUser(EnumSet.of(Permission.Can_Create_Hackathon, Permission.Can_Create_Team)),

    /**
     * The platform administrator. Can manage user requests and system settings.
     */
    G_SystemAdmin(EnumSet.of(Permission.Can_Manage_Request)),

    /**
     * The default role for a newly registered user. Limited basic permissions.
     */
    G_NormalUser(EnumSet.of(Permission.Can_Create_Request, Permission.Can_Create_Team));

    /**
     * The set of permissions associated with this role.
     */
    private final Set<Permission> permissions;

    /**
     * Constructs a role with a defined set of permissions.
     *
     * @param permissions the permissions to grant.
     */
    GlobalRole(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    /**
     * Retrieves the complete list of permissions granted by this role.
     *
     * @return an immutable list of permissions.
     */
    @Override
    public List<Permission> getListPermission() {
        return List.copyOf(this.permissions);
    }

    /**
     * Checks if this role grants a specific permission.
     *
     * @param permission the permission to check.
     * @return {@code true} if allowed, {@code false} otherwise.
     */
    @Override
    public boolean hasPermission(Permission permission) {
        return this.permissions.contains(permission);
    }
}
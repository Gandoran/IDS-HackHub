package unicam.it.idshackhub.model.user.role;

import unicam.it.idshackhub.model.user.role.permission.Permission;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Defines roles specific to the context of a permanent Main Team.
 * <p>
 * These roles govern what actions a user can perform regarding their team management,
 * such as registering the team for an event or creating sub-teams.
 * </p>
 */
public enum TeamRole implements Role {

    /**
     * A standard member of the team.
     * Currently grants no administrative permissions within the team structure.
     */
    T_TeamMember(EnumSet.noneOf(Permission.class)),

    /**
     * The leader/manager of the team.
     * Has permissions to register the team for events and create hackathon sub-teams.
     */
    T_TeamLeader(EnumSet.of(Permission.Can_Register_Team, Permission.Can_Create_HackathonTeam));

    /**
     * The set of permissions associated with this role.
     */
    private final Set<Permission> permissions;

    /**
     * Constructs a role with a defined set of permissions.
     *
     * @param permissions the permissions to grant.
     */
    TeamRole(Set<Permission> permissions) {
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
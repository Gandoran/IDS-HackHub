package unicam.it.idshackhub.model.user.role;

import unicam.it.idshackhub.model.user.role.permission.Permission;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Defines roles specific to the context of a Hackathon event.
 * <p>
 * These roles are only active and valid when the user is interacting with the specific
 * {@link unicam.it.idshackhub.model.hackathon.Hackathon} entity they are assigned to.
 * </p>
 */
public enum HackathonRole implements Role {

    /**
     * A judge responsible for evaluating projects.
     * Has permissions to vote and end the evaluation phase.
     */
    H_Judge(EnumSet.of(Permission.Can_Vote, Permission.Can_End_Valuation_State)),

    /**
     * The event organizer.
     * Has permissions to proclaim winners and invite judges.
     */
    H_Organizer(EnumSet.of(Permission.Can_Proclamate_Winner, Permission.Can_Invite_Judge)),

    /**
     * A standard participant within a team.
     * Typically has read-only access or internal team permissions (currently none explicitly defined).
     */
    H_HackathonTeamMember(EnumSet.noneOf(Permission.class)),

    /**
     * The leader of a participating team.
     * Has the specific permission to submit the final project.
     */
    H_HackathonTeamLeader(EnumSet.of(Permission.Can_Submit));

    /**
     * The set of permissions associated with this role.
     */
    private final Set<Permission> permissions;

    /**
     * Constructs a role with a defined set of permissions.
     *
     * @param permissions the permissions to grant.
     */
    HackathonRole(Set<Permission> permissions) {
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
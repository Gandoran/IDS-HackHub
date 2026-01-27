package unicam.it.idshackhub.model.user.role;

import unicam.it.idshackhub.model.user.role.permission.Permission;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public enum ContextRole implements Role {
    /**
     * A standard member of the team.
     * Currently grants no administrative permissions within the team structure.
     */
    T_TeamMember(EnumSet.noneOf(Permission.class)),

    /**
     * The leader/manager of the team.
     * Has permissions to register the team for events and create hackathon sub-teams.
     */
    T_TeamLeader(EnumSet.of(Permission.Can_Register_Team, Permission.Can_Create_HackathonTeam)),

    /**
     * A judge responsible for evaluating projects.
     * Has permissions to vote and end the evaluation phase.
     */
    H_Judge(EnumSet.of(Permission.Can_Vote, Permission.Can_End_Evaluation_State, Permission.Can_See_Submissions)),

    /**
     * A mentor responsible for helping teams.
     * Has permission to send emails and organize meetings.
     */
    H_Mentor(EnumSet.of(Permission.Can_Send_Email, Permission.Can_Manage_Help_Request, Permission.Can_See_Submissions)),

    /**
     * The event organizer.
     * Has permission to proclaim winners and invite judges.
     */
    H_Organizer(EnumSet.of(Permission.Can_Proclamate_Winner, Permission.Can_Invite_Staff, Permission.Can_See_Submissions)),

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

    ContextRole(Set<Permission> permissions) {
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

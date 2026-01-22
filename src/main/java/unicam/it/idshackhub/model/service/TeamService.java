package unicam.it.idshackhub.model.service;

import unicam.it.idshackhub.model.hackathon.Hackathon;
import unicam.it.idshackhub.model.team.HackathonTeam;
import unicam.it.idshackhub.model.team.Team;
import unicam.it.idshackhub.model.team.builder.HackathonTeamBuilder;
import unicam.it.idshackhub.model.user.User;
import unicam.it.idshackhub.model.user.assignment.Assignment;
import unicam.it.idshackhub.model.user.assignment.BaseContext;
import unicam.it.idshackhub.model.user.role.HackathonRole;
import unicam.it.idshackhub.model.user.role.Role;
import unicam.it.idshackhub.model.user.role.TeamRole;
import unicam.it.idshackhub.model.user.role.permission.Permission;

import java.util.List;

import static unicam.it.idshackhub.model.service.PermissionChecker.checkPermission;

/**
 * Service responsible for managing Hackathon Team operations.
 * <p>
 * This service handles the specialized logic for registering a sub-team ({@link HackathonTeam})
 * to a specific Hackathon event. It orchestrates validation, object construction, and
 * the assignment of event-specific roles.
 * </p>
 */
public class TeamService {

    /**
     * Registers a new team for a specific Hackathon.
     * <p>
     * This method performs a sequence of operations:
     * <ol>
     * <li>Retrieves the Main Team context of the leader.</li>
     * <li>Validates permissions, member availability, uniqueness, and hackathon rules.</li>
     * <li>Builds the {@link HackathonTeam} object.</li>
     * <li>Finalizes registration by linking entities and assigning roles.</li>
     * </ol>
     * </p>
     *
     * @param teamLeader          the leader of the Main Team initiating the registration.
     * @param name                the name of the Hackathon Team.
     * @param description         the description of the Hackathon Team.
     * @param hackathonTeamLeader the user designated as the leader for this specific event.
     * @param members             the list of members participating in the event.
     * @param hackathon           the target Hackathon event.
     * @return the fully registered {@link HackathonTeam}.
     * @throws RuntimeException if any validation rule (permissions, size, uniqueness) is violated.
     */
    public HackathonTeam registerHackathonTeam(User teamLeader, String name, String description, User hackathonTeamLeader, List<User> members, Hackathon hackathon) {
        Team mainTeam = this.getMainTeam(teamLeader);

        validatePermissions(teamLeader, mainTeam);
        validateMembersAvailability(members, hackathon);
        validateMainTeamUniqueness(mainTeam, hackathon);
        validateHackathonRules(members.size(), hackathon);

        HackathonTeam hackathonTeam = buildHackathonTeam(name, description, hackathonTeamLeader, mainTeam, members);

        finalizeRegistration(hackathonTeam, mainTeam, hackathon);

        return hackathonTeam;
    }


    private void validatePermissions(User user, Team mainTeam) {
        if (!checkPermission(user, Permission.Can_Register_Team, mainTeam)) {
            throw new RuntimeException("Permission denied");
        }
    }


    private void validateMembersAvailability(List<User> members, Hackathon hackathon) {
        for (User member : members) {
            if (member.getRoleByContext(hackathon).isPresent()) {
                throw new RuntimeException("User already in the hackathon");
            }
        }
    }

    private void validateMainTeamUniqueness(Team mainTeam, Hackathon hackathon) {
        boolean alreadyParticipating = hackathon.getTeams().stream()
                .anyMatch(ht -> ht.getMainTeam().equals(mainTeam));

        if (alreadyParticipating) {
            throw new RuntimeException("Main Team already has a Hackathon Team");
        }
    }

    private void validateHackathonRules(int teamSize, Hackathon hackathon) {
        if (hackathon.getTeams().size() >= hackathon.getRules().getMaxTeams()) {
            throw new RuntimeException("Maximum team amount reached");
        }
        if (teamSize > hackathon.getRules().getMaxPlayersPerTeam()) {
            throw new RuntimeException("Team size is too big");
        }
        if (teamSize < hackathon.getRules().getMinPlayersPerTeam()) {
            throw new RuntimeException("Team size is too small");
        }
    }

    private HackathonTeam buildHackathonTeam(String name, String description, User leader, Team mainTeam, List<User> members) {
        return new HackathonTeamBuilder().reset()
                .buildName(name)
                .buildDescription(description)
                .buildLeader(leader)
                .buildMainTeam(mainTeam)
                .buildMembers(members)
                .getResult();
    }

    private void finalizeRegistration(HackathonTeam hackTeam, Team mainTeam, Hackathon hackathon) {
        linkHackTeamToMainTeam(hackTeam, mainTeam);

        hackathon.getTeams().add(hackTeam);
        hackTeam.setHackathonParticipation(hackathon);

        assignHackathonRoles(hackTeam, hackathon);
    }

    private void linkHackTeamToMainTeam(HackathonTeam hackathonTeam, Team mainTeam){
        if(mainTeam.getHackathonTeams().contains(hackathonTeam)){
            throw new RuntimeException("Hackathon team already exists in this Main Team");
        }
        mainTeam.getHackathonTeams().add(hackathonTeam);
    }

    private void assignHackathonRoles(HackathonTeam hackTeam, Hackathon hackathon) {
        for(User member : hackTeam.getMembers()){
            this.addAssignment(member, hackathon, HackathonRole.H_HackathonTeamMember);
        }
        this.addAssignment(hackTeam.getLeader(), hackathon, HackathonRole.H_HackathonTeamLeader);
    }

    private void addAssignment(User user, BaseContext context, Role role) {
        user.getAssignments().add(new Assignment(context, role));
    }

    private Team getMainTeam(User teamLeader){
        return teamLeader.getContextByRole(TeamRole.T_TeamLeader)
                .map(context -> (Team) context)
                .orElseThrow(() -> new RuntimeException("You have to be a Team Leader of a Main Team to create a Hackathon Team"));
    }
}
package unicam.it.idshackhub.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import unicam.it.idshackhub.model.hackathon.Hackathon;
import unicam.it.idshackhub.model.team.HackathonTeam;
import unicam.it.idshackhub.model.team.Team;
import unicam.it.idshackhub.model.team.builder.HackathonTeamBuilder;
import unicam.it.idshackhub.model.user.User;
import unicam.it.idshackhub.model.user.assignment.Assignment;
import unicam.it.idshackhub.model.user.role.ContextRole;
import unicam.it.idshackhub.model.user.role.permission.Permission;
import unicam.it.idshackhub.repository.HackathonTeamRepository;
import unicam.it.idshackhub.repository.UserRepository;

import java.util.List;

import static unicam.it.idshackhub.service.PermissionChecker.checkPermission;

/**
 * Manages team participation within a Hackathon.
 * <p>
 * This service mainly implements the use case of registering a main {@link unicam.it.idshackhub.model.team.Team}
 * to an {@link unicam.it.idshackhub.model.hackathon.Hackathon} by creating a {@link unicam.it.idshackhub.model.team.HackathonTeam}
 * and assigning the appropriate roles to participants.
 * </p>
 */

@Service
public class TeamService {

    private final HackathonTeamRepository hackathonTeamRepository;
    private final UserRepository userRepository;

    /**
     * Creates the service.
     *
     * @param hackathonTeamRepository repository used to persist Hackathon team participation entities.
     * @param userRepository repository used to persist role assignments for users.
     */

    @Autowired
    public TeamService(HackathonTeamRepository hackathonTeamRepository, UserRepository userRepository) {
        this.hackathonTeamRepository = hackathonTeamRepository;
        this.userRepository = userRepository;
    }

    /**
     * Registers a new team for a specific Hackathon.
     */
    @Transactional // Fondamentale: salva team, assignments e aggiorna hackathon tutto insieme

    /**
     * Registers a main team to participate in a specific Hackathon.
     * <p>
     * The registration is allowed only if:
     * <ul>
     *   <li>the acting user has {@link unicam.it.idshackhub.model.user.role.permission.Permission#Can_Register_Team} on the main team;</li>
     *   <li>the Hackathon phase allows team registration;</li>
     *   <li>the main team is not already registered to the same Hackathon;</li>
     *   <li>all requested members are available and comply with Hackathon rules.</li>
     * </ul>
     * The method creates a {@link unicam.it.idshackhub.model.team.HackathonTeam} via {@link unicam.it.idshackhub.model.team.builder.HackathonTeamBuilder},
     * links it to the Hackathon and main team, persists it, and assigns roles/assignments to participants.
     * </p>
     *
     * @param teamLeader the leader of the main team performing the registration.
     * @param name the name of the Hackathon team.
     * @param description the description of the Hackathon team.
     * @param hackathonTeamLeader the user who will lead the team within the Hackathon.
     * @param members the list of users participating in the Hackathon team.
     * @param hackathon the Hackathon to register the team to.
     * @return the persisted {@link unicam.it.idshackhub.model.team.HackathonTeam}.
     * @throws RuntimeException if permissions or preconditions are not satisfied.
     */

    public HackathonTeam registerHackathonTeam(User teamLeader, String name, String description, User hackathonTeamLeader, List<User> members, Hackathon hackathon) {
        Team mainTeam = this.getMainTeam(teamLeader);

        validatePermissions(teamLeader, mainTeam);
        validateHackathonPermissions(hackathon);
        validateMainTeamUniqueness(mainTeam, hackathon);
        validateMembersAvailability(members, hackathon);
        validateHackathonRules(members.size(), hackathon);

        HackathonTeam hackathonTeam = buildHackathonTeam(name, description, hackathonTeamLeader, mainTeam, members);

        // 4. JPA relationships
        // Add the team to the Hackathon list (OneToMany relationship)
        hackathon.getTeams().add(hackathonTeam);
        hackathonTeam.setHackathonParticipation(hackathon);

        // Add the Hackathon team to the main team list
        mainTeam.getHackathonTeams().add(hackathonTeam);

        // 5. Intermediate save to obtain the HackathonTeam ID
        // Needed to create Assignments that reference this Context
        hackathonTeam = hackathonTeamRepository.save(hackathonTeam);

        // 6. Role assignment (Assignment)
        assignHackathonRoles(hackathonTeam, hackathon);

        // 7. Final save of users (cascades Assignments)
        userRepository.saveAll(members); // Saves all members with the new roles
        userRepository.save(hackathonTeamLeader);

        return hackathonTeam;
    }

    /**
     * Ensures that the given user is allowed to register their main team to a hackathon.
     *
     * @param user the user requesting the operation
     * @param mainTeam the main team that will be registered
     * @throws RuntimeException if the user does not have {@link Permission#Can_Register_Team} on the given team
     */

    private void validatePermissions(User user, Team mainTeam) {
        if (!checkPermission(user, Permission.Can_Register_Team, mainTeam)) {
            throw new RuntimeException("Permission denied");
        }
    }

    /**
     * Ensures that the hackathon current phase allows team registration.
     *
     * @param hackathon the target hackathon
     * @throws RuntimeException if the hackathon state denies {@link Permission#Can_Register_Team}
     */

    private void validateHackathonPermissions(Hackathon hackathon) {
        if (!hackathon.isActionAllowed(Permission.Can_Register_Team)) {
            throw new RuntimeException("Permission denied by hackathon");
        }
    }

    /**
     * Ensures that none of the given users is already assigned to the provided hackathon.
     *
     * @param members the candidate members of the hackathon team
     * @param hackathon the target hackathon
     * @throws RuntimeException if at least one user already has a role within the hackathon context
     */

    private void validateMembersAvailability(List<User> members, Hackathon hackathon) {
        for (User member : members) {
            if (member.getRoleByContext(hackathon).isPresent()) {
                throw new RuntimeException("User already in the hackathon");
            }
        }
    }

    /**
     * Ensures that the provided main team is not already associated to an existing hackathon team
     * within the same hackathon.
     *
     * @param mainTeam the main team to check
     * @param hackathon the target hackathon
     * @throws RuntimeException if the main team is already linked to a hackathon team
     */

    private void validateMainTeamUniqueness(Team mainTeam, Hackathon hackathon) {
        boolean alreadyParticipating = hackathon.getTeams().stream()
                .anyMatch(ht -> ht.getMainTeam().equals(mainTeam));
        if (alreadyParticipating) {
            throw new RuntimeException("Main Team already has a Hackathon Team");
        }
    }

    /**
     * Validates hackathon constraints related to teams and team size.
     * <p>
     * Checks:
     * <ul>
     *   <li>maximum number of registered teams</li>
     *   <li>minimum / maximum number of players per team</li>
     * </ul>
     *
     * @param teamSize the size of the team being registered
     * @param hackathon the target hackathon
     * @throws RuntimeException if any constraint is violated
     */

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

    /**
     * Creates a {@link HackathonTeam} using the builder, applying the provided attributes.
     *
     * @param name the hackathon team name
     * @param description the hackathon team description
     * @param leader the team leader (hackathon context)
     * @param mainTeam the underlying main team the hackathon team is based on
     * @param members the team members (including the leader, if your model expects it)
     * @return the newly built hackathon team
     */

    private HackathonTeam buildHackathonTeam(String name, String description, User leader, Team mainTeam, List<User> members) {
        return new HackathonTeamBuilder().reset()
                .buildName(name)
                .buildDescription(description)
                .buildLeader(leader)
                .buildMainTeam(mainTeam)
                .buildMembers(members)
                .getResult();
    }

    /**
     * Assigns hackathon context roles to the hackathon team leader and members.
     * <p>
     * Members receive {@link ContextRole#H_HackathonTeamMember}; the leader receives
     * {@link ContextRole#H_HackathonTeamLeader}.
     *
     * @param hackTeam the hackathon team whose users will be assigned
     * @param hackathon the hackathon context used for the assignments
     */

    private void assignHackathonRoles(HackathonTeam hackTeam, Hackathon hackathon) {
        for (User member : hackTeam.getMembers()) {
            member.addAssignment(new Assignment(hackathon,ContextRole.H_HackathonTeamMember));
        }
        hackTeam.getLeader().addAssignment(new Assignment(hackathon,ContextRole.H_HackathonTeamLeader));
    }

    /**
     * Retrieves the main team led by the given user.
     *
     * @param teamLeader the user expected to be a main team leader
     * @return the main team led by the user
     * @throws RuntimeException if the user is not a leader of any main team
     */

    private Team getMainTeam(User teamLeader) {
        return teamLeader.getContextByRole(ContextRole.T_TeamLeader)
                .map(context -> (Team) context)
                .orElseThrow(() -> new RuntimeException("You have to be a Team Leader of a Main Team to create a Hackathon Team"));
    }
}
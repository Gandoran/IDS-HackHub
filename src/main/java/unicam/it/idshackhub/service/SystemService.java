package unicam.it.idshackhub.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import unicam.it.idshackhub.exception.InvalidOperationException;
import unicam.it.idshackhub.exception.PermissionDeniedException;
import unicam.it.idshackhub.exception.ResourceNotFoundException;
import unicam.it.idshackhub.model.hackathon.Hackathon;
import unicam.it.idshackhub.model.hackathon.HackathonBuilder;
import unicam.it.idshackhub.model.hackathon.Schedule;
import unicam.it.idshackhub.model.hackathon.TeamRules;
import unicam.it.idshackhub.model.team.Team;
import unicam.it.idshackhub.model.team.builder.TeamBuilder;
import unicam.it.idshackhub.model.user.User;
import unicam.it.idshackhub.model.user.assignment.Assignment;
import unicam.it.idshackhub.model.user.role.ContextRole;
import unicam.it.idshackhub.model.user.role.permission.Permission;
import unicam.it.idshackhub.repository.*;

import java.util.ArrayList;
import java.util.List;

import static unicam.it.idshackhub.service.utils.EntityUtils.getEntity;
import static unicam.it.idshackhub.service.utils.PermissionChecker.checkPermission;

/**
 * Provides system-level use cases that create core aggregates such as Hackathons and Teams.
 * <p>
 * This service coordinates builders and repositories and ensures that the acting user has the required
 * global permissions. It also establishes the initial assignments/roles in the created contexts.
 * </p>
 */
@Service
public class SystemService {

    private final UserRepository userRepository;
    private final HackathonRepository hackathonRepository;
    private final TeamRepository teamRepository;

    @Autowired
    public SystemService(UserRepository userRepository,
                         HackathonRepository hackathonRepository,
                         TeamRepository teamRepository) {
        this.userRepository = userRepository;
        this.hackathonRepository = hackathonRepository;
        this.teamRepository = teamRepository;
    }

    /**
     * Creates and persists a new Hackathon.
     * <p>
     * The acting user must have {@link unicam.it.idshackhub.model.user.role.permission.Permission#Can_Create_Hackathon}.
     * The created Hackathon is initialized through {@link unicam.it.idshackhub.model.hackathon.HackathonBuilder} and the
     * user is assigned as {@link unicam.it.idshackhub.model.user.role.ContextRole#H_Organizer}.
     * </p>
     */

    @Transactional
    public Hackathon createHackathon(Long verifiedUserId, String title, String description,Double prize, TeamRules teamRules, Schedule schedule) {
        User verifiedUser = getEntity(userRepository, verifiedUserId, "Organizer");
        if (!checkPermission(verifiedUser, Permission.Can_Create_Hackathon)) {
            throw new PermissionDeniedException("Permission denied");
        }
        HackathonBuilder hackathonBuilder = new HackathonBuilder();
        Hackathon hackathon = hackathonBuilder.reset()
                .buildTitle(title)
                .buildDescription(description)
                .buildPrize(prize)
                .buildRules(teamRules)
                .buildSchedule(schedule)
                .buildStaff(verifiedUser)
                .getResult();
        hackathon = hackathonRepository.save(hackathon);
        verifiedUser.addAssignment(new Assignment(hackathon,ContextRole.H_Organizer));
        userRepository.save(verifiedUser);
        return hackathon;
    }

    /**
     * Creates and persists a new Team.
     * <p>
     * The acting user must not already belong to a team and must have
     * {@link unicam.it.idshackhub.model.user.role.permission.Permission#Can_Create_Team}. The user is assigned
     * as {@link unicam.it.idshackhub.model.user.role.ContextRole#T_TeamLeader} in the created Team.
     * </p>
     */
    @Transactional
    public Team createTeam(Long userId, String name, String description, String payPalEmail) {
        User user = getEntity(userRepository, userId, "Leader");

        if (user.getUserTeam() != null) {
            throw new InvalidOperationException("User already in a team");
        }
        if (!checkPermission(user, Permission.Can_Create_Team)) {
            throw new PermissionDeniedException("Permission denied");
        }

        TeamBuilder builder = new TeamBuilder();
        Team team = builder.buildName(name)
                .buildDescription(description)
                .buildLeader(user)
                .buildMembers(new ArrayList<>())
                .buildPayPalAccount(payPalEmail)
                .getResult();

        team = teamRepository.save(team);
        user.addAssignment(new Assignment(team, ContextRole.T_TeamLeader));
        user.setUserTeam(team);
        userRepository.save(user);
        return team;
    }

    /**
     * Retrieves all teams from the system.
     */
    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    /**
     * Retrieves a specific team by its ID.
     * Throws a ResourceNotFoundException if the team does not exist.
     */
    public Team getTeamById(Long id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with ID: " + id));
    }

    /**
     * Retrieves all hackathons from the system.
     */
    public List<Hackathon> getAllHackathons() {
        return hackathonRepository.findAll();
    }

    /**
     * Retrieves a specific hackathon by its ID.
     * Throws a ResourceNotFoundException if the hackathon does not exist.
     */
    public Hackathon getHackathonById(Long id) {
        return hackathonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hackathon not found with ID: " + id));
    }

}
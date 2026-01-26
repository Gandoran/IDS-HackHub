package unicam.it.idshackhub.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

import static unicam.it.idshackhub.service.PermissionChecker.checkPermission;

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
     *
     * @param verifiedUser the user creating the Hackathon.
     * @param title the Hackathon title.
     * @param description the Hackathon description.
     * @param teamRules the rules that constrain team composition.
     * @param schedule the schedule/location information.
     * @return the persisted Hackathon.
     * @throws RuntimeException if the user lacks permission.
     */

    @Transactional
    public Hackathon createHackathon(User verifiedUser, String title, String description, TeamRules teamRules, Schedule schedule) {
        if (!checkPermission(verifiedUser, Permission.Can_Create_Hackathon)) {
            throw new RuntimeException("Permission denied");
        }
        HackathonBuilder hackathonBuilder = new HackathonBuilder();
        Hackathon hackathon = hackathonBuilder.reset()
                .buildTitle(title)
                .buildDescription(description)
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
     *
     * @param user the user creating the team.
     * @param name the team name.
     * @param description the team description.
     * @param iban the team's IBAN.
     * @return the persisted Team.
     * @throws RuntimeException if the user is already in a team or lacks permission.
     */

    @Transactional
    public Team createTeam(User user, String name, String description, String iban) {
        if (user.getUserTeam() != null) {
            throw new RuntimeException("User already in a team");
        }
        if (!checkPermission(user, Permission.Can_Create_Team)) {
            throw new RuntimeException("Permission denied");
        }
        TeamBuilder builder = new TeamBuilder();
        Team team = builder.buildName(name)
                .buildDescription(description)
                .buildLeader(user)
                .buildMembers(new ArrayList<>())
                .buildIban(iban)
                .getResult();
        team = teamRepository.save(team);
        user.addAssignment(new Assignment(team,ContextRole.T_TeamLeader));
        user.setUserTeam(team);
        userRepository.save(user);
        return team;
    }
}
package unicam.it.idshackhub.model.service;

import java.util.ArrayList;

import unicam.it.idshackhub.model.hackathon.Hackathon;
import unicam.it.idshackhub.model.hackathon.HackathonBuilder;
import unicam.it.idshackhub.model.hackathon.Schedule;
import unicam.it.idshackhub.model.hackathon.TeamRules;
import unicam.it.idshackhub.model.team.Team;
import unicam.it.idshackhub.model.team.builder.TeamBuilder;
import unicam.it.idshackhub.model.user.User;
import unicam.it.idshackhub.model.user.assignment.Assignment;
import unicam.it.idshackhub.model.user.assignment.BaseContext;
import unicam.it.idshackhub.model.user.role.HackathonRole;
import unicam.it.idshackhub.model.user.role.Role;
import unicam.it.idshackhub.model.user.role.TeamRole;
import unicam.it.idshackhub.model.user.role.permission.Permission;
import unicam.it.idshackhub.model.utils.Request;

import static unicam.it.idshackhub.model.service.PermissionChecker.checkPermission;

/**
 * Provides high-level system operations and entry points for creating core entities.
 * <p>
 * This service handles the creation of system-wide entities such as {@link Hackathon},
 * permanent {@link Team}s, and user {@link Request}s. It acts as a gatekeeper ensuring
 * that users possess the necessary permissions (via {@link PermissionChecker}) before
 * executing these operations.
 * </p>
 */
public class SystemService {

    /**
     * Creates a new request on behalf of a user.
     *
     * @param user        the user initiating the request.
     * @param description the content or reason for the request.
     * @return the created {@link Request} object.
     * @throws RuntimeException if the user lacks the {@link Permission#Can_Create_Request} permission.
     */
    public Request createRequest(User user, String description) {
        if (!checkPermission(user, Permission.Can_Create_Request)) {
            throw new RuntimeException("Permission denied");
        }
        return new Request(1, user, description);
    }

    /**
     * Processes a user request by approving or denying it.
     * <p>
     * If the request is managed successfully (approved), the associated user may receive
     * elevated privileges (e.g., promoted to Verified User).
     * </p>
     *
     * @param admin   the system administrator handling the request.
     * @param request the request object to be processed.
     * @param manage  {@code true} to approve the request; {@code false} to deny it.
     * @return the status of the management action (matches the {@code manage} parameter).
     * @throws RuntimeException if the admin lacks the {@link Permission#Can_Manage_Request} permission.
     */
    public boolean manageRequest(User admin, Request request, boolean manage) {
        if (!checkPermission(admin, Permission.Can_Manage_Request)) {
            throw new RuntimeException("Permission denied");
        }
        request.Manage(manage);
        return manage;
    }

    /**
     * Creates a new Hackathon event.
     * <p>
     * This method utilizes the {@link HackathonBuilder} to construct the event and automatically
     * assigns the creator the {@link HackathonRole#H_Organizer} role for this specific context.
     * </p>
     *
     * @param verifiedUser the user organizing the event (must be Verified).
     * @param title        the title of the Hackathon.
     * @param description  the description of the event.
     * @param teamRules    the rules regarding team composition.
     * @param schedule     the schedule of the event.
     * @return the newly created {@link Hackathon} instance.
     * @throws RuntimeException if the user lacks the {@link Permission#Can_Create_Hackathon} permission.
     */
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

        this.AddAssignment(verifiedUser, hackathon, HackathonRole.H_Organizer);
        return hackathon;
    }

    /**
     * Creates a new permanent Main Team.
     * <p>
     * This method enforces the rule that a user can only belong to one main team at a time.
     * Upon success, the user is assigned the {@link TeamRole#T_TeamLeader} role, and the
     * bidirectional relationship between User and Team is established.
     * </p>
     *
     * @param user        the user creating the team (becomes the Leader).
     * @param name        the name of the team.
     * @param description the description of the team.
     * @param iban        the financial identifier for the team.
     * @return the newly created {@link Team} instance.
     * @throws RuntimeException if the user is already part of a team or lacks {@link Permission#Can_Create_Team}.
     */
    public Team createTeam(User user, String name, String description, String iban) {
        if(user.getUserTeam() != null){
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

        this.AddAssignment(user, team, TeamRole.T_TeamLeader);
        user.setUserTeam(team);

        return team;
    }

    private void AddAssignment(User user, BaseContext context, Role role) {
        user.getAssignments().add(new Assignment(context, role));
    }
}
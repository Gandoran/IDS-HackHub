/*package unicam.it.idshackhub.model.service;

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
import unicam.it.idshackhub.model.user.assignment.Context;
import unicam.it.idshackhub.model.user.role.HackathonRole;
import unicam.it.idshackhub.model.user.role.Role;
import unicam.it.idshackhub.model.user.role.TeamRole;
import unicam.it.idshackhub.model.user.role.permission.Permission;
import unicam.it.idshackhub.model.utils.Request;

import static unicam.it.idshackhub.model.service.PermissionChecker.checkPermission;

public class SystemService {

    public boolean createRequest(User user, String description) {
        if (!checkPermission(user, Permission.Can_Create_Request)) {
            throw new RuntimeException("Permission denied");
        }
        Request request = new Request(1, user, description);
        return true;
    }


    public boolean manageRequest(User admin, Request request, boolean manage) {
        if (!checkPermission(admin, Permission.Can_Manage_Request)) {
            throw new RuntimeException("Permission denied");
        }
        request.Manage(manage);
        return manage;
    }


    public Hackathon createHackathon(User verifiedUser, String title, String description, TeamRules teamRules, Schedule schedule) {
        if (!checkPermission(verifiedUser, Permission.Can_Create_Hackathon)) {
            throw new RuntimeException("Permission denied");
        }
        HackathonBuilder hackathonBuilder = new HackathonBuilder();
        Hackathon hackathon =hackathonBuilder.reset()
                .buildTitle(title)
                .buildDescription(description)
                .buildRules(teamRules)
                .buildSchedule(schedule)
                .buildStaff(verifiedUser)
                .getResult();
        this.AddAssignment(verifiedUser, hackathon, HackathonRole.H_Organizer);
        return hackathon;
    }


    public Team createTeam(User user, String name, String description, String iban) {
        if(user.getUserTeam()!=null){
            throw new RuntimeException("User already in a team");
        }
        if (!checkPermission(user, Permission.Can_Create_Team)) {
            throw new RuntimeException("Permission denied");
        }
        TeamBuilder builder = new TeamBuilder();
        Team team=builder.buildName(name)
                .buildDescription(description)
                .buildLeader(user)
                .buildMembers(new ArrayList<>())
                .buildIban(iban)
                .getResult();
        this.AddAssignment(user,team, TeamRole.T_TeamLeader);
        return team;
    }

    private void AddAssignment(User user, BaseContext context, Role role) {
        user.getAssignments().add(new Assignment(context,role));
    }
}
*/
 // GENERATA DA GEMINI
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

public class SystemService {

    public Request createRequest(User user, String description) {
        if (!checkPermission(user, Permission.Can_Create_Request)) {
            throw new RuntimeException("Permission denied");
        }
        return new Request(1, user, description);
    }

    public boolean manageRequest(User admin, Request request, boolean manage) {
        if (!checkPermission(admin, Permission.Can_Manage_Request)) {
            throw new RuntimeException("Permission denied");
        }
        request.Manage(manage);
        return manage;
    }

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

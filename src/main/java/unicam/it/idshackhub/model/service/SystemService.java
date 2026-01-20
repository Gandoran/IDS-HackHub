package unicam.it.idshackhub.model.service;

import java.util.ArrayList;
import unicam.it.idshackhub.model.hackathon.HackathonBuilder;
import unicam.it.idshackhub.model.hackathon.Schedule;
import unicam.it.idshackhub.model.hackathon.TeamRules;
import unicam.it.idshackhub.model.team.builder.TeamBuilder;
import unicam.it.idshackhub.model.user.User;
import unicam.it.idshackhub.model.user.role.permission.Permission;
import unicam.it.idshackhub.model.utils.Request;

import static unicam.it.idshackhub.model.service.PermissionChecker.checkPermission;

public class SystemService {

    public void createRequest(User user, String description) {
        if (checkPermission(user, Permission.Can_Create_Request)) {
            throw new RuntimeException("Permission denied");
        }
        Request request = new Request(user, description);
    }


    public void manageRequest(User admin, Request request, boolean manage) {
        if (!checkPermission(admin, Permission.Can_Manage_Request)) {
            throw new RuntimeException("Permission denied");
        }
        request.Manage(manage);
    }


    public void createHackathon(User verifiedUser, String title, String description, TeamRules teamRules, Schedule schedule) {
        if (checkPermission(verifiedUser, Permission.Can_Create_HackathonTeam)) {
            throw new RuntimeException("Permission denied");
        }
        HackathonBuilder hackathonBuilder = new HackathonBuilder();
                hackathonBuilder.reset()
                .buildTitle(title)
                .buildDescription(description)
                .buildRules(teamRules)
                .buildSchedule(schedule)
                .buildStaff()
                .getResult();

    }


    public void createTeam(User user, String name, String description, String iban) {
        if (!checkPermission(user, Permission.Can_Create_Team)) {
            throw new RuntimeException("Permission denied");
        }
        TeamBuilder builder = new TeamBuilder();
        builder.buildName(name)
                .buildDescription(description)
                .buildLeader(user)
                .buildMembers(new ArrayList<>())
                .buildIban(iban)
                .getTeam();
    }
}

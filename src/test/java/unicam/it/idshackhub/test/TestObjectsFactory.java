package unicam.it.idshackhub.test;

import unicam.it.idshackhub.model.hackathon.*;
import unicam.it.idshackhub.model.team.HackathonTeam;
import unicam.it.idshackhub.model.team.Team;
import unicam.it.idshackhub.model.team.builder.HackathonTeamBuilder;
import unicam.it.idshackhub.model.team.builder.TeamBuilder;
import unicam.it.idshackhub.model.user.User;
import unicam.it.idshackhub.model.user.assignment.Assignment;
import unicam.it.idshackhub.model.user.assignment.BaseContext;
import unicam.it.idshackhub.model.user.role.GlobalRole;
import unicam.it.idshackhub.model.user.role.HackathonRole;
import unicam.it.idshackhub.model.user.role.Role;
import unicam.it.idshackhub.model.user.role.TeamRole;
import unicam.it.idshackhub.model.utils.Request;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TestObjectsFactory {

    public static User createUser(Long id, String username, String password) {
        return new User(id, username, username + "@test.com", password);
    }

    public static User createVerifiedUser(Long id, String username, String password) {
        User user = new User(id, username, username + "@test.com", password);
        user.setGlobalRole(GlobalRole.G_VerifiedUser);
        return user;
    }

    public static User createAdmin(Long id, String username, String password) {
        User user = new User(id, username, username + "@test.com", password);
        user.setGlobalRole(GlobalRole.G_SystemAdmin);
        return user;
    }

    public static Team createMainTeam(Long id, String name, User leader) {
        TeamBuilder builder = new TeamBuilder();
        Team team = builder.reset()
                .buildName(name)
                .buildDescription("Description for " + name)
                .buildLeader(leader)
                .buildMembers(new ArrayList<>())
                .buildIban("IT" + name.toUpperCase() + "00000000000000000")
                .getResult();
        team.setId(id);

        AddAssignment(leader, team, TeamRole.T_TeamLeader);
        leader.setUserTeam(team);

        return team;
    }

    public static HackathonTeam createHackathonTeam(Long id, String name, Team mainTeam, User leader, List<User> members, Hackathon hackathon) {
        HackathonTeamBuilder builder = new HackathonTeamBuilder();
        HackathonTeam team = builder.reset()
                .buildMainTeam(mainTeam)
                .buildName(name)
                .buildDescription("Description for " + name)
                .buildLeader(leader)
                .buildMembers(members)
                .buildHackathonParticipation(hackathon)
                .getResult();
        team.setId(id);

        if (!mainTeam.getHackathonTeams().contains(team)) {
            mainTeam.getHackathonTeams().add(team);
        }

        if (hackathon != null) {
            if (!hackathon.getTeams().contains(team)) {
                hackathon.getTeams().add(team);
            }

            for (User member : members) {
                AddAssignment(member, hackathon, HackathonRole.H_HackathonTeamMember);
            }
            AddAssignment(leader, hackathon, HackathonRole.H_HackathonTeamLeader);
        }

        return team;
    }

    public static Hackathon createHackathon(Long id, String title, User organizer) {
        LocalDateTime now = LocalDateTime.now();
        Schedule schedule = new Schedule(
                now,
                now.plusDays(1),
                now.plusDays(2),
                "Online"
        );
        TeamRules rules = new TeamRules(20, 1, 5, 1);

        HackathonBuilder builder = new HackathonBuilder();
        Hackathon h = builder.reset()
                .buildTitle(title)
                .buildDescription("Test Description for " + title)
                .buildRules(rules)
                .buildSchedule(schedule)
                .buildStaff(organizer)
                .getResult();

        h.setId(id);
        organizer.setGlobalRole(GlobalRole.G_VerifiedUser);

        AddAssignment(organizer, h, HackathonRole.H_Organizer);

        return h;
    }

    public static Request createRequest(Long id, User user, String description) {
        return new Request(id, user, description);
    }

    public static TeamRules createTeamRules() {
        return new TeamRules(20, 1, 5, 1);
    }

    public static Schedule createSchedule() {
        return new Schedule(LocalDateTime.now(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), "Online");
    }

    private static void AddAssignment(User user, BaseContext context, Role role) {
        boolean alreadyAssigned = user.getAssignments().stream()
                .anyMatch(a -> a.getContext().equals(context) && a.getRole().equals(role));

        if (!alreadyAssigned) {
            user.getAssignments().add(new Assignment(context, role));
        }
    }
}
package unicam.it.idshackhub.model.service;

import unicam.it.idshackhub.model.hackathon.Hackathon;
import unicam.it.idshackhub.model.team.HackathonTeam;
import unicam.it.idshackhub.model.team.Team;
import unicam.it.idshackhub.model.team.builder.HackathonTeamBuilder;
import unicam.it.idshackhub.model.user.User;
import unicam.it.idshackhub.model.user.role.TeamRole;
import unicam.it.idshackhub.model.user.role.permission.Permission;

import java.util.List;

import static unicam.it.idshackhub.model.service.PermissionChecker.checkPermission;

public class TeamService {

    public void registerTeam(User teamLeader, HackathonTeam hackathonTeam, Hackathon hackathon) {
        if(!checkPermission(teamLeader, Permission.Can_Register_Team, hackathonTeam.getMainTeam())) {
            throw new RuntimeException("Permission denied");}
        if(hackathon.getTeams().contains(hackathonTeam)) {
           throw new RuntimeException("Team already registered");}
        hackathon.getTeams().add(hackathonTeam);
    }


    public void createHackathonTeam(User teamLeader,String name,String description, User hackathonTeamLeader, List<User> HackathonTeamMembers) {
        Team mainTeam = teamLeader.getContextByRole(TeamRole.T_TeamLeader)
                .map(context -> (Team) context)
                .orElseThrow(() -> new RuntimeException("You have to be a Team Leader of a Main Team to create a Hackathon Team"));
        if(!checkPermission(teamLeader, Permission.Can_Create_HackathonTeam, mainTeam)) {
            throw new RuntimeException("Permission denied");
        }

        HackathonTeamBuilder builder = new HackathonTeamBuilder();
        builder.buildName(name);
        builder.buildDescription(description);
        builder.buildLeader(hackathonTeamLeader);
        builder.buildMainTeam(mainTeam);
        builder.buildMembers(HackathonTeamMembers);

        HackathonTeam hackTeam = builder.getTeam();

        boolean alreadyExists = mainTeam.getHackathonTeams().stream()
                .anyMatch(t -> t.getId() == hackTeam.getId());
        if (alreadyExists) {
            throw new RuntimeException("Team already exists");
        }

        mainTeam.getHackathonTeams().add(hackTeam);
        }
    }

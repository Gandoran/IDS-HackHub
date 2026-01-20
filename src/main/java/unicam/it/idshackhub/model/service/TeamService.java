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
        if(!checkPermission(teamLeader, Permission.Can_Register_Team)) {
            throw new RuntimeException("Permission denied");}
        hackathon.getTeams().add(hackathonTeam);
    }


    public void createHackathonTeam(User teamLeader,String name,String description, User hackathonTeamLeader, List<User> HackathonTeamMembers) {
        if(checkPermission(teamLeader, Permission.Can_Create_HackathonTeam)){
            HackathonTeamBuilder builder = new HackathonTeamBuilder();
            builder.buildName(name);
            builder.buildDescription(description);
            builder.buildLeader(hackathonTeamLeader);
            builder.buildMainTeam(
                    teamLeader.getContextByRole(TeamRole.T_TeamLeader)
                            .map(context -> (Team) context)
                            .orElseThrow(() -> new RuntimeException("Main Team not found"))
            );
            builder.buildMembers(HackathonTeamMembers);
            HackathonTeam team = builder.getTeam();
        }
    }

}

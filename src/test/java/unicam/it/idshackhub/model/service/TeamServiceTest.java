package unicam.it.idshackhub.model.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import unicam.it.idshackhub.model.hackathon.Hackathon;
import unicam.it.idshackhub.model.team.HackathonTeam;
import unicam.it.idshackhub.model.team.Team;
import unicam.it.idshackhub.model.user.User;
import unicam.it.idshackhub.model.user.role.HackathonRole;
import unicam.it.idshackhub.test.TestObjectsFactory;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TeamServiceTest {

    private TeamService teamService;
    private User teamLeader;
    private User teamLeaderTwo;
    private User hackathonOrganizer;
    private User memberUser;
    private User hackathonTeamLeader;
    private Team mainTeam;
    private Hackathon hackathon;
    private List<User> members;

    @BeforeEach
    void setUp() {
        teamService = new TeamService();
        teamLeader = TestObjectsFactory.createUser(1L, "LeaderUser", "Pass1");
        memberUser = TestObjectsFactory.createUser(2L, "MemberUser", "Pass2");
        hackathonOrganizer = TestObjectsFactory.createUser(3L, "OrganizerUser", "Pass3");
        hackathonTeamLeader = TestObjectsFactory.createUser(4L, "HackTeamLeader", "Pass4");
        teamLeaderTwo = TestObjectsFactory.createUser(5L, "LeaderUserTwo", "Pass5");

        mainTeam = TestObjectsFactory.createMainTeam(100L, "Hack Team", teamLeader);
        hackathon = TestObjectsFactory.createHackathon(500L, "Super Hackathon", hackathonOrganizer);

        members = new ArrayList<>();
        members.add(teamLeader);
        members.add(memberUser);
        members.add(hackathonTeamLeader);
    }

    @Test
    void registerHackathonTeam_Success() {
        HackathonTeam result = teamService.registerHackathonTeam(teamLeader, "Hack Team Alpha", "Desc", hackathonTeamLeader, members, hackathon);

        assertTrue(hackathon.getTeams().contains(result));
        assertEquals(hackathon, result.getHackathonParticipation());
        assertTrue(mainTeam.getHackathonTeams().contains(result));

        boolean hasLeaderRole = hackathonTeamLeader.getAssignments().stream()
                .anyMatch(a -> a.getRole() == HackathonRole.H_HackathonTeamLeader
                        && a.getContext().equals(hackathon));
        assertTrue(hasLeaderRole, "Leader should receive H_HackathonTeamLeader assignment");

        boolean leaderHasMemberRole = hackathonTeamLeader.getAssignments().stream()
                .anyMatch(a -> a.getRole() == HackathonRole.H_HackathonTeamMember
                        && a.getContext().equals(hackathon));
        assertTrue(leaderHasMemberRole, "Leader should also have H_HackathonTeamMember assignment");

        boolean hasMemberRole = memberUser.getAssignments().stream()
                .anyMatch(a -> a.getRole() == HackathonRole.H_HackathonTeamMember
                        && a.getContext().equals(hackathon));
        assertTrue(hasMemberRole, "Members should receive H_HackathonTeamMember assignment");
    }

    @Test
    void registerHackathonTeam_Failure_MaxTeams() {
        hackathon.getRules().setMaxTeams(0);
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                teamService.registerHackathonTeam(teamLeader, "Alpha", "Desc", hackathonTeamLeader, members, hackathon));
        assertEquals("Maximum team amount reached", exception.getMessage());
    }

    @Test
    void registerHackathonTeam_Failure_MaxTeamMembers() {
        hackathon.getRules().setMaxPlayersPerTeam(1);
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                teamService.registerHackathonTeam(teamLeader, "Alpha", "Desc", hackathonTeamLeader, members, hackathon));
        assertEquals("Team size is too big", exception.getMessage());
    }

    @Test
    void registerHackathonTeam_Failure_MinTeamMembers() {
        hackathon.getRules().setMinPlayersPerTeam(10);
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                teamService.registerHackathonTeam(teamLeader, "Alpha", "Desc", hackathonTeamLeader, members, hackathon));
        assertEquals("Team size is too small", exception.getMessage());
    }

    @Test
    void registerHackathonTeam_Failure_MainTeamAlreadyParticipating() {
        teamService.registerHackathonTeam(teamLeader, "Alpha", "Desc", hackathonTeamLeader, members, hackathon);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                teamService.registerHackathonTeam(teamLeader, "Beta", "Desc", hackathonTeamLeader, members, hackathon));
        assertEquals("User already in the hackathon", exception.getMessage());
    }

    @Test
    void registerHackathonTeam_Failure_UserAlreadyInHackathon() {
        List<User> newMembers = new ArrayList<>(members);
        teamService.registerHackathonTeam(teamLeader, "Alpha", "Desc", hackathonTeamLeader, members, hackathon);

        Team secondMainTeam = TestObjectsFactory.createMainTeam(200L, "Other Team", teamLeaderTwo);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                teamService.registerHackathonTeam(teamLeaderTwo, "Beta", "Desc", teamLeaderTwo, newMembers, hackathon));
        assertEquals("User already in the hackathon", exception.getMessage());
    }

    @Test
    void registerHackathonTeam_Failure_NotTeamLeader() {
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                teamService.registerHackathonTeam(memberUser, "Name", "Desc", hackathonTeamLeader, members, hackathon));
        assertEquals("You have to be a Team Leader of a Main Team to create a Hackathon Team", exception.getMessage());
    }
}
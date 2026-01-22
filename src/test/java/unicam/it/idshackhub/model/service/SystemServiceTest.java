package unicam.it.idshackhub.model.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import unicam.it.idshackhub.model.hackathon.Hackathon;
import unicam.it.idshackhub.model.hackathon.Schedule;
import unicam.it.idshackhub.model.hackathon.TeamRules;
import unicam.it.idshackhub.model.team.Team;
import unicam.it.idshackhub.model.user.User;
import unicam.it.idshackhub.model.user.role.GlobalRole;
import unicam.it.idshackhub.model.user.role.HackathonRole;
import unicam.it.idshackhub.model.user.role.TeamRole;
import unicam.it.idshackhub.model.utils.Request;
import unicam.it.idshackhub.test.TestObjectsFactory;
import static org.junit.jupiter.api.Assertions.*;

class SystemServiceTest {

    private SystemService systemService;
    private User teamLeader;
    private User hackathonOrganizer;
    private User memberUser;
    private User sysAdmin;
    private Team mainTeam;
    private TeamRules teamRules;
    private Schedule schedule;
    private Request requestMemberUser;

    @BeforeEach
    void setUp() {
        systemService = new SystemService();
        teamLeader = TestObjectsFactory.createUser(1L, "LeaderUser", "Pass1");
        memberUser = TestObjectsFactory.createUser(2L, "MemberUser", "Pass2");
        hackathonOrganizer = TestObjectsFactory.createVerifiedUser(3L, "OrganizerUser", "Pass3");
        sysAdmin = TestObjectsFactory.createAdmin(6L, "Admin", "Pass6");

        requestMemberUser = TestObjectsFactory.createRequest(55L, memberUser, "RequestDesc");

        mainTeam = TestObjectsFactory.createMainTeam(100L, "Hack Team", teamLeader);

        teamRules = TestObjectsFactory.createTeamRules();
        schedule = TestObjectsFactory.createSchedule();
    }

    @Test
    void createRequest_Success() {
        Request result = systemService.createRequest(memberUser, "Request");
        assertNotNull(result);
        assertEquals("Request", result.getDescription());
        assertEquals(memberUser, result.getUser());
    }

    @Test
    void createRequest_Failure_PermissionDenied() {
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                systemService.createRequest(sysAdmin, "Request"));
        assertEquals("Permission denied", exception.getMessage());
    }

    @Test
    void manageRequest_Success() {
        assertNotEquals(GlobalRole.G_VerifiedUser, memberUser.getGlobalRole());
        boolean result = systemService.manageRequest(sysAdmin, requestMemberUser, true);
        assertTrue(result);
        assertEquals(GlobalRole.G_VerifiedUser, memberUser.getGlobalRole(),
                "User should be promoted to VerifiedUser after request approval");
    }

    @Test
    void manageRequest_Failure_RequestDenied() {
        boolean result = systemService.manageRequest(sysAdmin, requestMemberUser, false);
        assertFalse(result);
        assertNotEquals(GlobalRole.G_VerifiedUser, memberUser.getGlobalRole(),
                "User should NOT be promoted if request is denied");
    }

    @Test
    void createHackathon_Success() {
        Hackathon result = systemService.createHackathon(hackathonOrganizer, "NewHack", "Desc", teamRules, schedule);

        assertNotNull(result);

        boolean hasOrganizerRole = hackathonOrganizer.getAssignments().stream()
                .anyMatch(a -> a.getRole() == HackathonRole.H_Organizer && a.getContext().equals(result));

        assertTrue(hasOrganizerRole, "User should be assigned H_Organizer role for the new Hackathon");
    }

    @Test
    void createTeam_Success() {
        Team result = systemService.createTeam(memberUser, "TeamName", "Desc", "ITTEAM00000000000");

        assertNotNull(result);

        boolean hasLeaderRole = memberUser.getAssignments().stream()
                .anyMatch(a -> a.getRole() == TeamRole.T_TeamLeader && a.getContext().equals(result));
        assertTrue(hasLeaderRole, "User should be assigned T_TeamLeader role");

        assertEquals(result, memberUser.getUserTeam(), "User's team reference must be updated");
    }

    @Test
    void createTeam_Failure_UserAlreadyInATeam() {
        Team existingTeam = TestObjectsFactory.createMainTeam(999L, "Existing", memberUser);
        memberUser.setUserTeam(existingTeam);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                systemService.createTeam(memberUser, "NewTeam", "NewTeamDesc", "ITTEAM000000000000"));
        assertEquals("User already in a team", exception.getMessage());
    }
}
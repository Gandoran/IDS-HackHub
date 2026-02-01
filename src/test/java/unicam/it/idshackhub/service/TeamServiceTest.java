package unicam.it.idshackhub.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import unicam.it.idshackhub.model.hackathon.Hackathon;
import unicam.it.idshackhub.model.message.MessageType;
import unicam.it.idshackhub.model.team.HackathonTeam;
import unicam.it.idshackhub.model.team.Team;
import unicam.it.idshackhub.model.user.User;
import unicam.it.idshackhub.model.user.role.ContextRole;
import unicam.it.idshackhub.model.user.role.permission.Permission;
import unicam.it.idshackhub.repository.HackathonTeamRepository;
import unicam.it.idshackhub.repository.UserRepository;
import unicam.it.idshackhub.test.TestObjectsFactory;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    @Mock private HackathonTeamRepository hackathonTeamRepository;
    @Mock private UserRepository userRepository;
    @Mock private MessageService messageService;

    @InjectMocks
    private TeamService teamService;

    private MockedStatic<PermissionChecker> permissionCheckerMock;

    private User teamLeader;
    private User teamLeaderTwo;
    private User hackathonOrganizer;
    private User memberUser;
    private User hackathonTeamLeader;
    private User invitedUser;
    private Team mainTeam;
    private Hackathon hackathon;
    private List<User> members;

    @BeforeEach
    void setUp() {
        permissionCheckerMock = Mockito.mockStatic(PermissionChecker.class);

        teamLeader = TestObjectsFactory.createUser(1L, "LeaderUser", "Pass1");
        memberUser = TestObjectsFactory.createUser(2L, "MemberUser", "Pass2");
        hackathonOrganizer = TestObjectsFactory.createUser(3L, "OrganizerUser", "Pass3");
        hackathonTeamLeader = TestObjectsFactory.createUser(4L, "HackTeamLeader", "Pass4");
        teamLeaderTwo = TestObjectsFactory.createUser(5L, "LeaderUserTwo", "Pass5");
        invitedUser = TestObjectsFactory.createUser(6L, "InvitedUser", "Pass6");

        mainTeam = TestObjectsFactory.createMainTeam(100L, "Hack Team", teamLeader);
        teamLeader.setUserTeam(mainTeam);

        hackathon = TestObjectsFactory.createHackathon(500L, "Super Hackathon", hackathonOrganizer);

        members = new ArrayList<>();
        members.add(teamLeader);
        members.add(memberUser);
        members.add(hackathonTeamLeader);

        lenient().when(hackathonTeamRepository.save(any(HackathonTeam.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        lenient().when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        lenient().when(userRepository.saveAll(anyList()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Default permission for registration tests
        permissionCheckerMock.when(() -> PermissionChecker.checkPermission(any(), eq(Permission.Can_Register_Team), any()))
                .thenReturn(true);
    }

    @AfterEach
    void tearDown() {
        permissionCheckerMock.close();
    }

    @Test
    void registerHackathonTeam_Success() {
        HackathonTeam result = teamService.registerHackathonTeam(teamLeader, "Hack Team Alpha", "Desc", hackathonTeamLeader, members, hackathon);

        assertTrue(hackathon.getTeams().contains(result));
        assertEquals(hackathon, result.getHackathonParticipation());
        assertTrue(mainTeam.getHackathonTeams().contains(result));

        boolean hasLeaderRole = hackathonTeamLeader.getAssignments().stream()
                .anyMatch(a -> a.getRole().toString().equals(ContextRole.H_HackathonTeamLeader.toString())
                        && a.getContext().equals(hackathon));
        assertTrue(hasLeaderRole, "Leader should receive H_HackathonTeamLeader assignment");

        boolean leaderHasMemberRole = hackathonTeamLeader.getAssignments().stream()
                .anyMatch(a -> a.getRole().toString().equals(ContextRole.H_HackathonTeamMember.toString())
                        && a.getContext().equals(hackathon));
        assertTrue(leaderHasMemberRole, "Leader should also have H_HackathonTeamMember assignment");

        boolean hasMemberRole = memberUser.getAssignments().stream()
                .anyMatch(a -> a.getRole().toString().equals(ContextRole.H_HackathonTeamMember.toString())
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
        assertEquals("Main Team already has a Hackathon Team", exception.getMessage());
    }

    @Test
    void registerHackathonTeam_Failure_UserAlreadyInHackathon() {
        teamService.registerHackathonTeam(teamLeader, "Alpha", "Desc", hackathonTeamLeader, members, hackathon);

        Team secondMainTeam = TestObjectsFactory.createMainTeam(200L, "Other Team", teamLeaderTwo);
        teamLeaderTwo.setUserTeam(secondMainTeam);

        List<User> newMembers = new ArrayList<>(members);

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

    @Test
    void inviteUserToTeam_Success() {
        permissionCheckerMock.when(() -> PermissionChecker.checkPermission(teamLeader, Permission.Can_Invite_Users, mainTeam))
                .thenReturn(true);

        invitedUser.setUserTeam(null);

        teamService.inviteUserToTeam(invitedUser, teamLeader);

        verify(messageService).sendMessage(
                eq(teamLeader),
                eq(invitedUser),
                eq(MessageType.INVITE_USER_REQUEST),
                eq("You have been invited to join " + mainTeam.getName()),
                eq(mainTeam.getId())
        );
    }

    @Test
    void inviteUserToTeam_PermissionDenied() {
        permissionCheckerMock.when(() -> PermissionChecker.checkPermission(teamLeader, Permission.Can_Invite_Users, mainTeam))
                .thenReturn(false);

        invitedUser.setUserTeam(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                teamService.inviteUserToTeam(invitedUser, teamLeader));

        assertEquals("Permission denied: Cannot invite user.", exception.getMessage());
    }

    @Test
    void inviteUserToTeam_UserAlreadyInTeam() {
        permissionCheckerMock.when(() -> PermissionChecker.checkPermission(teamLeader, Permission.Can_Invite_Users, mainTeam))
                .thenReturn(true);

        Team otherTeam = new Team();
        invitedUser.setUserTeam(otherTeam);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                teamService.inviteUserToTeam(invitedUser, teamLeader));

        assertEquals("User already in a team", exception.getMessage());
    }
}
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
import unicam.it.idshackhub.model.user.User;
import unicam.it.idshackhub.model.user.role.permission.Permission;
import unicam.it.idshackhub.model.utils.Submission;
import unicam.it.idshackhub.repository.SubmissionRepository;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HackathonTeamServiceTest {

    @Mock
    private SubmissionRepository submissionRepository;

    @Mock
    private MessageService messageService;

    @InjectMocks
    private HackathonTeamService hackathonTeamService;

    private MockedStatic<PermissionChecker> permissionCheckerMock;

    private User leader;
    private User member;
    private HackathonTeam team;
    private Hackathon hackathon;

    @BeforeEach
    void setUp() {
        permissionCheckerMock = Mockito.mockStatic(PermissionChecker.class);

        leader = new User();
        leader.setId(1L);
        leader.setUsername("Leader");

        member = new User();
        member.setId(2L);
        member.setUsername("Member");

        hackathon = mock(Hackathon.class);

        lenient().when(hackathon.getId()).thenReturn(100L);
        lenient().when(hackathon.getSubmissions()).thenReturn(new ArrayList<>());

        team = new HackathonTeam();
        team.setId(50L);
        team.setHackathonParticipation(hackathon);
    }

    @AfterEach
    void tearDown() {
        permissionCheckerMock.close();
    }

    @Test
    void postSubmission_Success_NewSubmission() {
        String description = "Project Final";

        permissionCheckerMock.when(() -> PermissionChecker.checkPermission(leader, Permission.Can_Submit, team))
                .thenReturn(true);

        when(hackathon.isActionAllowed(Permission.Can_Submit)).thenReturn(true);
        when(submissionRepository.save(any(Submission.class))).thenAnswer(i -> i.getArguments()[0]);

        Submission result = hackathonTeamService.postSubmission(leader, description, team, hackathon);

        assertNotNull(result);
        assertEquals(description, result.getDescription());
        assertEquals(team, result.getTeam());
        assertEquals(hackathon, result.getHackathon());
        assertNotNull(result.getSubmissionDate());

        verify(submissionRepository).save(any(Submission.class));
    }

    @Test
    void postSubmission_Success_UpdateSubmission() {
        String oldDescription = "Draft";
        String newDescription = "Final Version";

        Submission existingSubmission = new Submission(oldDescription, team);
        team.setSubmission(existingSubmission);

        permissionCheckerMock.when(() -> PermissionChecker.checkPermission(leader, Permission.Can_Submit, team))
                .thenReturn(true);

        when(hackathon.isActionAllowed(Permission.Can_Submit)).thenReturn(true);
        when(submissionRepository.save(any(Submission.class))).thenAnswer(i -> i.getArguments()[0]);

        Submission result = hackathonTeamService.postSubmission(leader, newDescription, team, hackathon);

        assertEquals(existingSubmission, result);
        assertEquals(newDescription, result.getDescription());

        verify(submissionRepository).save(existingSubmission);
    }

    @Test
    void postSubmission_PermissionDenied_User() {
        permissionCheckerMock.when(() -> PermissionChecker.checkPermission(leader, Permission.Can_Submit, team))
                .thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                hackathonTeamService.postSubmission(leader, "Desc", team, hackathon));

        assertEquals("Permission denied: Cannot submit submission.", ex.getMessage());
        verifyNoInteractions(submissionRepository);
    }

    @Test
    void postSubmission_PermissionDenied_HackathonState() {
        permissionCheckerMock.when(() -> PermissionChecker.checkPermission(leader, Permission.Can_Submit, team))
                .thenReturn(true);

        when(hackathon.isActionAllowed(Permission.Can_Submit)).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                hackathonTeamService.postSubmission(leader, "Desc", team, hackathon));

        assertEquals("Permission denied: Cannot submit submission. Hackathon not in the correct state.", ex.getMessage());
        verifyNoInteractions(submissionRepository);
    }

    @Test
    void requestHelp_Success() {
        String problem = "We need help with Docker";

        permissionCheckerMock.when(() -> PermissionChecker.checkPermission(member, Permission.Can_Create_Help_Request, hackathon))
                .thenReturn(true);

        when(hackathon.isActionAllowed(Permission.Can_Create_Help_Request)).thenReturn(true);

        hackathonTeamService.requestHelp(member, hackathon, problem);

        verify(messageService).sendMessage(
                eq(member),
                isNull(),
                eq(MessageType.HELP_REQUEST),
                eq(problem),
                eq(100L)
        );
    }

    @Test
    void requestHelp_PermissionDenied_User() {
        permissionCheckerMock.when(() -> PermissionChecker.checkPermission(member, Permission.Can_Create_Help_Request, hackathon))
                .thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                hackathonTeamService.requestHelp(member, hackathon, "Help"));

        assertEquals("Permission Denied: You cannot send help request.", ex.getMessage());
        verifyNoInteractions(messageService);
    }

    @Test
    void requestHelp_PermissionDenied_HackathonState() {
        permissionCheckerMock.when(() -> PermissionChecker.checkPermission(member, Permission.Can_Create_Help_Request, hackathon))
                .thenReturn(true);

        when(hackathon.isActionAllowed(Permission.Can_Create_Help_Request)).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                hackathonTeamService.requestHelp(member, hackathon, "Help"));

        assertEquals("Cannot send help request: Hackathon not in the correct state.", ex.getMessage());
        verifyNoInteractions(messageService);
    }
}
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
import unicam.it.idshackhub.model.message.Message;
import unicam.it.idshackhub.model.message.MessageType;
import unicam.it.idshackhub.model.team.Team;
import unicam.it.idshackhub.model.user.User;
import unicam.it.idshackhub.model.user.role.permission.Permission;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private MessageService messageService;

    @InjectMocks
    private UserService userService;

    private MockedStatic<PermissionChecker> permissionCheckerMock;
    private User candidate;

    @BeforeEach
    void setUp() {
        permissionCheckerMock = Mockito.mockStatic(PermissionChecker.class);

        candidate = new User();
        candidate.setId(50L);
        candidate.setUsername("WannabeVerified");
    }

    @AfterEach
    void tearDown() {
        permissionCheckerMock.close();
    }

    @Test
    void sendVerifyRequest_Success() {
        // Setup: L'utente ha il permesso base (G_NormalUser di solito ce l'ha)
        permissionCheckerMock.when(() -> PermissionChecker.checkPermission(candidate, Permission.Can_Create_Verified_Request))
                .thenReturn(true);

        String content = "Please verify me";

        // Execute
        userService.sendVerifyRequest(candidate, content);

        // Verify: Deve chiamare messageService con recipient = NULL
        verify(messageService).sendMessage(
                eq(candidate), // Sender
                isNull(),      // Recipient (NULL per broadcast agli admin)
                eq(MessageType.VERIFY_USER_REQUEST), // Tipo
                eq(content),   // Contenuto
                eq(50L)        // Reference ID (ID dell'utente)
        );

    }

    @Test
    void sendVerifyRequest_PermissionDenied() {
        // Setup: L'utente NON ha il permesso (es. utente bannato o non registrato correttamente)
        permissionCheckerMock.when(() -> PermissionChecker.checkPermission(candidate, Permission.Can_Create_Verified_Request))
                .thenReturn(false);

        // Execute & Verify
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                userService.sendVerifyRequest(candidate, "Please verify me"));

        assertEquals("Permission denied: Cannot send verification request.", ex.getMessage());
        verifyNoInteractions(messageService);
    }

    @Test
    void sendJoinRequest_Success() {
        Team targetTeam = new Team();
        targetTeam.setId(101L);
        User teamLeader = new User();
        teamLeader.setId(202L);
        targetTeam.setLeader(teamLeader);

        permissionCheckerMock.when(() -> PermissionChecker.checkPermission(candidate, Permission.Can_Send_Join_Request))
                .thenReturn(true);

        String content = "I would like to join your team.";

        userService.sendJoinRequest(candidate, targetTeam, content);

        verify(messageService).sendMessage(
                eq(candidate),
                eq(teamLeader),
                eq(MessageType.JOIN_TEAM_REQUEST),
                eq(content),
                eq(101L)
        );
    }

    @Test
    void sendJoinRequest_PermissionDenied() {
        Team targetTeam = new Team();

        permissionCheckerMock.when(() -> PermissionChecker.checkPermission(candidate, Permission.Can_Send_Join_Request))
                .thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                userService.sendJoinRequest(candidate, targetTeam, "Let me in"));

        assertEquals("Permission denied: Cannot send join request.", ex.getMessage());
        verifyNoInteractions(messageService);
    }

    @Test
    void sendJoinRequest_AlreadyInTeam() {
        Team targetTeam = new Team();
        Team currentTeam = new Team();
        candidate.setUserTeam(currentTeam);

        permissionCheckerMock.when(() -> PermissionChecker.checkPermission(candidate, Permission.Can_Send_Join_Request))
                .thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                userService.sendJoinRequest(candidate, targetTeam, "Let me in"));

        assertEquals("You are already in a team", ex.getMessage());
        verifyNoInteractions(messageService);
    }

    @Test
    void manageInvites_DelegatesToMessageService() {
        Long messageId = 99L;
        boolean accept = true;
        Message expectedResponse = new Message();

        when(messageService.processReply(messageId, accept, candidate)).thenReturn(expectedResponse);

        Message result = userService.manageInvites(messageId, candidate, accept);

        assertEquals(expectedResponse, result);
        verify(messageService).processReply(messageId, accept, candidate);
    }

}
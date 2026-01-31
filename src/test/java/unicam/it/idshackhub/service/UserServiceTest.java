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
import unicam.it.idshackhub.model.message.MessageType;
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
}
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
import unicam.it.idshackhub.model.message.ActionStatus;
import unicam.it.idshackhub.model.message.Message;
import unicam.it.idshackhub.model.message.MessageType;
import unicam.it.idshackhub.model.user.User;
import unicam.it.idshackhub.model.user.role.permission.Permission;
import unicam.it.idshackhub.repository.MessageRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class git AdminServiceTest {

    @Mock private MessageRepository messageRepository;
    @Mock private MessageService messageService;

    @InjectMocks
    private AdminService adminService;

    private MockedStatic<PermissionChecker> permissionCheckerMock;
    private User admin;
    private User normalUser;

    @BeforeEach
    void setUp() {
        permissionCheckerMock = Mockito.mockStatic(PermissionChecker.class);

        admin = new User();
        admin.setId(1L);
        admin.setUsername("AdminUser");

        normalUser = new User();
        normalUser.setId(2L);
    }

    @AfterEach
    void tearDown() {
        permissionCheckerMock.close();
    }

    @Test
    void manageVerificationRequest_Success() {
        // Setup: Admin ha il permesso
        permissionCheckerMock.when(() -> PermissionChecker.checkPermission(admin, Permission.Can_Manage_Verified_Request))
                .thenReturn(true);

        Long messageId = 100L;
        boolean accept = true;

        // Execute
        adminService.manageVerificationRequest(admin, messageId, accept);

        // Verify: Deve delegare al MessageService
        verify(messageService).processReply(messageId, accept, admin);
    }

    @Test
    void manageVerificationRequest_PermissionDenied() {
        // Setup: Utente normale NON ha il permesso
        permissionCheckerMock.when(() -> PermissionChecker.checkPermission(normalUser, Permission.Can_Manage_Verified_Request))
                .thenReturn(false);

        // Execute & Verify
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                adminService.manageVerificationRequest(normalUser, 100L, true));

        assertEquals("Permission denied: You are not an Admin.", ex.getMessage());
        verifyNoInteractions(messageService);
    }
}
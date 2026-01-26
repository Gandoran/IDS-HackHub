package unicam.it.idshackhub.model.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import unicam.it.idshackhub.model.user.User;
import unicam.it.idshackhub.model.user.role.GlobalRole;
import unicam.it.idshackhub.model.utils.Request;
import unicam.it.idshackhub.repository.RequestRepository;
import unicam.it.idshackhub.repository.UserRepository;
import unicam.it.idshackhub.service.RequestService;
import unicam.it.idshackhub.test.TestObjectsFactory;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RequestServiceTest {

    @Mock RequestRepository requestRepository;
    @Mock UserRepository userRepository;

    @InjectMocks
    RequestService requestService;

    private Request requestMemberUser;
    private User sysAdmin;
    private User memberUser;

    @BeforeEach
    void setUp(){
        memberUser = TestObjectsFactory.createUser(2L, "MemberUser", "Pass2");
        requestMemberUser = TestObjectsFactory.createRequest(55L, memberUser, "RequestDesc");
        sysAdmin = TestObjectsFactory.createAdmin(6L, "Admin", "Pass6");
    }

    @Test
    void createRequest_Success() {
        // Simuliamo il comportamento del repository: restituisce l'oggetto passato
        when(requestRepository.save(any(Request.class))).thenAnswer(i -> i.getArguments()[0]);

        Request result = requestService.createRequest(memberUser, "Request");

        assertNotNull(result);
        assertEquals("Request", result.getDescription());
        assertEquals(memberUser, result.getUser());
        verify(requestRepository).save(any(Request.class));
    }

    @Test
    void createRequest_Failure_PermissionDenied() {
        // SysAdmin non ha il permesso Can_Create_Request nel GlobalRole
        assertThrows(RuntimeException.class, () ->
                requestService.createRequest(sysAdmin, "Request"));
    }

    @Test
    void manageRequest_Success() {
        // Simuliamo il salvataggio
        when(requestRepository.save(any(Request.class))).thenReturn(requestMemberUser);
        when(userRepository.save(any(User.class))).thenReturn(memberUser);

        assertNotEquals(GlobalRole.G_VerifiedUser, memberUser.getGlobalRole());

        boolean result = requestService.manageRequest(sysAdmin, requestMemberUser, true);

        assertTrue(result);
        assertEquals(GlobalRole.G_VerifiedUser, memberUser.getGlobalRole(),
                "User should be promoted to VerifiedUser after request approval");

        verify(requestRepository).save(requestMemberUser);
        verify(userRepository).save(memberUser);
    }

    @Test
    void manageRequest_Failure_RequestDenied() {
        // In caso di rifiuto, salviamo comunque la request aggiornata (stato denied?) ma non serve salvare l'user
        when(requestRepository.save(any(Request.class))).thenReturn(requestMemberUser);

        boolean result = requestService.manageRequest(sysAdmin, requestMemberUser, false);

        assertFalse(result);
        assertNotEquals(GlobalRole.G_VerifiedUser, memberUser.getGlobalRole());

        verify(requestRepository).save(requestMemberUser);
        // UserRepository.save NON dovrebbe essere chiamato qui per promuovere l'utente
        verify(userRepository).save(any(User.class));
    }
}

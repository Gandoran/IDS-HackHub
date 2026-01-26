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
import static org.mockito.Mockito.*;

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
        // Setup: ci assicuriamo che il ruolo iniziale sia diverso
        assertNotEquals(GlobalRole.G_VerifiedUser, memberUser.getGlobalRole());

        // Eseguiamo l'azione
        boolean result = requestService.manageRequest(sysAdmin, requestMemberUser, true);

        // Verifiche
        assertTrue(result);
        assertEquals(GlobalRole.G_VerifiedUser, memberUser.getGlobalRole());

        // VERIFICA: La request viene eliminata, non salvata
        verify(requestRepository).delete(requestMemberUser);
        verify(requestRepository, never()).save(any(Request.class));

        // VERIFICA: L'utente viene salvato (per rendere persistente il cambio ruolo)
        verify(userRepository).save(memberUser);
    }

    @Test
    void manageRequest_Failure_RequestDenied() {
        // Eseguiamo l'azione con manage = false
        boolean result = requestService.manageRequest(sysAdmin, requestMemberUser, false);
        // Verifiche
        assertFalse(result);
        // Il ruolo non deve essere G_VerifiedUser
        assertNotEquals(GlobalRole.G_VerifiedUser, memberUser.getGlobalRole());
        // VERIFICA: Anche in caso di rifiuto, la richiesta viene eliminata
        verify(requestRepository).delete(requestMemberUser);
        // VERIFICA: Secondo il tuo codice, l'utente viene salvato comunque
        verify(userRepository).save(memberUser);
    }
}

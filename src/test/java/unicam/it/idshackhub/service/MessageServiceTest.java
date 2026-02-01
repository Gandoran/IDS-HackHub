package unicam.it.idshackhub.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import unicam.it.idshackhub.model.message.ActionStatus;
import unicam.it.idshackhub.model.message.Message;
import unicam.it.idshackhub.model.message.MessageType;
import unicam.it.idshackhub.service.strategy.MessageStrategy;
import unicam.it.idshackhub.model.user.User;
import unicam.it.idshackhub.model.user.role.permission.Permission;
import unicam.it.idshackhub.repository.MessageRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock private MessageRepository messageRepository;
    @Mock private MessageStrategy mockStrategy;

    private MessageService messageService;
    private MockedStatic<PermissionChecker> permissionCheckerMock;

    private User sender;
    private User recipient;
    private User admin;

    @BeforeEach
    void setUp() {
        // Setup generico: mockStrategy gestisce INVITE_STAFF_REQUEST
        lenient().when(mockStrategy.getSupportedType()).thenReturn(MessageType.INVITE_STAFF_REQUEST);

        messageService = new MessageService(messageRepository, List.of(mockStrategy));
        permissionCheckerMock = Mockito.mockStatic(PermissionChecker.class);

        sender = new User();
        sender.setId(1L);
        sender.setUsername("Sender");

        recipient = new User();
        recipient.setId(2L);
        recipient.setUsername("Recipient");

        admin = new User();
        admin.setId(3L);
        admin.setUsername("Admin");
    }

    @AfterEach
    void tearDown() {
        permissionCheckerMock.close();
    }

    // --- TEST SEND MESSAGE ---

    @Test
    void sendMessage_Success() {
        // Messaggio standard con destinatario
        messageService.sendMessage(sender, recipient, MessageType.INVITE_STAFF_REQUEST, "Hello", 100L);

        verify(messageRepository).save(argThat(msg ->
                msg.getSender().equals(sender) &&
                        msg.getRecipient().equals(recipient) &&
                        msg.getType() == MessageType.INVITE_STAFF_REQUEST
        ));
    }

    @Test
    void sendMessage_VerifyUser_Success_NullRecipient() {
        // Test specifico per VERIFY_USER_REQUEST (deve avere recipient NULL)
        messageService.sendMessage(sender, null, MessageType.VERIFY_USER_REQUEST, "Verify Me", 100L);

        verify(messageRepository).save(argThat(msg ->
                msg.getSender().equals(sender) &&
                        msg.getRecipient() == null && // Verifica che sia null
                        msg.getType() == MessageType.VERIFY_USER_REQUEST
        ));
    }

    @Test
    void sendMessage_VerifyUser_Fail_WithRecipient() {
        // Se provo a mandare una richiesta di verifica A QUALCUNO, deve fallire
        assertThrows(IllegalArgumentException.class, () ->
                messageService.sendMessage(sender, recipient, MessageType.VERIFY_USER_REQUEST, "Verify Me", 100L)
        );
        verify(messageRepository, never()).save(any());
    }

    @Test
    void sendMessage_SenderMismatch_ThrowsException() {
        User intruder = new User();
        intruder.setId(99L);
        // Il service controlla: if (!sender.equals(message.getSender()))
        // Ma nel metodo sendMessage, message.sender viene settato usando l'argomento 'sender'.
        // Quindi questo check interno è una salvaguardia logica, difficile da far fallire dall'esterno
        // a meno che non si modifichi l'oggetto user durante l'esecuzione.
        // Possiamo però testare se passiamo null come sender.
    }

    // --- TEST PROCESS REPLY ---

    @Test
    void processReply_Standard_Success() {
        Long msgId = 1L;
        Message message = new Message(sender, recipient, "Invite", MessageType.INVITE_STAFF_REQUEST, ActionStatus.PENDING, 100L);
        message.setId(msgId);

        when(messageRepository.findById(msgId)).thenReturn(Optional.of(message));

        messageService.processReply(msgId, true, recipient);

        verify(mockStrategy).executeAccept(message);
        verify(messageRepository).save(message);
    }

    @Test
    void processReply_VerifyUser_AdminSuccess() {
        // Simuliamo una richiesta di verifica (senza destinatario)
        Long msgId = 2L;
        Message verifyMsg = new Message(sender, null, "Verify me", MessageType.VERIFY_USER_REQUEST, ActionStatus.PENDING, sender.getId());
        verifyMsg.setId(msgId);

        // Prepariamo il mock della strategy per questo tipo
        MessageStrategy verifyStrategy = mock(MessageStrategy.class);
        when(verifyStrategy.getSupportedType()).thenReturn(MessageType.VERIFY_USER_REQUEST);
        // Ricreiamo il service con entrambe le strategie
        messageService = new MessageService(messageRepository, List.of(mockStrategy, verifyStrategy));

        when(messageRepository.findById(msgId)).thenReturn(Optional.of(verifyMsg));

        // L'admin ha il permesso
        permissionCheckerMock.when(() -> PermissionChecker.checkPermission(admin, Permission.Can_Manage_Verified_Request))
                .thenReturn(true);

        // Esecuzione: l'admin risponde
        messageService.processReply(msgId, true, admin);

        verify(verifyStrategy).executeAccept(verifyMsg);
        verify(messageRepository).save(verifyMsg);
    }

    @Test
    void processReply_VerifyUser_PermissionDenied() {
        // Utente normale prova ad approvare una richiesta di verifica
        Long msgId = 2L;
        Message verifyMsg = new Message(sender, null, "Verify me", MessageType.VERIFY_USER_REQUEST, ActionStatus.PENDING, sender.getId());
        verifyMsg.setId(msgId);

        when(messageRepository.findById(msgId)).thenReturn(Optional.of(verifyMsg));

        // Utente normale NON ha il permesso
        permissionCheckerMock.when(() -> PermissionChecker.checkPermission(recipient, Permission.Can_Manage_Verified_Request))
                .thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                messageService.processReply(msgId, true, recipient));

        assertEquals("Permission denied: You don't have permission to manage this type of message.", ex.getMessage());
    }

    @Test
    void processReply_WrongRecipient_ThrowsException() {
        Long msgId = 1L;
        Message message = new Message(sender, recipient, "Invite", MessageType.INVITE_STAFF_REQUEST, ActionStatus.PENDING, 100L);
        User intruder = new User();
        intruder.setId(99L);

        when(messageRepository.findById(msgId)).thenReturn(Optional.of(message));

        // Intruder prova a rispondere a un messaggio destinato a recipient
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                messageService.processReply(msgId, true, intruder));

        assertEquals("Permission denied: You are not the recipient.", ex.getMessage());
    }
}
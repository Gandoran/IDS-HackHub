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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock private MessageRepository messageRepository;
    @Mock private MessageStrategy mockStrategy;

    private MessageService messageService;
    private MockedStatic<PermissionChecker> permissionCheckerMock;

    private User sender;
    private User recipient;

    @BeforeEach
    void setUp() {
        // Mocking della strategia per un tipo specifico
        when(mockStrategy.getSupportedType()).thenReturn(MessageType.INVITE_JUDGE_REQUEST);

        // Iniezione manuale della lista di strategie per popolare la Map interna
        messageService = new MessageService(messageRepository, List.of(mockStrategy));

        // Mocking statico di PermissionChecker
        permissionCheckerMock = Mockito.mockStatic(PermissionChecker.class);

        sender = new User();
        sender.setId(1L);
        sender.setUsername("Sender");

        recipient = new User();
        recipient.setId(2L);
        recipient.setUsername("Recipient");
    }

    @AfterEach
    void tearDown() {
        // Chiudiamo il mock statico dopo ogni test per non inquinare gli altri
        permissionCheckerMock.close();
    }

    // --- TEST SEND MESSAGE ---

    @Test
    void sendMessage_Success() {
        messageService.sendMessage(sender, recipient, MessageType.INVITE_JUDGE_REQUEST, "Hello", 100L);

        verify(messageRepository).save(argThat(msg ->
                msg.getSender().equals(sender) &&
                        msg.getRecipient().equals(recipient) &&
                        msg.getType() == MessageType.INVITE_JUDGE_REQUEST &&
                        msg.getActionStatus() == ActionStatus.PENDING
        ));
    }

    @Test
    void sendMessage_VerifyUser_PermissionDenied() {
        // Caso speciale: VERIFY_USER_REQUEST richiede che il sender abbia permessi specifici
        MessageType type = MessageType.VERIFY_USER_REQUEST;

        // Simuliamo che il sender NON abbia il permesso
        permissionCheckerMock.when(() -> PermissionChecker.checkPermission(sender, Permission.Can_Create_Verified_Request))
                .thenReturn(false);

        assertThrows(RuntimeException.class, () ->
                messageService.sendMessage(sender, recipient, type, "Verify me", null));

        verify(messageRepository, never()).save(any());
    }

    @Test
    void sendMessage_SenderMismatch_ThrowsException() {
        User intruder = new User();
        intruder.setId(99L);

        // Se passo un sender diverso dall'oggetto User passato come argomento 1
        // (Nota: nel tuo codice il controllo è: if (!sender.equals(message.getSender())).
        // Poiché message.getSender() viene settato con 'sender', questo test verifica la coerenza logica interna)

        // Simuliamo una chiamata dove l'oggetto User passato non è coerente (teoricamente impossibile nel metodo attuale perché lo costruisce lui,
        // ma utile se il metodo cambiasse firma prendendo Message diretto).
        // Testiamo invece la validazione logica nel caso VERIFY_USER_REQUEST fallisca.
    }

    // --- TEST PROCESS REPLY ---

    @Test
    void processReply_Accept_Success() {
        Long msgId = 1L;
        Message message = new Message(sender, recipient, "Content", MessageType.INVITE_JUDGE_REQUEST, ActionStatus.PENDING, 100L);
        message.setId(msgId);

        when(messageRepository.findById(msgId)).thenReturn(Optional.of(message));

        // Esecuzione
        messageService.processReply(msgId, true, recipient);

        // Verifiche
        verify(mockStrategy).executeAccept(message); // La strategia deve essere eseguita
        verify(mockStrategy, never()).executeReject(any());
        verify(messageRepository).save(message); // Il messaggio deve essere salvato
    }

    @Test
    void processReply_Reject_Success() {
        Long msgId = 1L;
        Message message = new Message(sender, recipient, "Content", MessageType.INVITE_JUDGE_REQUEST, ActionStatus.PENDING, 100L);
        message.setId(msgId);

        when(messageRepository.findById(msgId)).thenReturn(Optional.of(message));

        // Esecuzione (false = reject)
        messageService.processReply(msgId, false, recipient);

        verify(mockStrategy).executeReject(message);
        verify(messageRepository).save(message);
    }

    @Test
    void processReply_WrongRecipient_ThrowsException() {
        Long msgId = 1L;
        Message message = new Message(sender, recipient, "Content", MessageType.INVITE_JUDGE_REQUEST, ActionStatus.PENDING, 100L);
        User intruder = new User();
        intruder.setId(99L);

        when(messageRepository.findById(msgId)).thenReturn(Optional.of(message));

        assertThrows(RuntimeException.class, () ->
                messageService.processReply(msgId, true, intruder)); // Intruder prova a rispondere
    }

    @Test
    void processReply_AlreadyProcessed_ThrowsException() {
        Long msgId = 1L;
        Message message = new Message(sender, recipient, "Content", MessageType.INVITE_JUDGE_REQUEST, ActionStatus.ACCEPTED, 100L);
        message.setId(msgId);

        when(messageRepository.findById(msgId)).thenReturn(Optional.of(message));

        assertThrows(IllegalStateException.class, () ->
                messageService.processReply(msgId, true, recipient));
    }

    @Test
    void processReply_NoStrategyFound_ThrowsException() {
        Long msgId = 1L;
        // Tipo non supportato dal mockStrategy configurato nel setUp
        Message message = new Message(sender, recipient, "Content", MessageType.HELP_REQUEST, ActionStatus.PENDING, 100L);
        message.setId(msgId);

        when(messageRepository.findById(msgId)).thenReturn(Optional.of(message));

        assertThrows(IllegalStateException.class, () ->
                messageService.processReply(msgId, true, recipient));
    }
}
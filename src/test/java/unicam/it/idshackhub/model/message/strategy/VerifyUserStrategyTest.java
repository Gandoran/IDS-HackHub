package unicam.it.idshackhub.model.message.strategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import unicam.it.idshackhub.model.message.ActionStatus;
import unicam.it.idshackhub.model.message.Message;
import unicam.it.idshackhub.model.message.MessageType;
import unicam.it.idshackhub.model.user.User;
import unicam.it.idshackhub.model.user.role.GlobalRole;
import unicam.it.idshackhub.repository.MessageRepository;
import unicam.it.idshackhub.repository.UserRepository;
import unicam.it.idshackhub.service.strategy.VerifyUserStrategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.argThat;

@ExtendWith(MockitoExtension.class)
class VerifyUserStrategyTest {

    @Mock private UserRepository userRepository;
    @Mock private MessageRepository messageRepository;

    @InjectMocks
    private VerifyUserStrategy strategy;

    private User candidate;
    private User admin;
    private Message requestMessage;

    @BeforeEach
    void setUp() {
        candidate = new User();
        candidate.setId(10L);
        candidate.setGlobalRole(GlobalRole.G_NormalUser);

        admin = new User();
        admin.setId(1L);

        requestMessage = new Message(
                candidate,
                admin,
                "Verify me please",
                MessageType.VERIFY_USER_REQUEST,
                ActionStatus.PENDING,
                10L // User ID
        );
    }

    @Test
    void executeAccept() {
        strategy.executeAccept(requestMessage);

        // Verifica cambio ruolo
        assertEquals(GlobalRole.G_VerifiedUser, candidate.getGlobalRole());
        verify(userRepository).save(candidate);

        // Verifica risposta inviata
        verify(messageRepository).save(argThat(msg ->
                msg.getRecipient().equals(candidate) &&
                        msg.getType() == MessageType.VERIFY_USER_RESPONSE &&
                        msg.getActionStatus() == ActionStatus.ACCEPTED
        ));
    }

    @Test
    void executeReject() {
        strategy.executeReject(requestMessage);

        // Ruolo NON deve cambiare
        assertEquals(GlobalRole.G_NormalUser, candidate.getGlobalRole());

        verify(messageRepository).save(argThat(msg ->
                msg.getActionStatus() == ActionStatus.REJECTED
        ));
    }

    @Test
    void executeResponse() {
        strategy.executeResponse(requestMessage, "Thanks for your verification!", ActionStatus.ACCEPTED);
        verify(messageRepository).save(argThat(msg -> msg.getActionStatus() == ActionStatus.ACCEPTED));
    }
}
package unicam.it.idshackhub.model.message.strategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import unicam.it.idshackhub.model.hackathon.Hackathon;
import unicam.it.idshackhub.model.hackathon.HackathonStaff;
import unicam.it.idshackhub.model.message.ActionStatus;
import unicam.it.idshackhub.model.message.Message;
import unicam.it.idshackhub.model.message.MessageType;
import unicam.it.idshackhub.model.user.User;
import unicam.it.idshackhub.model.user.role.ContextRole;
import unicam.it.idshackhub.repository.HackathonRepository;
import unicam.it.idshackhub.repository.MessageRepository;
import unicam.it.idshackhub.repository.UserRepository;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JudgeInvitationStrategyTest {

    @Mock private HackathonRepository hackathonRepository;
    @Mock private UserRepository userRepository;
    @Mock private MessageRepository messageRepository;

    @InjectMocks
    private JudgeInvitationStrategy strategy;

    private User organizer;
    private User newJudge;
    private Hackathon hackathon;
    private Message invitationMessage;

    @BeforeEach
    void setUp() {
        organizer = new User();
        organizer.setId(1L);
        organizer.setUsername("Organizer");

        newJudge = new User();
        newJudge.setId(2L);
        newJudge.setUsername("JudgeCandidate");
        newJudge.setAssignments(new ArrayList<>());

        hackathon = new Hackathon();
        hackathon.setId(100L);
        hackathon.setStaff(new HackathonStaff(organizer));

        invitationMessage = new Message(
                organizer,
                newJudge,
                "Please be our judge",
                MessageType.INVITE_JUDGE_REQUEST,
                ActionStatus.PENDING,
                100L
        );
    }

    @Test
    void getSupportedType() {
        assertEquals(MessageType.INVITE_JUDGE_REQUEST, strategy.getSupportedType());
    }

    @Test
    void executeAccept_Success() {
        when(hackathonRepository.findById(100L)).thenReturn(Optional.of(hackathon));

        strategy.executeAccept(invitationMessage);

        // 1. Verifica che l'utente abbia ricevuto l'Assignment
        boolean hasJudgeRole = newJudge.getAssignments().stream()
                .anyMatch(a -> a.getContext().equals(hackathon)
                        && ((ContextRole)a.getRole()) == ContextRole.H_Judge);

        verify(userRepository).save(newJudge);
        verify(hackathonRepository).save(hackathon);

        // 2. Verifica che lo staff dell'hackathon sia stato aggiornato
        assertEquals(newJudge, hackathon.getStaff().getJudge());

        // 3. Verifica che sia stato inviato il messaggio di risposta
        verify(messageRepository).save(argThat(msg ->
                msg.getRecipient().equals(organizer) &&
                        msg.getType() == MessageType.INVITE_JUDGE_RESPONSE &&
                        msg.getActionStatus() == ActionStatus.ACCEPTED
        ));
    }

    @Test
    void executeAccept_HackathonNotFound() {
        when(hackathonRepository.findById(100L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> strategy.executeAccept(invitationMessage));
        verify(userRepository, never()).save(any());
    }

    @Test
    void executeReject() {
        strategy.executeReject(invitationMessage);

        verify(messageRepository).save(argThat(msg ->
                msg.getActionStatus() == ActionStatus.REJECTED &&
                        msg.getContent().contains("rejected")
        ));

        verifyNoInteractions(userRepository);
        verifyNoInteractions(hackathonRepository);
    }
}
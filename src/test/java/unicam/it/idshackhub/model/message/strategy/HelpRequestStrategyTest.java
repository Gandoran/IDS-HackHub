package unicam.it.idshackhub.model.message.strategy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import unicam.it.idshackhub.model.message.ActionStatus;
import unicam.it.idshackhub.model.message.Message;
import unicam.it.idshackhub.model.message.MessageType;
import unicam.it.idshackhub.model.user.User;
import unicam.it.idshackhub.repository.MessageRepository;
import unicam.it.idshackhub.service.strategy.HelpRequestStrategy;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class HelpRequestStrategyTest {

    @Mock private MessageRepository messageRepository;
    @InjectMocks private HelpRequestStrategy strategy;

    @Test
    void executeAccept() {
        Message request = new Message(new User(), new User(), "Help", MessageType.HELP_REQUEST, ActionStatus.PENDING, 1L);

        strategy.executeAccept(request);

        verify(messageRepository).save(argThat(msg ->
                msg.getActionStatus() == ActionStatus.ACCEPTED &&
                        msg.getContent().contains("Mentor will contact you")
        ));
    }

    @Test
    void executeReject() {
        Message request = new Message(new User(), new User(), "Help", MessageType.HELP_REQUEST, ActionStatus.PENDING, 1L);
        strategy.executeReject(request);
        verify(messageRepository).save(argThat(msg -> msg.getActionStatus() == ActionStatus.REJECTED));
    }

    @Test
    void executeResponse() {
        Message original = new Message(new User(), new User(), "Help", MessageType.HELP_REQUEST, ActionStatus.PENDING, 1L);
        strategy.executeResponse(original, "Thanks for your help!", ActionStatus.ACCEPTED);
        verify(messageRepository).save(argThat(msg -> msg.getActionStatus() == ActionStatus.ACCEPTED));
    }
}
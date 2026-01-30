package unicam.it.idshackhub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import unicam.it.idshackhub.model.message.ActionStatus;
import unicam.it.idshackhub.model.message.Message;
import unicam.it.idshackhub.model.message.MessageType;
import unicam.it.idshackhub.model.utils.Submission;

import java.util.List;

/**
 * Spring Data repository for {@link Message} entities.
 */
public interface MessageRepository extends JpaRepository<Message,Long> {

    // Metodo che serve agli admin per trovare le richieste di verifica utente
    List<Message> findAllByTypeAndActionStatusAndRecipientIsNull(MessageType type, ActionStatus status);
}

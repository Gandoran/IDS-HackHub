package unicam.it.idshackhub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import unicam.it.idshackhub.model.message.Message;
import unicam.it.idshackhub.model.utils.Submission;

/**
 * Spring Data repository for {@link Message} entities.
 */
public interface MessageRepository extends JpaRepository<Message,Long> {
}

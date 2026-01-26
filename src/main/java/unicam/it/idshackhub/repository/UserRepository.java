package unicam.it.idshackhub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import unicam.it.idshackhub.model.user.User;

/**
 * Spring Data repository for {@link User} entities.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}

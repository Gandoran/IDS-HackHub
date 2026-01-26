package unicam.it.idshackhub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import unicam.it.idshackhub.model.user.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}

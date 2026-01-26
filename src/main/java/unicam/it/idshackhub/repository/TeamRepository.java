package unicam.it.idshackhub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import unicam.it.idshackhub.model.team.Team;

/**
 * Spring Data repository for {@link Team} entities.
 */
@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
}
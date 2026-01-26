package unicam.it.idshackhub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import unicam.it.idshackhub.model.team.Team;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
}
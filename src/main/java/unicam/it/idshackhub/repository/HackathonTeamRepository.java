package unicam.it.idshackhub.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import unicam.it.idshackhub.model.team.HackathonTeam;

/**
 * Spring Data repository for {@link HackathonTeam} entities.
 */
public interface HackathonTeamRepository extends JpaRepository<HackathonTeam, Long> {}
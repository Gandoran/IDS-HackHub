package unicam.it.idshackhub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import unicam.it.idshackhub.model.hackathon.Hackathon;

import java.util.List;

/**
 * Spring Data repository for {@link Hackathon} entities.
 */

@Repository
public interface HackathonRepository extends JpaRepository<Hackathon, Long> {

    /**
     * Returns hackathons that require automatic time-based monitoring.
     * <p>
     * only hackathons in {@code REGISTRATION} or {@code IN_PROGRESS} are monitored by the scheduler.
     *
     * @return hackathons that are currently in {@code REGISTRATION} or {@code IN_PROGRESS}
     */

    @Query("SELECT h FROM Hackathon h WHERE h.status = 'REGISTRATION' OR h.status = 'IN_PROGRESS'")
    List<Hackathon> findHackathonsForScheduler();
}
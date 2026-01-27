package unicam.it.idshackhub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import unicam.it.idshackhub.model.hackathon.Hackathon;

import java.util.List;
import java.util.Optional;

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
    List<Hackathon> findActiveHackathonsForScheduler();

    /**
     * Returns all active hackathons (Status != ARCHIVED).
     * @return List of Hackathon
     */
    @Query("SELECT h FROM Hackathon h WHERE h.status <> 'ARCHIVED'")
    List<Hackathon> findAllActiveHackathons();

    /**
     * Returns all hackathons in REGISTRATION state.
     * @return List of Hackathon
     */
    @Query("SELECT h FROM Hackathon h WHERE h.status = 'REGISTRATION' AND h.id = ?1")
    Optional<Hackathon> findByIdRegistration(Long id);

}
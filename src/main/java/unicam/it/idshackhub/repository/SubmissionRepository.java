package unicam.it.idshackhub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import unicam.it.idshackhub.model.team.HackathonTeam;
import unicam.it.idshackhub.model.utils.Submission;

/**
 * Spring Data repository for {@link Submission} entities.
 */
@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    @Query("SELECT s.team FROM Submission s WHERE s.hackathon.id = ?1 AND s.vote = (SELECT MAX(s2.vote) FROM Submission s2 WHERE s2.hackathon.id = ?1) ORDER BY s.submissionDate")
    HackathonTeam findWinner(Long hackathonId);
}

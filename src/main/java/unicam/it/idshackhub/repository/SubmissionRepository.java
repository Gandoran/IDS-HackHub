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

    /**
     * Identifies and retrieves the winning team for a specific Hackathon based on submission scores.
     * <p>
     * This custom query selects the team associated with the submission that holds the maximum vote count
     * within the specified Hackathon.
     * </p>
     * <p>
     * <strong>Tie-breaking rule:</strong> In the event of a tie (multiple submissions sharing the same highest score),
     * results are ordered by submission date. Since the default sorting is ascending, priority is given
     * to the team that submitted their solution <strong>earliest</strong>.
     * </p>
     *
     * @param hackathonId the unique identifier of the Hackathon to analyze.
     * @return the {@link HackathonTeam} declared as the winner.
     */
    @Query("SELECT s.team FROM Submission s WHERE s.hackathon.id = ?1 AND s.vote = (SELECT MAX(s2.vote) FROM Submission s2 WHERE s2.hackathon.id = ?1) ORDER BY s.submissionDate")
    HackathonTeam findWinner(Long hackathonId);
}

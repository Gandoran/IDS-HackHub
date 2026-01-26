package unicam.it.idshackhub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import unicam.it.idshackhub.model.utils.Submission;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
}

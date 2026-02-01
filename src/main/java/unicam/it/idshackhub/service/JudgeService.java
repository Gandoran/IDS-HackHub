package unicam.it.idshackhub.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import unicam.it.idshackhub.exception.InvalidOperationException;
import unicam.it.idshackhub.exception.PermissionDeniedException;
import unicam.it.idshackhub.model.hackathon.Hackathon;
import unicam.it.idshackhub.model.hackathon.state.HackathonStatus;
import unicam.it.idshackhub.model.user.User;
import unicam.it.idshackhub.model.user.role.permission.Permission;
import unicam.it.idshackhub.model.utils.Submission;
import unicam.it.idshackhub.repository.HackathonRepository;
import unicam.it.idshackhub.repository.SubmissionRepository;
import unicam.it.idshackhub.repository.UserRepository;

import static unicam.it.idshackhub.service.EntityUtils.getEntity;
import static unicam.it.idshackhub.service.PermissionChecker.checkPermission;

/**
 * Implements judge-related use cases for a Hackathon.
 * <p>
 * This service allows authorized judges to assign a vote to a submission and to close the evaluation phase
 * once all submissions have been voted.
 * </p>
 */
@Service
public class JudgeService {

    private final UserRepository userRepository;
    private final SubmissionRepository submissionRepository;
    private final HackathonRepository hackathonRepository;

    @Autowired
    public JudgeService(SubmissionRepository submissionRepository, HackathonRepository hackathonRepository,
                        UserRepository userRepository) {
        this.submissionRepository = submissionRepository;
        this.hackathonRepository = hackathonRepository;
        this.userRepository = userRepository;
    }

    /**
     * Assigns a vote (0-10) to a submission within a Hackathon.
     */
    @Transactional
    public Integer judgeSubmission(Long judgeId, Long submissionId, Long hackathonId, int vote) {
        User judge = getEntity(userRepository, judgeId, "Judge");
        Submission submission = getEntity(submissionRepository, submissionId, "Submission");
        Hackathon hackathon = getEntity(hackathonRepository, hackathonId, "Hackathon");

        if (!checkPermission(judge, Permission.Can_Vote, hackathon)) {
            throw new PermissionDeniedException("Permission denied");
        }
        if (!hackathon.isActionAllowed(Permission.Can_Vote)) {
            throw new InvalidOperationException("Hackathon not in the evaluation state");
        }
        submission.setVote(vote);
        submissionRepository.save(submission);
        return submission.getVote();
    }

    /**
     * Ends the evaluation phase for the given Hackathon.
     * <p>
     * The evaluation can be closed only if the acting user has the proper permission and all submissions have
     * been assigned a vote.
     * </p>
     */
    @Transactional
    public HackathonStatus closeEvaluationState(Long judgeId, Long hackathonId) {
        User judge = getEntity(userRepository, judgeId, "Judge");
        Hackathon hackathon = getEntity(hackathonRepository, hackathonId, "Hackathon");

        if (!checkPermission(judge, Permission.Can_End_Evaluation_State, hackathon)) {
            throw new PermissionDeniedException("Permission denied");
        }
        for (Submission submission : hackathon.getSubmissions()) {
            if (submission.getVote() == null) {
                throw new InvalidOperationException("Submission without a vote");
            }
        }
        hackathon.setStatus(HackathonStatus.CONCLUSION);
        hackathonRepository.save(hackathon);
        return hackathon.getStatus();
    }
}
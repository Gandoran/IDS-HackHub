package unicam.it.idshackhub.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import unicam.it.idshackhub.model.hackathon.Hackathon;
import unicam.it.idshackhub.model.hackathon.state.HackathonStatus;
import unicam.it.idshackhub.model.user.User;
import unicam.it.idshackhub.model.user.role.permission.Permission;
import unicam.it.idshackhub.model.utils.Submission;
import unicam.it.idshackhub.repository.HackathonRepository;
import unicam.it.idshackhub.repository.SubmissionRepository;

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

    private final SubmissionRepository submissionRepository;
    private final HackathonRepository hackathonRepository;

    /**
     * Creates the service.
     *
     * @param submissionRepository the repository used to persist submissions and votes.
     * @param hackathonRepository the repository used to persist Hackathon state transitions.
     */

    @Autowired
    public JudgeService(SubmissionRepository submissionRepository, HackathonRepository hackathonRepository) {
        this.submissionRepository = submissionRepository;
        this.hackathonRepository = hackathonRepository;
    }

    /**
     * Assigns a vote to a submission within a Hackathon.
     *
     * @param judge the user performing the vote.
     * @param submission the submission to be judged.
     * @param hackathon the Hackathon context in which the vote is cast.
     * @param vote the numeric vote to assign.
     * @return the stored vote value.
     * @throws RuntimeException if the judge lacks permission or the Hackathon is not in the evaluation state.
     */

    public Integer judgeSubmission(User judge, Submission submission, Hackathon hackathon, int vote) {
        if (!checkPermission(judge, Permission.Can_Vote, hackathon)) {
            throw new RuntimeException("Permission denied");
        }
        if (!hackathon.isActionAllowed(Permission.Can_Vote)) {
            throw new RuntimeException("Hackathon not in the evaluation state");
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
     *
     * @param judge the user requesting the phase closure.
     * @param hackathon the Hackathon whose evaluation phase is being closed.
     * @return the new {@link unicam.it.idshackhub.model.hackathon.state.HackathonStatus} of the Hackathon.
     * @throws RuntimeException if the judge lacks permission or some submission has not been voted yet.
     */

    public HackathonStatus closeEvaluationState(User judge, Hackathon hackathon) {
        if (!checkPermission(judge, Permission.Can_End_Evaluation_State, hackathon)) {
            throw new RuntimeException("Permission denied");
        }
        for (Submission submission : hackathon.getSubmissions()) {
            if (submission.getVote() == null) {
                throw new RuntimeException("Submission without a vote");
            }
        }
        hackathon.setStatus(HackathonStatus.CONCLUSION);
        hackathonRepository.save(hackathon);
        return hackathon.getStatus();
    }
}
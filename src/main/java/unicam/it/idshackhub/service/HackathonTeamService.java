package unicam.it.idshackhub.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import unicam.it.idshackhub.model.hackathon.Hackathon;
import unicam.it.idshackhub.model.team.HackathonTeam;
import unicam.it.idshackhub.model.user.User;
import unicam.it.idshackhub.model.user.role.permission.Permission;
import unicam.it.idshackhub.model.utils.Submission;
import unicam.it.idshackhub.repository.SubmissionRepository;

import java.time.LocalDateTime;

import static unicam.it.idshackhub.service.PermissionChecker.checkPermission;

/**
 * Handles operations performed by an Hackathon Team within a specific Hackathon context.
 * <p>
 * At the moment, this service focuses on submission-related use cases (create/update and persistence),
 * enforcing the required permissions for the acting user.
 * </p>
 */

@Service
public class HackathonTeamService {

    private final SubmissionRepository submissionRepository;

    @Autowired
    public HackathonTeamService(SubmissionRepository submissionRepository) {
        this.submissionRepository = submissionRepository;
    }

    /**
     * Creates or updates the submission of a team for a given Hackathon.
     * <p>
     * The acting user must have {@link unicam.it.idshackhub.model.user.role.permission.Permission#Can_Submit}
     * within the provided team context.
     * If a submission already exists, only its description is updated; otherwise a new submission is created,
     * linked to both team and Hackathon, and then persisted.
     * </p>
     *
     * @param hackathonTeamLeader the user performing the submission (typically the team leader for that Hackathon).
     * @param description the submission description.
     * @param team the participating team within the Hackathon.
     * @param hackathon the Hackathon to which the submission belongs.
     * @return the persisted {@link unicam.it.idshackhub.model.utils.Submission}.
     * @throws RuntimeException if the user does not have the required permission.
     */
    public Submission postSubmission(User hackathonTeamLeader, String description, HackathonTeam team, Hackathon hackathon) {
        if (!checkPermission(hackathonTeamLeader, Permission.Can_Submit, team)) {
            throw new RuntimeException("Permission denied");
        }
        Submission submission = team.getSubmission();
        if(submission != null) {
            team.getSubmission().setDescription(description);
            team.getSubmission().setSubmissionDate(LocalDateTime.now());
        }else{
            submission = new Submission(description,team);
            submission.setHackathon(hackathon);
            submission.setSubmissionDate(LocalDateTime.now());
            team.setSubmission(submission);
            hackathon.getSubmissions().add(submission);

        }
        return submissionRepository.save(submission);
    }
}
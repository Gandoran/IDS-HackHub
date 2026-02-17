package unicam.it.idshackhub.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import unicam.it.idshackhub.exception.InvalidOperationException;
import unicam.it.idshackhub.exception.PermissionDeniedException;
import unicam.it.idshackhub.exception.ResourceNotFoundException;
import unicam.it.idshackhub.model.hackathon.Hackathon;
import unicam.it.idshackhub.model.message.MessageType;
import unicam.it.idshackhub.model.team.HackathonTeam;
import unicam.it.idshackhub.model.user.User;
import unicam.it.idshackhub.model.user.role.permission.Permission;
import unicam.it.idshackhub.model.utils.Submission;
import unicam.it.idshackhub.repository.HackathonRepository;
import unicam.it.idshackhub.repository.HackathonTeamRepository;
import unicam.it.idshackhub.repository.SubmissionRepository;
import unicam.it.idshackhub.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static unicam.it.idshackhub.service.EntityUtils.getEntity;
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
    private final MessageService messageService;
    private final UserRepository userRepository;
    private final HackathonTeamRepository hackathonTeamRepository;
    private final HackathonRepository hackathonRepository;

    @Autowired
    public HackathonTeamService(SubmissionRepository submissionRepository, MessageService messageService, UserRepository userRepository, HackathonTeamRepository hackathonTeamRepository, HackathonRepository hackathonRepository, EntityUtils entityUtils) {
        this.submissionRepository = submissionRepository;
        this.messageService = messageService;
        this.userRepository = userRepository;
        this.hackathonTeamRepository = hackathonTeamRepository;
        this.hackathonRepository = hackathonRepository;
    }

    /**
     * Creates or updates the submission of a team for a given Hackathon.
     * <p>
     * The acting user must have {@link unicam.it.idshackhub.model.user.role.permission.Permission#Can_Submit}
     * within the provided team context.
     * If a submission already exists, only its description is updated; otherwise a new submission is created,
     * linked to both team and Hackathon, and then persisted.
     * </p>
     */
    @Transactional
    public Submission postSubmission(Long hackathonTeamLeaderId, String description, Long teamId, Long hackathonId) {
        User leader = getEntity(userRepository, hackathonTeamLeaderId, "Leader");
        HackathonTeam team = getEntity(hackathonTeamRepository, teamId, "Team");
        Hackathon hackathon = getEntity(hackathonRepository, hackathonId, "Hackathon");

        if(!team.getLeader().equals(leader)){
            throw new PermissionDeniedException("You are not the leader of this team.");
        }
        if (!checkPermission(leader, Permission.Can_Submit, hackathon)) {
            throw new PermissionDeniedException("You don't have submission rights.");
        }
        if (!hackathon.isActionAllowed(Permission.Can_Submit)) {
            throw new InvalidOperationException("Submission not allowed in current phase.");
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

    /**
     * Sends a Help Request to the mentors of a given Hackathon.
     */
    @Transactional
    public void requestHelp(Long userId, Long hackathonId, String problemDescription) {
        User member = getEntity(userRepository, userId, "User");
        Hackathon hackathon = getEntity(hackathonRepository, hackathonId, "Hackathon");

        if (!hackathon.isActionAllowed(Permission.Can_Create_Help_Request)) {
            throw new InvalidOperationException("Hackathon not in the correct state.");
        }
        if (member.getRoleByContext(hackathon).isEmpty()) {
            throw new RuntimeException("You are not participating in this Hackathon.");
        }
        if(!checkPermission(member, Permission.Can_Create_Help_Request, hackathon)){
            throw new PermissionDeniedException("You cannot send help request.");
        }

        messageService.sendMessage(member, null, MessageType.HELP_REQUEST, problemDescription, hackathon.getId());
    }


    /**
     * Retrieves the participants (members) of a specific Hackathon Team.
     *
     * @param hackathonTeamId The ID of the Hackathon Team.
     * @return A list of User entities representing the team members.
     * @throws ResourceNotFoundException if the Hackathon Team is not found.
     */
    public List<User> getTeamParticipants(Long hackathonTeamId) {
        HackathonTeam team = getEntity(hackathonTeamRepository, hackathonTeamId, "HackathonTeam");
        return team.getMembers();
    }

}
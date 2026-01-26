package unicam.it.idshackhub.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import unicam.it.idshackhub.model.hackathon.Hackathon;
import unicam.it.idshackhub.model.hackathon.state.HackathonStatus;
import unicam.it.idshackhub.model.team.HackathonTeam;
import unicam.it.idshackhub.model.user.User;
import unicam.it.idshackhub.model.user.assignment.Assignment;
import unicam.it.idshackhub.model.user.role.ContextRole;
import unicam.it.idshackhub.model.utils.Submission;
import unicam.it.idshackhub.repository.HackathonRepository;
import unicam.it.idshackhub.repository.SubmissionRepository;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class JudgeServiceTest {

    @Mock
    private SubmissionRepository submissionRepository;

    @Mock
    private HackathonRepository hackathonRepository;

    @InjectMocks
    private JudgeService judgeService;

    private User judge;
    private Hackathon hackathon;
    private Submission submission;

    @BeforeEach
    void setUp() {
        judge = new User(1L, "JudgeDredd", "law@test.com", "hash");

        hackathon = new Hackathon();
        hackathon.setId(100L);
        // Importante: inizializzare la lista sottomissioni per evitare NPE nel metodo closeEvaluation
        hackathon.setSubmissions(new ArrayList<>());

        HackathonTeam team = new HackathonTeam();
        submission = new Submission("Project Alpha", team);
        hackathon.getSubmissions().add(submission);

        // Assegniamo il ruolo Giudice
        Assignment assignment = new Assignment(hackathon, ContextRole.H_Judge);
        assignment.setUser(judge);
        judge.getAssignments().add(assignment);
    }

    @Test
    void testJudgeSubmissionSuccess() {
        // Il giudice può votare solo in fase di EVALUATION
        hackathon.setStatus(HackathonStatus.EVALUATION);

        int vote = 28;

        Integer result = judgeService.judgeSubmission(judge, submission, hackathon, vote);

        assertEquals(vote, result);
        assertEquals(vote, submission.getVote());

        verify(submissionRepository).save(submission);
    }

    @Test
    void testJudgeSubmissionWrongStatus() {
        // Se siamo ancora in progress, non si vota
        hackathon.setStatus(HackathonStatus.IN_PROGRESS);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            judgeService.judgeSubmission(judge, submission, hackathon, 18);
        });

        assertEquals("Hackathon not in the evaluation state", ex.getMessage());
    }

    @Test
    void testCloseEvaluationStateSuccess() {
        hackathon.setStatus(HackathonStatus.EVALUATION);
        submission.setVote(30); // Tutte le submission devono avere un voto

        HackathonStatus status = judgeService.closeEvaluationState(judge, hackathon);

        assertEquals(HackathonStatus.CONCLUSION, status);
        assertEquals(HackathonStatus.CONCLUSION, hackathon.getStatus());
    }

    @Test
    void testCloseEvaluationStateFailMissingVote() {
        hackathon.setStatus(HackathonStatus.EVALUATION);
        submission.setVote(null); // Voto mancante

        assertThrows(RuntimeException.class, () -> {
            judgeService.closeEvaluationState(judge, hackathon);
        });
    }
}
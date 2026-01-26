package unicam.it.idshackhub.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import unicam.it.idshackhub.model.hackathon.Hackathon;
import unicam.it.idshackhub.model.team.HackathonTeam;
import unicam.it.idshackhub.model.user.User;
import unicam.it.idshackhub.model.user.assignment.Assignment;
import unicam.it.idshackhub.model.user.role.ContextRole;
import unicam.it.idshackhub.model.utils.Submission;
import unicam.it.idshackhub.repository.SubmissionRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class) // Abilita Mockito
class HackathonTeamServiceTest {

    @Mock // Crea un finto repository
    private SubmissionRepository submissionRepository;

    @InjectMocks // Inietta il finto repository dentro il vero service
    private HackathonTeamService hackathonTeamService;

    private User leader;
    private HackathonTeam team;
    private Hackathon hackathon;

    @BeforeEach
    void setUp() {
        // Setup base
        leader = new User(1L, "Leader", "leader@test.com", "hash");
        hackathon = new Hackathon();
        hackathon.setId(100L);
        team = new HackathonTeam();
        team.setId(50L);
        team.setHackathonParticipation(hackathon);

        // Assegniamo il permesso corretto in memoria (senza DB)
        Assignment assignment = new Assignment(team, ContextRole.H_HackathonTeamLeader);
        assignment.setUser(leader);
        leader.getAssignments().add(assignment);
    }

    @Test
    void testPostSubmissionSuccess() {
        String description = "Project Final";

        // Quando il repository viene chiamato con save(), restituisci l'oggetto passato
        when(submissionRepository.save(any(Submission.class))).thenAnswer(i -> i.getArguments()[0]);

        // Esecuzione
        Submission result = hackathonTeamService.postSubmission(leader, description, team, hackathon);

        // Verifiche
        assertNotNull(result);
        assertEquals(description, result.getDescription());
        assertEquals(team, result.getTeam());

        // Verifica che il metodo save del repository sia stato chiamato 1 volta
        verify(submissionRepository).save(any(Submission.class));
    }

    @Test
    void testPostSubmissionPermissionDenied() {
        User intruder = new User(99L, "Intruder", "bad@test.com", "hash");
        // Intruder non ha assignments

        assertThrows(RuntimeException.class, () -> {
            hackathonTeamService.postSubmission(intruder, "Virus", team, hackathon);
        });
    }
}
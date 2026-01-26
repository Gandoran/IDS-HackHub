package unicam.it.idshackhub.model.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import unicam.it.idshackhub.model.hackathon.Hackathon;
import unicam.it.idshackhub.model.hackathon.Schedule;
import unicam.it.idshackhub.model.hackathon.TeamRules;
import unicam.it.idshackhub.model.team.Team;
import unicam.it.idshackhub.model.user.User;
import unicam.it.idshackhub.model.user.role.ContextRole;
import unicam.it.idshackhub.repository.HackathonRepository;
import unicam.it.idshackhub.repository.RequestRepository;
import unicam.it.idshackhub.repository.TeamRepository;
import unicam.it.idshackhub.repository.UserRepository;
import unicam.it.idshackhub.service.SystemService;
import unicam.it.idshackhub.test.TestObjectsFactory;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class) // Necessario per Mockito
class SystemServiceTest {

    @Mock private RequestRepository requestRepository;
    @Mock private UserRepository userRepository;
    @Mock private HackathonRepository hackathonRepository;
    @Mock private TeamRepository teamRepository;

    @InjectMocks
    private SystemService systemService;

    private User teamLeader;
    private User hackathonOrganizer;
    private User memberUser;
    private TeamRules teamRules;
    private Schedule schedule;

    @BeforeEach
    void setUp() {
        // Setup Users
        teamLeader = TestObjectsFactory.createUser(1L, "LeaderUser", "Pass1");
        memberUser = TestObjectsFactory.createUser(2L, "MemberUser", "Pass2");
        hackathonOrganizer = TestObjectsFactory.createVerifiedUser(3L, "OrganizerUser", "Pass3");

        // Setup Objects
        teamRules = TestObjectsFactory.createTeamRules();
        schedule = TestObjectsFactory.createSchedule();
    }

    @Test
    void createHackathon_Success() {
        // Simuliamo salvataggio Hackathon e User
        when(hackathonRepository.save(any(Hackathon.class))).thenAnswer(i -> i.getArguments()[0]);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        Hackathon result = systemService.createHackathon(hackathonOrganizer, "NewHack", "Desc", teamRules, schedule);

        assertNotNull(result);

        // Verifica assegnazione ruolo
        boolean hasOrganizerRole = hackathonOrganizer.getAssignments().stream()
                .anyMatch(a -> a.getRole().toString().equals(ContextRole.H_Organizer.toString())
                        && a.getContext().equals(result));

        assertTrue(hasOrganizerRole, "User should be assigned H_Organizer role");

        verify(hackathonRepository).save(any(Hackathon.class));
        verify(userRepository).save(hackathonOrganizer);
    }

    @Test
    void createTeam_Success() {
        // Simuliamo salvataggio Team e User
        when(teamRepository.save(any(Team.class))).thenAnswer(i -> i.getArguments()[0]);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        Team result = systemService.createTeam(memberUser, "TeamName", "Desc", "ITTEAM00000000000");

        assertNotNull(result);

        // Verifica assegnazione ruolo
        boolean hasLeaderRole = memberUser.getAssignments().stream()
                .anyMatch(a -> a.getRole().toString().equals(ContextRole.T_TeamLeader.toString())
                        && a.getContext().equals(result));

        assertTrue(hasLeaderRole, "User should be assigned T_TeamLeader role");
        assertEquals(result, memberUser.getUserTeam());

        verify(teamRepository).save(any(Team.class));
        verify(userRepository).save(memberUser);
    }

    @Test
    void createTeam_Failure_UserAlreadyInATeam() {
        // Assegniamo un team esistente
        Team existingTeam = TestObjectsFactory.createMainTeam(999L, "Existing", memberUser);
        memberUser.setUserTeam(existingTeam);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                systemService.createTeam(memberUser, "NewTeam", "NewTeamDesc", "ITTEAM000000000000"));
        assertEquals("User already in a team", exception.getMessage());
    }
}
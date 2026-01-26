package unicam.it.idshackhub.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import unicam.it.idshackhub.model.hackathon.Hackathon;
import unicam.it.idshackhub.model.team.HackathonTeam;
import unicam.it.idshackhub.model.team.Team;
import unicam.it.idshackhub.model.user.User;
import unicam.it.idshackhub.model.user.role.ContextRole;
import unicam.it.idshackhub.repository.HackathonTeamRepository;
import unicam.it.idshackhub.repository.UserRepository;
import unicam.it.idshackhub.test.TestObjectsFactory;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    private User teamLeader;
    private User teamLeaderTwo;
    private User hackathonOrganizer;
    private User memberUser;
    private User hackathonTeamLeader;
    private Team mainTeam;
    private Hackathon hackathon;
    private List<User> members;
    @Mock private HackathonTeamRepository hackathonTeamRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks // <--- Mockito creerà l'istanza e inietterà i repository sopra
    private TeamService teamService;


    @BeforeEach
    void setUp() {

        // Setup Users
        teamLeader = TestObjectsFactory.createUser(1L, "LeaderUser", "Pass1");
        memberUser = TestObjectsFactory.createUser(2L, "MemberUser", "Pass2");
        hackathonOrganizer = TestObjectsFactory.createUser(3L, "OrganizerUser", "Pass3");
        hackathonTeamLeader = TestObjectsFactory.createUser(4L, "HackTeamLeader", "Pass4");
        teamLeaderTwo = TestObjectsFactory.createUser(5L, "LeaderUserTwo", "Pass5");

        // Setup Main Team e Hackathon
        mainTeam = TestObjectsFactory.createMainTeam(100L, "Hack Team", teamLeader);
        hackathon = TestObjectsFactory.createHackathon(500L, "Super Hackathon", hackathonOrganizer);

        members = new ArrayList<>();
        members.add(teamLeader);
        members.add(memberUser);
        members.add(hackathonTeamLeader);

        lenient().when(hackathonTeamRepository.save(any(HackathonTeam.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        lenient().when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        lenient().when(userRepository.saveAll(anyList()))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void registerHackathonTeam_Success() {
        HackathonTeam result = teamService.registerHackathonTeam(teamLeader, "Hack Team Alpha", "Desc", hackathonTeamLeader, members, hackathon);

        // Verifiche strutturali
        assertTrue(hackathon.getTeams().contains(result));
        assertEquals(hackathon, result.getHackathonParticipation());
        assertTrue(mainTeam.getHackathonTeams().contains(result));

        // Verifiche Ruoli
        // Nota: Assicuriamoci che il controllo del ruolo funzioni con la nuova logica JPA (role.toString())
        boolean hasLeaderRole = hackathonTeamLeader.getAssignments().stream()
                .anyMatch(a -> a.getRole().toString().equals(ContextRole.H_HackathonTeamLeader.toString())
                        && a.getContext().equals(hackathon));
        assertTrue(hasLeaderRole, "Leader should receive H_HackathonTeamLeader assignment");

        boolean leaderHasMemberRole = hackathonTeamLeader.getAssignments().stream()
                .anyMatch(a -> a.getRole().toString().equals(ContextRole.H_HackathonTeamMember.toString())
                        && a.getContext().equals(hackathon));
        assertTrue(leaderHasMemberRole, "Leader should also have H_HackathonTeamMember assignment");

        boolean hasMemberRole = memberUser.getAssignments().stream()
                .anyMatch(a -> a.getRole().toString().equals(ContextRole.H_HackathonTeamMember.toString())
                        && a.getContext().equals(hackathon));
        assertTrue(hasMemberRole, "Members should receive H_HackathonTeamMember assignment");
    }

    @Test
    void registerHackathonTeam_Failure_MaxTeams() {
        hackathon.getRules().setMaxTeams(0);
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                teamService.registerHackathonTeam(teamLeader, "Alpha", "Desc", hackathonTeamLeader, members, hackathon));
        assertEquals("Maximum team amount reached", exception.getMessage());
    }

    @Test
    void registerHackathonTeam_Failure_MaxTeamMembers() {
        hackathon.getRules().setMaxPlayersPerTeam(1);
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                teamService.registerHackathonTeam(teamLeader, "Alpha", "Desc", hackathonTeamLeader, members, hackathon));
        assertEquals("Team size is too big", exception.getMessage());
    }

    @Test
    void registerHackathonTeam_Failure_MinTeamMembers() {
        hackathon.getRules().setMinPlayersPerTeam(10);
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                teamService.registerHackathonTeam(teamLeader, "Alpha", "Desc", hackathonTeamLeader, members, hackathon));
        assertEquals("Team size is too small", exception.getMessage());
    }

    @Test
    void registerHackathonTeam_Failure_MainTeamAlreadyParticipating() {
        // Prima registrazione OK
        teamService.registerHackathonTeam(teamLeader, "Alpha", "Desc", hackathonTeamLeader, members, hackathon);

        // Seconda registrazione con stesso Main Team -> Fallisce
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                teamService.registerHackathonTeam(teamLeader, "Beta", "Desc", hackathonTeamLeader, members, hackathon));
        assertEquals("Main Team already has a Hackathon Team", exception.getMessage());
    }

    @Test
    void registerHackathonTeam_Failure_UserAlreadyInHackathon() {
        // Registriamo il primo team
        teamService.registerHackathonTeam(teamLeader, "Alpha", "Desc", hackathonTeamLeader, members, hackathon);

        // Creiamo un secondo leader con un altro team principale
        Team secondMainTeam = TestObjectsFactory.createMainTeam(200L, "Other Team", teamLeaderTwo);

        // Proviamo a registrare un nuovo team usando gli stessi membri (che sono già iscritti)
        List<User> newMembers = new ArrayList<>(members); // Stessi utenti

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                teamService.registerHackathonTeam(teamLeaderTwo, "Beta", "Desc", teamLeaderTwo, newMembers, hackathon));
        assertEquals("User already in the hackathon", exception.getMessage());
    }

    @Test
    void registerHackathonTeam_Failure_NotTeamLeader() {
        // memberUser non è leader di nessun Main Team
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                teamService.registerHackathonTeam(memberUser, "Name", "Desc", hackathonTeamLeader, members, hackathon));
        assertEquals("You have to be a Team Leader of a Main Team to create a Hackathon Team", exception.getMessage());
    }
}
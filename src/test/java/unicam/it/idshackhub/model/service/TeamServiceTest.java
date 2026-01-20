package unicam.it.idshackhub.model.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;
import unicam.it.idshackhub.model.hackathon.Hackathon;
import unicam.it.idshackhub.model.team.HackathonTeam;
import unicam.it.idshackhub.model.team.Team;
import unicam.it.idshackhub.model.user.User;
import unicam.it.idshackhub.model.user.assignment.Assignment;
import unicam.it.idshackhub.model.user.role.GlobalRole;
import unicam.it.idshackhub.model.user.role.Role;
import unicam.it.idshackhub.model.user.role.Role.*;
import unicam.it.idshackhub.model.user.role.permission.Permission;
import unicam.it.idshackhub.test.TestObjectsFactory;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static unicam.it.idshackhub.model.service.PermissionChecker.checkPermission;

class TeamServiceTest {

    private TeamService teamService;
    private User teamLeader;
    private User teamLeaderTwo;
    private User hackathonOrganizer;
    private User memberUser;
    private User hackathonTeamLeader;
    private User hackathonJudge;
    private Team mainTeam;
    private Team secondaryTeam;
    private Hackathon hackathon;
    private HackathonTeam hackathonTeam;

    @BeforeEach
    void setUp() {teamService = new TeamService();

        // 1. Creazione Utenti tramite Factory
        teamLeader = TestObjectsFactory.createUser(1L, "LeaderUser", GlobalRole.G_VerifiedUser);
        memberUser = TestObjectsFactory.createUser(2L, "MemberUser", GlobalRole.G_NormalUser);
        hackathonOrganizer = TestObjectsFactory.createUser(3L, "OrganizerUser", GlobalRole.G_VerifiedUser);
        hackathonTeamLeader = TestObjectsFactory.createUser(4L, "HackTeamLeader", GlobalRole.G_NormalUser);
        teamLeaderTwo = TestObjectsFactory.createUser(5L, "LeaderUserTwo", GlobalRole.G_VerifiedUser);

        // 2. Creazione Main Team (questo assegna automaticamente il ruolo T_TeamLeader all'utente)
        // Nota: Assicurati che Team estenda BaseContext nel tuo codice!
        mainTeam = TestObjectsFactory.createMainTeam(100L, "Hack Team", teamLeader);
        secondaryTeam = TestObjectsFactory.createMainTeam(200L, "Fake Team", teamLeaderTwo);

        // 3. Creazione Hackathon
        hackathon = TestObjectsFactory.createHackathon(500L, "Super Hackathon", hackathonOrganizer);

        // 4. Creazione Hackathon Team
        ArrayList<User> hackTeamMembers = new ArrayList<>();
        hackTeamMembers.add(teamLeader);
        hackTeamMembers.add(memberUser);
        hackathonTeam = TestObjectsFactory.createHackathonTeam(200L, "Hack Team Alpha", mainTeam, hackathonTeamLeader, hackTeamMembers, hackathon);
    }


    @Test
    void registerTeam_Success() {
        teamService.registerTeam(teamLeader, hackathonTeam, hackathon);
        assertTrue(hackathon.getTeams().contains(hackathonTeam), "Il team dovrebbe essere registrato nell'Hackathon");
    }

    @Test
    void registerTeam_Failure_AlreadyRegistered() {
        teamService.registerTeam(teamLeader, hackathonTeam, hackathon);
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                teamService.registerTeam(teamLeader, hackathonTeam, hackathon));
        assertEquals("Team already registered", exception.getMessage());
    }

    @Test
    void registerTeam_Failure_PermissionDenied() {
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                teamService.registerTeam(memberUser, hackathonTeam, hackathon));
        assertEquals("Permission denied", exception.getMessage());
    }

    @Test
    void createHackathonTeam_Success() {
        List<User> members = List.of(hackathonTeamLeader, memberUser);
        teamService.createHackathonTeam(teamLeader, "HackTeam", "Desc", hackathonTeamLeader, members);
        mainTeam.getHackathonTeams().forEach(hackathonTeam -> assertTrue(hackathonTeam.getMembers().contains(hackathonTeamLeader)));
    }

    @Test
    void createHackathonTeam_Failure_PermissionDenied() {
        // Crea un ruolo personalizzato finto
        Role restrictedRole = new Role() {
            @Override
            public List<Permission> getListPermission() {
                return new ArrayList<>();
            }

            // Sovrascrive il metodo per controllare i permessi per negare sempre
            // il permesso per la creazione di HackathonTeam
            @Override
            public boolean hasPermission(Permission permission) {
                if (permission == Permission.Can_Create_HackathonTeam) {
                    return false;
                }
                return true;
            }
        };

        // Crea l'utente fantoccio
        User restrictedUser = TestObjectsFactory.createUser(99L, "RestrictedUser", GlobalRole.G_NormalUser);
        // Crea un Assignment per l'utente fantoccio
        Assignment fakeAssignment = TestObjectsFactory.createAssignment(mainTeam, restrictedRole);
        restrictedUser.getAssignments().add(fakeAssignment);

        assertThrows(RuntimeException.class, () ->
                teamService.createHackathonTeam(restrictedUser, "Fail Team", "Desc", memberUser, List.of())
        );
    }

    @Test
    void createHackathonTeam_Failure_TeamNotFound() {
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                        teamService.createHackathonTeam(memberUser, "Name", "Desc", hackathonTeamLeader, List.of()));
        assertEquals("You have to be a Team Leader to create a Hackathon Team", exception.getMessage());
    }

    @Test
    void createHackathonTeam_Failure_TeamAlreadyExists() {
        teamService.createHackathonTeam(teamLeader, "HackTeam", "Desc", hackathonTeamLeader, List.of());
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                teamService.createHackathonTeam(teamLeader, "HackTeam", "Desc", hackathonTeamLeader, List.of()));
        assertEquals("Team already exists", exception.getMessage());
    }

}
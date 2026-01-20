package unicam.it.idshackhub.test;

import unicam.it.idshackhub.model.hackathon.*;
import unicam.it.idshackhub.model.team.HackathonTeam;
import unicam.it.idshackhub.model.team.Team;
import unicam.it.idshackhub.model.team.builder.HackathonTeamBuilder;
import unicam.it.idshackhub.model.team.builder.TeamBuilder;
import unicam.it.idshackhub.model.user.User;
import unicam.it.idshackhub.model.user.assignment.Assignment;
import unicam.it.idshackhub.model.user.assignment.BaseContext;
import unicam.it.idshackhub.model.user.role.GlobalRole;
import unicam.it.idshackhub.model.user.role.HackathonRole;
import unicam.it.idshackhub.model.user.role.Role;
import unicam.it.idshackhub.model.user.role.TeamRole;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class TestObjectsFactory {

    /**
     * Crea un utente generico con un ruolo globale.
     */
    public static User createUser(Long id, String username, GlobalRole globalRole) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(username.toLowerCase() + "@test.com");
        user.setGlobalRole(globalRole);
        user.setAssignments(new ArrayList<>());
        return user;
    }

    /**
     * Crea un Team principale (Azienda/Gruppo stabile).
     */
    public static Team createMainTeam(Long id, String name, User leader) {
        TeamBuilder builder = new TeamBuilder();
        Team team = builder.reset()
                .buildIban("IT"+name.toUpperCase()+"00000000000000000")
                .buildName(name)
                .buildDescription("Description for " + name)
                .buildLeader(leader)
                .buildMembers(new ArrayList<>())
                .getTeam();
        team.setId(id);
        if (leader != null) {
            Assignment assignment = createAssignment(team, TeamRole.T_TeamLeader);
            leader.getAssignments().add(assignment);

        }
        return team;
    }

    /**
     * Crea un HackathonTeam collegato a un mainTeam e ad un hackathon.
     */
    public static HackathonTeam createHackathonTeam(Long id, String name, Team mainTeam, User leader, ArrayList<User> members, Hackathon hackathon) {
        HackathonTeamBuilder builder = new HackathonTeamBuilder();
        HackathonTeam team = builder.reset()
                .buildMainTeam(mainTeam)
                .buildName(name)
                .buildDescription("Description for " + name)
                .buildLeader(leader)
                .buildMembers(members)
                .buildHackathonParticipation(hackathon)
                .getTeam();
        team.setId(id);
        return team;

    }

    /**
     * Crea un Hackathon con date valide.
     */
    public static Hackathon createHackathon(Long id, String title, User organizer) {
        LocalDateTime now = LocalDateTime.now();
        Schedule schedule = new Schedule(
                now,              // Registrazione iniziata
                now.plusDays(1),  // Evento inizia dopo
                now.plusDays(2),  // Evento finisce
                "Online"
        );
        TeamRules rules = new TeamRules(20, 1, 5, 1);

        HackathonBuilder builder = new HackathonBuilder();
        Hackathon h = builder.reset()
                .buildTitle(title)
                .buildDescription("Test Description for " + title)
                .buildRules(rules)
                .buildSchedule(schedule)
                .getResult();

        h.setId(id);
        h.getStaff().setOrganizer(organizer);

        // LOGICA DI ASSEGNAZIONE RUOLO ORGANIZZATORE
        if (organizer != null) {
            Assignment assignment = createAssignment(h, HackathonRole.H_Organizator);
            organizer.getAssignments().add(assignment);
        }

        return h;
    }

    /**
     * Metodo Helper per creare Assignment anche senza costruttore pubblico.
     * Usa la REFLECTION per forzare i campi privati.
     */
    public static Assignment createAssignment(BaseContext context, Role role) {
        Assignment assignment = new Assignment();
        try {
            // Setta il campo 'context'
            Field contextField = Assignment.class.getDeclaredField("context");
            contextField.setAccessible(true);
            contextField.set(assignment, context);

            // Setta il campo 'role'
            Field roleField = Assignment.class.getDeclaredField("role");
            roleField.setAccessible(true);
            roleField.set(assignment, role);

        } catch (Exception e) {
            throw new RuntimeException("Errore nella creazione dell'Assignment di test: " + e.getMessage());
        }
        return assignment;
    }
}
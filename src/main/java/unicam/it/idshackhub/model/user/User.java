package unicam.it.idshackhub.model.user;

import lombok.Getter;
import lombok.Setter;
import unicam.it.idshackhub.model.team.Team;
import unicam.it.idshackhub.model.user.assignment.Assignment;
import unicam.it.idshackhub.model.user.assignment.Context;
import unicam.it.idshackhub.model.user.role.GlobalRole;
import unicam.it.idshackhub.model.user.role.Role;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Represents a registered user in the system.
 * <p>
 * The User entity is the central actor of the platform. It manages identity information
 * and holds the references to all roles and permissions via two mechanisms:
 * <ul>
 * <li><b>Global Role:</b> A baseline role valid everywhere (e.g., Admin, NormalUser).</li>
 * <li><b>Assignments:</b> A list of context-specific roles (e.g., Leader of Team A, Judge of Hackathon B).</li>
 * </ul>
 * </p>
 */
@Getter
@Setter
public class User {

    /**
     * Unique identifier for the user.
     */
    private long id;

    /**
     * The username used for login and display.
     */
    private String username;

    /**
     * The user's email address.
     */
    private String email;

    /**
     * The hashed version of the user's password.
     */
    private String passwordHash;

    /**
     * The global role assigned to the user, determining platform-wide privileges.
     */
    private GlobalRole globalRole;

    /**
     * A list of contextual assignments linking the user to specific Teams or Hackathons with specific roles.
     */
    private List<Assignment> assignments;

    /**
     * The reference to the permanent Main Team this user belongs to (if any).
     */
    private Team userTeam;

    /**
     * Constructs a new User with default permissions.
     *
     * @param id           the unique ID.
     * @param username     the username.
     * @param email        the email address.
     * @param passwordHash the password hash.
     */
    public User(long id, String username, String email, String passwordHash){
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.globalRole = GlobalRole.G_NormalUser;
        this.assignments = new ArrayList<>();
        this.userTeam = null;
    }

    /**
     * Retrieves the specific role this user holds within a given context.
     *
     * @param context the context to query (e.g., a specific {@link unicam.it.idshackhub.model.hackathon.Hackathon}).
     * @return an {@link Optional} containing the {@link Role} if found, or empty if the user has no assignment in that context.
     */
    public Optional<Role> getRoleByContext(Context context) {
        return assignments.stream()
                .filter(a -> context.equals(a.getContext()))
                .map(a -> (Role) a.getRole())
                .findFirst();
    }

    /**
     * Retrieves the context where the user holds a specific role.
     * <p>
     * Useful for finding "Which team am I leading?" or "Which hackathon am I organizing?".
     * </p>
     *
     * @param role the role to search for.
     * @return an {@link Optional} containing the {@link Context} if found, or empty otherwise.
     */
    public Optional<Context> getContextByRole(Role role) {
        return assignments.stream()
                .filter(a -> role.equals(a.getRole()))
                .map(Assignment::getContext)
                .map(Context.class::cast)
                .findFirst();
    }
}
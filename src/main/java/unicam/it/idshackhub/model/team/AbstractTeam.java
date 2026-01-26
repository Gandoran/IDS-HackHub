package unicam.it.idshackhub.model.team;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import unicam.it.idshackhub.model.user.User;
import unicam.it.idshackhub.model.user.assignment.BaseContext;
import java.util.ArrayList;
import java.util.List;

/**
 * Serves as the base class for all team-related entities.
 * <p>
 * This abstract class captures the common properties shared by both permanent teams
 * ({@link Team}) and temporary participation groups ({@link HackathonTeam}).
 * It extends {@link BaseContext}, allowing specific roles and permissions to be
 * assigned within the scope of any team.
 * </p>
 */
@MappedSuperclass
@Getter @Setter
public abstract class AbstractTeam extends BaseContext {

    /**
     * The display name of the team.
     */
    private String name;

    /**
     * A brief description of the team's purpose or goals.
     */
    private String description;

    /**
     * The user designated as the leader of this team context.
     * <p>
     * Note: The leader typically holds special permissions (e.g., registering the team).
     * </p>
     */
    @ManyToOne
    @JoinColumn(name = "leader_id")
    private User leader;

    /**
     * The list of users who are members of this team.
     */
    @ManyToMany
    @JoinTable(
            name = "team_members",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> members = new ArrayList<>();
}
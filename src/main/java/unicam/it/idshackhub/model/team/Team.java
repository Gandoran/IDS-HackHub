package unicam.it.idshackhub.model.team;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a permanent organization or group (Main Team).
 * <p>
 * This entity represents a stable group of users (e.g., a company, a startup, or a group of friends)
 * that exists independently of any specific event. It maintains a history of its participations
 * via the {@link #hackathonTeams} list.
 * </p>
 */
@Entity
@Table(name = "team")
@Getter @Setter
public class Team extends AbstractTeam {

    /**
     * The PayPal account for the team
     */
    private String payPalAccount;

    /**
     * A historical list of all Hackathon participations (sub-teams) created by this organization.
     */
    @OneToMany(mappedBy = "mainTeam", cascade = CascadeType.ALL)
    private List<HackathonTeam> hackathonTeams = new ArrayList<>();

    /**
     * Checks equality based on the Team Leader.
     * <p>
     * <b>Note:</b> The current implementation defines equality solely based on the Leader.
     * This implies that a Leader implies the identity of the Team in this context.
     * </p>
     *
     * @param o the object to compare.
     * @return {@code true} if the teams are led by the same user.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Team that = (Team) o;
        return Objects.equals(this.getLeader(), that.getLeader());
    }
}
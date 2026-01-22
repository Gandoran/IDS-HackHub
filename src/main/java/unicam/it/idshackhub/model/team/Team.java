package unicam.it.idshackhub.model.team;

import lombok.Getter;
import lombok.Setter;

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
@Getter
@Setter
public class Team extends AbstractTeam {

    /**
     * The International Bank Account Number (IBAN) associated with the team
     * for financial transactions or prize payouts.
     */
    private String iban;

    /**
     * A historical list of all Hackathon participations (sub-teams) created by this organization.
     */
    private List<HackathonTeam> hackathonTeams;

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
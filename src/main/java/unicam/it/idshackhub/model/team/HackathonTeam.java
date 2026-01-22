package unicam.it.idshackhub.model.team;

import lombok.Getter;
import lombok.Setter;
import unicam.it.idshackhub.model.hackathon.Hackathon;

import java.util.Objects;

/**
 * Represents a temporary sub-team formed specifically for a Hackathon participation.
 * <p>
 * A {@link HackathonTeam} is always linked to a permanent parent {@link Team} ("Main Team")
 * and the specific {@link Hackathon} event it is attending. This allows a Main Team to
 * spawn multiple sub-teams for different events (or multiple sub-teams for the same event,
 * if rules allow), while tracking the specific members for that occasion.
 * </p>
 */
@Getter
@Setter
public class HackathonTeam extends AbstractTeam {

    /**
     * The permanent parent team that spawned this participation group.
     */
    private Team mainTeam;

    /**
     * The Hackathon event this team is registered for.
     */
    private Hackathon hackathonParticipation;

    /**
     * Checks equality based on the Business Key (MainTeam + Hackathon).
     * <p>
     * Two HackathonTeam instances are considered equal if they belong to the same
     * Main Team and are participating in the same Hackathon event.
     * </p>
     *
     * @param o the object to compare.
     * @return {@code true} if the teams represent the same participation context.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        HackathonTeam that = (HackathonTeam) o;

        return Objects.equals(this.mainTeam, that.mainTeam) &&
                Objects.equals(this.hackathonParticipation, that.hackathonParticipation);
    }

    /**
     * Generates a hash code based on the Main Team and Hackathon Participation.
     *
     * @return the hash code.
     */
    @Override
    public int hashCode() {
        return Objects.hash(mainTeam, hackathonParticipation);
    }
}
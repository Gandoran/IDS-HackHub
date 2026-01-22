package unicam.it.idshackhub.model.hackathon;

import lombok.Getter;
import lombok.Setter;

/**
 * Encapsulates the rules and constraints regarding team composition for a Hackathon.
 * <p>
 * This class defines the limits for the number of teams allowed in an event
 * and the number of members allowed within a single team.
 * </p>
 */
@Setter
@Getter
public class TeamRules {

    /**
     * The maximum number of teams allowed to register for the Hackathon.
     */
    private int maxTeams;

    /**
     * The minimum number of teams required for the Hackathon to proceed.
     */
    private int minTeams;

    /**
     * The maximum number of players allowed in a single team.
     */
    private int maxPlayersPerTeam;

    /**
     * The minimum number of players required to form a valid team.
     */
    private int minPlayersPerTeam;

    /**
     * Constructs a new set of TeamRules.
     *
     * @param maxT the maximum number of teams.
     * @param minT the minimum number of teams.
     * @param maxP the maximum number of players per team.
     * @param minP the minimum number of players per team.
     * @throws IllegalArgumentException if the maximum values are less than the minimum values.
     */
    public TeamRules(int maxT, int minT, int maxP, int minP) {
        if(maxT < minT || maxP < minP) throw new IllegalArgumentException("Maximum values cannot be less than minimum values.");
        this.maxTeams = maxT;
        this.minTeams = minT;
        this.maxPlayersPerTeam = maxP;
        this.minPlayersPerTeam = minP;
    }
}
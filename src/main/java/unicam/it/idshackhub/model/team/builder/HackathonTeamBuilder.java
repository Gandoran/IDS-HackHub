package unicam.it.idshackhub.model.team.builder;

import unicam.it.idshackhub.model.hackathon.Hackathon;
import unicam.it.idshackhub.model.team.HackathonTeam;
import unicam.it.idshackhub.model.team.Team;

/**
 * Concrete builder for creating {@link HackathonTeam} instances.
 * <p>
 * This class extends the abstract team builder to include fields specific to
 * hackathon participation, such as the parent {@link Team} and the target {@link Hackathon}.
 * </p>
 */
public class HackathonTeamBuilder extends TeamBuilderAbstract<HackathonTeam, HackathonTeamBuilder> {

    /**
     * Initializes a new builder instance with a fresh HackathonTeam object.
     */
    public HackathonTeamBuilder() {
        super.team = new HackathonTeam();
    }

    /**
     * Sets the main (permanent) team that acts as the parent for this hackathon team.
     *
     * @param mainTeam the parent {@link Team}.
     * @return the builder instance for method chaining.
     */
    public HackathonTeamBuilder buildMainTeam(Team mainTeam) {
        team.setMainTeam(mainTeam);
        return this;
    }

    /**
     * Sets the Hackathon event in which the team will participate.
     *
     * @param h the target {@link Hackathon}.
     * @return the builder instance for method chaining.
     */
    public HackathonTeamBuilder buildHackathonParticipation(Hackathon h) {
        team.setHackathonParticipation(h);
        return this;
    }

    /**
     * Resets the builder state to start constructing a new HackathonTeam.
     *
     * @return the builder instance for method chaining.
     */
    @Override
    public HackathonTeamBuilder reset() {
        team = new HackathonTeam();
        return this;
    }
}
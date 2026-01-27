package unicam.it.idshackhub.model.team.builder;

import unicam.it.idshackhub.model.team.Team;
import unicam.it.idshackhub.model.user.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Concrete builder implementation for creating permanent {@link Team} entities.
 * <p>
 * This builder extends the abstract logic to include fields specific to a Main Team,
 * such as the PayPal account .
 * </p>
 */
public class TeamBuilder extends TeamBuilderAbstract<Team, TeamBuilder> {

    /**
     * Initializes the builder with a new empty Team instance.
     */
    public TeamBuilder() {
        super.team = new Team();
    }

    /**
     * Sets the PayPal email for the team.
     *
     * @param email the financial identifier string.
     * @return the builder instance for method chaining.
     */
    public TeamBuilder buildPayPalAccount(String email) {
        team.setPayPalAccount(email);
        return this;
    }

    /**
     * Resets the builder and ensures the list of sub-teams (HackathonTeams) is initialized.
     *
     * @return the builder instance.
     */
    @Override
    public TeamBuilder reset() {
        team = new Team();
        team.setHackathonTeams(new ArrayList<>());
        return this;
    }
}
package unicam.it.idshackhub.model.hackathon;

import unicam.it.idshackhub.model.user.User;
import java.util.ArrayList;

/**
 * Builder class for creating instances of {@link Hackathon}.
 * <p>
 * This class implements the Builder design pattern to facilitate the construction
 * of complex Hackathon objects step-by-step. It ensures that the internal state
 * is initialized correctly before the object is returned.
 * </p>
 */
public class HackathonBuilder {

    private Hackathon hackathon;

    /**
     * Returns the constructed Hackathon object.
     *
     * @return the {@link Hackathon} instance being built.
     */
    public Hackathon getResult(){
        return hackathon;
    }

    /**
     * Resets the builder and initializes a new Hackathon instance.
     * This method must be called before starting the build process.
     *
     * @return the builder instance for method chaining.
     */
    public HackathonBuilder reset(){
        hackathon = new Hackathon();
        hackathon.setTeams(new ArrayList<>());
        return this;
    }

    /**
     * Sets the title of the Hackathon.
     *
     * @param title the name of the event.
     * @return the builder instance for method chaining.
     */
    public HackathonBuilder buildTitle(String title){
        this.hackathon.setTitle(title);
        return this;
    }

    /**
     * Sets the description of the Hackathon.
     *
     * @param description the details of the event.
     * @return the builder instance for method chaining.
     */
    public HackathonBuilder buildDescription(String description){
        this.hackathon.setDescription(description);
        return this;
    }

    /**
     * Sets the rules for team composition.
     *
     * @param rules the {@link TeamRules} object defining limits.
     * @return the builder instance for method chaining.
     */
    public HackathonBuilder buildRules(TeamRules rules){
        this.hackathon.setRules(rules);
        return this;
    }

    /**
     * Sets the schedule (dates and location) for the event.
     *
     * @param schedule the {@link Schedule} object.
     * @return the builder instance for method chaining.
     */
    public HackathonBuilder buildSchedule(Schedule schedule){
        this.hackathon.setSchedule(schedule);
        return this;
    }

    /**
     * Configures the staff for the Hackathon by setting the Organizer.
     * <p>
     * This method creates a new {@link HackathonStaff} instance wrapping the provided user.
     * </p>
     *
     * @param organizer the {@link User} who is organizing the event.
     * @return the builder instance for method chaining.
     */
    public HackathonBuilder buildStaff(User organizer){
        this.hackathon.setStaff(new HackathonStaff(organizer));
        return this;
    }
}
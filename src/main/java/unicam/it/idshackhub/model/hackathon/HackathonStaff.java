package unicam.it.idshackhub.model.hackathon;

import lombok.Getter;
import lombok.Setter;
import unicam.it.idshackhub.model.user.User;
import unicam.it.idshackhub.model.user.role.HackathonRole;

/**
 * Represents the administrative staff of a Hackathon.
 * <p>
 * This class acts as a container for key personnel associated with a specific event,
 * such as the main Organizer and the Judge.
 * </p>
 */
@Setter
@Getter
public class HackathonStaff {

    /**
     * The user responsible for organizing and managing the Hackathon.
     */
    private User organizer;

    /**
     * The user responsible for evaluating projects (optional or assigned later).
     */
    private User judge;

    /**
     * Constructs a new HackathonStaff with a designated organizer.
     *
     * @param organizer the {@link User} who organizes the event.
     */
    public HackathonStaff(User organizer) {
        this.organizer = organizer;
    }
}
package unicam.it.idshackhub.model.hackathon;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import unicam.it.idshackhub.model.user.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the administrative staff of a Hackathon.
 * <p>
 * This class acts as a container for key personnel associated with a specific event,
 * such as the main Organizer and the Judge.
 * </p>
 */
@Embeddable
@Getter @Setter @NoArgsConstructor
public class HackathonStaff {

    /**
     * The user responsible for organizing and managing the Hackathon.
     */
    @ManyToOne
    @JoinColumn(name = "organizer_id")
    private User organizer;

    /**
     * The user responsible for evaluating projects (assigned later).
     */
    @ManyToOne
    @JoinColumn(name = "judge_id")
    private User judge;

    /**
     * The list of users responsible for helping teams (assigned later).
     */
    @ManyToMany
    @JoinTable(
            name = "mentors_id",
            joinColumns = @JoinColumn(name = "hackathon_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> mentors = new ArrayList<>();

    /**
     * Constructs a new HackathonStaff with a designated organizer.
     *
     * @param organizer the {@link User} who organizes the event.
     */
    public HackathonStaff(User organizer) {
        this.organizer = organizer;
    }
}
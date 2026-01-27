package unicam.it.idshackhub.model.hackathon;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import unicam.it.idshackhub.model.hackathon.state.*;
import unicam.it.idshackhub.model.team.HackathonTeam;
import unicam.it.idshackhub.model.user.assignment.BaseContext;
import unicam.it.idshackhub.model.user.role.permission.Permission;
import unicam.it.idshackhub.model.utils.Submission;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a Hackathon event within the system.
 * <p>
 * This class serves as a {@link BaseContext}, meaning it acts as a context
 * for defining specific user roles and permissions (e.g., Organizer, Judge).
 * It aggregates all necessary information regarding the event, including
 * rules, scheduling, staff, participating teams and their submissions.
 * </p>
 */
@Entity
@Table(name = "hackathon")
@Getter
@Setter
public class Hackathon extends BaseContext {

    /**
     * The title or name of the Hackathon event.
     */
    private String title;

    /**
     * A detailed description of the event, its goals, and themes.
     */
    private String description;

    /**
     * The prize for this hackathon
     */
    private Double prize;

    /**
     * The set of rules governing team composition and limits for this Hackathon.
     */
    @Embedded
    private TeamRules rules;

    /**
     * The group of users responsible for managing and judging the event.
     */
    @Embedded
    private HackathonStaff staff;

    /**
     * The time window and location details for the event.
     */
    @Embedded
    private Schedule schedule;

    /**
     * The list of teams currently registered to participate in this Hackathon.
     */
    @OneToMany(mappedBy = "hackathonParticipation", cascade = CascadeType.ALL)
    private List<HackathonTeam> teams = new ArrayList<>();

    /**
     * The list of submission currently registered in this Hackathon.
     */
    @OneToMany(mappedBy = "hackathon", cascade = CascadeType.ALL)
    private List<Submission> submissions = new ArrayList<>();

    /**
     * The current phase/state of the Hackathon.
     */
    @Enumerated(EnumType.STRING)
    private HackathonStatus status = HackathonStatus.REGISTRATION;

    /**
     * Checks whether a permission is allowed in the current phase.
     */
    public boolean isActionAllowed(Permission perm) {
        return Objects.requireNonNull(HackathonStateFactory.getBehavior(this.status)).isActionAllowed(perm);
    }

    /**
     * Gets the Hackathon's state and updates it.
     */
    public void updateState() {
        HackathonState state = HackathonStateFactory.getBehavior(this.status);
        if (state != null) {
            state.updateState(this);
        }
    }
}
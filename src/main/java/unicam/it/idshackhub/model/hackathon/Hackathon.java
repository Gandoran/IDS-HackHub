package unicam.it.idshackhub.model.hackathon;

import lombok.Getter;
import lombok.Setter;
import unicam.it.idshackhub.model.team.HackathonTeam;
import unicam.it.idshackhub.model.user.assignment.BaseContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Hackathon event within the system.
 * <p>
 * This class serves as a {@link BaseContext}, meaning it acts as a context
 * for defining specific user roles and permissions (e.g., Organizer, Judge).
 * It aggregates all necessary information regarding the event, including
 * rules, scheduling, staff, and participating teams.
 * </p>
 */
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
     * The set of rules governing team composition and limits for this Hackathon.
     */
    private TeamRules rules;

    /**
     * The group of users responsible for managing and judging the event.
     */
    private HackathonStaff staff;

    /**
     * The time window and location details for the event.
     */
    private Schedule schedule;

    /**
     * The list of teams currently registered to participate in this Hackathon.
     */
    private List<HackathonTeam> teams = new ArrayList<>();
}
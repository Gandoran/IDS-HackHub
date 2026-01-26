package unicam.it.idshackhub.model.utils;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import unicam.it.idshackhub.model.hackathon.Hackathon;
import unicam.it.idshackhub.model.team.HackathonTeam;

/**
 * Represents a project or work submitted by a team for a specific hackathon.
 * <p>
 * This entity stores the submission details, including the project description,
 * the assigned score (vote), and the links to the participating team and
 * the parent hackathon.
 * </p>
 */
@Entity
@Getter @Setter @NoArgsConstructor
public class Submission {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;
    private Integer vote;

    @OneToOne @JoinColumn(name = "team_id")
    private HackathonTeam team;

    @ManyToOne @JoinColumn(name = "hackathon_id")
    private Hackathon hackathon;

    public Submission(String description, HackathonTeam team) {
        this.description = description;
        this.team = team;
    }
}
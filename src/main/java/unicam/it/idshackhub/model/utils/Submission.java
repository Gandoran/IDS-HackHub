package unicam.it.idshackhub.model.utils;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import unicam.it.idshackhub.model.hackathon.Hackathon;
import unicam.it.idshackhub.model.team.HackathonTeam;

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
    private Hackathon hackathon; // Necessario per la List<Submission> in Hackathon

    public Submission(String description, HackathonTeam team) {
        this.description = description;
        this.team = team;
    }
}
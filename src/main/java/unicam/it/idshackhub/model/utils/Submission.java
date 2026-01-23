package unicam.it.idshackhub.model.utils;

import lombok.Getter;
import lombok.Setter;
import unicam.it.idshackhub.model.team.HackathonTeam;

@Getter
@Setter
public class Submission {

    private String description;
    private Integer vote;
    private HackathonTeam team;

    public Submission(String description, HackathonTeam team) {
        this.description = description;
        this.team = team;
    }
}
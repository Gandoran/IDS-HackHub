package unicam.it.idshackhub.model.hackathon;

import lombok.Getter;
import lombok.Setter;
import unicam.it.idshackhub.model.team.HackathonTeam;
import unicam.it.idshackhub.model.user.assignment.BaseContext;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Hackathon extends BaseContext {
    private String title;
    private String description;
    private TeamRules rules;
    private HackathonStaff staff;
    private Schedule schedule;
    private List<HackathonTeam> teams = new ArrayList<>();
}

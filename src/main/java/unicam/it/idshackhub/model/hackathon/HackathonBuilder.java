package unicam.it.idshackhub.model.hackathon;

import unicam.it.idshackhub.model.team.HackathonTeam;
import unicam.it.idshackhub.model.user.User;

import java.util.ArrayList;
import java.util.List;

public class HackathonBuilder {

    private Hackathon hackathon;

    public Hackathon getResult(){
        return hackathon;
    }

    public HackathonBuilder reset(){
        hackathon = new Hackathon();
        hackathon.setTeams(new ArrayList<>());
        return this;
    }

    public HackathonBuilder buildTitle(String title){
        this.hackathon.setTitle(title);
        return this;
    }

    public HackathonBuilder buildDescription(String description){
        this.hackathon.setDescription(description);
        return this;
    }

    public HackathonBuilder buildRules(TeamRules rules){
        this.hackathon.setRules(rules);
        return this;
    }

    public HackathonBuilder buildSchedule(Schedule schedule){
        this.hackathon.setSchedule(schedule);
        return this;
    }

    public HackathonBuilder buildStaff(User organizer){
        this.hackathon.setStaff(new HackathonStaff(organizer));
        return this;
    }


}

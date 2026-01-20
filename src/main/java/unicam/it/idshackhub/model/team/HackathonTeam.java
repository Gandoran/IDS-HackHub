package unicam.it.idshackhub.model.team;

import unicam.it.idshackhub.model.hackathon.Hackathon;

public class HackathonTeam extends AbstractTeam{
    private Team mainTeam;
    private Hackathon hackathonParticipation;

    public void setMainTeam(Team mainTeam) {
        this.mainTeam = mainTeam;
    }

    public void setHackathonParticipation(Hackathon hackathonParticipation) {
        this.hackathonParticipation = hackathonParticipation;
    }
}
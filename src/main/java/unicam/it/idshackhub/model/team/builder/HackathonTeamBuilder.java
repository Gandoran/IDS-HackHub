package unicam.it.idshackhub.model.team.builder;

import unicam.it.idshackhub.model.hackathon.Hackathon;
import unicam.it.idshackhub.model.team.HackathonTeam;
import unicam.it.idshackhub.model.team.Team;

public class HackathonTeamBuilder extends TeamBuilderAbstract<HackathonTeam, HackathonTeamBuilder> {

    public HackathonTeamBuilder() {
        super.team = new HackathonTeam();
    }

    public HackathonTeamBuilder buildMainTeam(Team mainTeam) {

        team.setMainTeam(mainTeam);
        return this;
    }


    public HackathonTeamBuilder buildHackathonParticipation(Hackathon h) {
        team.setHackathonParticipation(h);
        return this;
    }


    @Override
    public HackathonTeamBuilder reset() {
        team = new HackathonTeam();
        return this;
    }

}
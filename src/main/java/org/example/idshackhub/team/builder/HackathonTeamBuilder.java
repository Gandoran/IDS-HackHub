package org.example.idshackhub.team.builder;

import org.example.idshackhub.model.hackathon.Hackathon;
import org.example.idshackhub.team.HackathonTeam;
import org.example.idshackhub.team.Team;

public class HackathonTeamBuilder extends TeamBuilderAbstract<HackathonTeam, HackathonTeamBuilder> {

    @Override
    protected HackathonTeam createTeamInstance() {
        return new HackathonTeam();
    }

    public HackathonTeamBuilder buildMainTeam(Team mainTeam) {
        team.setMainTeam(mainTeam);
        return this;
    }


    public HackathonTeamBuilder buildHackathonParticipation(Hackathon h) {
        team.setHackathonParticipation(h);
        return this;
    }
}
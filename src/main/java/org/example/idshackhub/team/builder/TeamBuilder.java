package org.example.idshackhub.team.builder;

import org.example.idshackhub.team.Team;

public class TeamBuilder extends TeamBuilderAbstract<Team, TeamBuilder> {

    @Override
    protected Team createTeamInstance() {
        return new Team();
    }

    public TeamBuilder buildIban(String iban) {
        team.setIban(iban);
        return this;
    }
}
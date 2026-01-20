package unicam.it.idshackhub.model.team.builder;

import unicam.it.idshackhub.model.team.Team;
import unicam.it.idshackhub.model.user.User;

import java.util.ArrayList;
import java.util.List;

public class TeamBuilder extends TeamBuilderAbstract<Team, TeamBuilder> {

    public TeamBuilder() {
        super.team = new Team();
    }

    public TeamBuilder buildIban(String iban) {
        team.setIban(iban);
        return this;
    }

    @Override
    public TeamBuilder reset() {
        team = new Team();
        team.setHackathonTeams(new ArrayList<>());
        return this;
    }
}
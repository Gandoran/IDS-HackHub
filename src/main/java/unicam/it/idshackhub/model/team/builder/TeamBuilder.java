package unicam.it.idshackhub.model.team.builder;

import unicam.it.idshackhub.model.team.Team;
import unicam.it.idshackhub.model.user.User;

import java.util.List;

public class TeamBuilder extends TeamBuilderAbstract<Team, TeamBuilder> {

    @Override
    protected Team createTeamInstance() {
        return new Team();
    }

    public TeamBuilder buildIban(String iban) {
        team.setIban(iban);
        return this;
    }

    @Override
    public TeamBuilder buildLeader(User leader) {
        return null;
    }

    @Override
    public TeamBuilder buildMembers(List<User> members) {
        return null;
    }
}
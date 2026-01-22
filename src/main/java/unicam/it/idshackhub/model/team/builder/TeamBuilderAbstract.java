package unicam.it.idshackhub.model.team.builder;

import unicam.it.idshackhub.model.team.AbstractTeam;
import unicam.it.idshackhub.model.user.User;

import java.util.List;

public abstract class TeamBuilderAbstract<V extends AbstractTeam, T extends TeamBuilderAbstract<V, T>> implements ITeamBuilder<V, T> {

    protected V team;
    public abstract T reset();

    @Override
    public T buildName(String name) {
        team.setName(name);
        return self();
    }

    @Override
    public T buildDescription(String description) {
        team.setDescription(description);
        return self();
    }

    @Override
    public T buildLeader(User leader) {
        team.setLeader(leader);
        return self();
    }

    @Override
    public T buildMembers(List<User> members) {
        team.setMembers(members);
        return self();
    }

    @Override
    public V getResult() {
        return team;
    }

    @SuppressWarnings("unchecked")
    protected T self() {
        return (T) this;
    }
}

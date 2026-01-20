package unicam.it.idshackhub.model.team.builder;

import unicam.it.idshackhub.model.team.AbstractTeam;
import unicam.it.idshackhub.model.user.User;

import java.util.List;

public abstract class TeamBuilderAbstract<V extends AbstractTeam, T extends TeamBuilderAbstract<V, T>> implements ITeamBuilder<V, T> {

    protected V team;
    protected abstract V createTeamInstance();

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

    public T buildLeader(User leader) {
        team.setLeader(leader);
        return self();
    }

    public T buildMembers(List<User> members) {
        team.setMembers(members);
        return self();
    }

    @Override
    public V getTeam() {
        return team;
    }

    @SuppressWarnings("unchecked")
    protected T self() {
        return (T) this;
    }
}

package org.example.idshackhub.team.builder;

import org.example.idshackhub.team.AbstractTeam;

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
    public V getTeam() {
        return team;
    }

    @SuppressWarnings("unchecked")
    protected T self() {
        return (T) this;
    }
}

package unicam.it.idshackhub.model.team.builder;

import unicam.it.idshackhub.model.team.AbstractTeam;
import unicam.it.idshackhub.model.user.User;

import java.util.List;

/**
 * Abstract base class implementing the common logic for all Team Builders.
 * <p>
 * This class handles the construction of fields shared by all teams (name, description, leader, members).
 * It utilizes the "Curiously Recurring Template Pattern" (CRTP) via the {@link #self()} method
 * to ensure that method chaining returns the correct concrete builder type.
 * </p>
 *
 * @param <V> the specific type of {@link AbstractTeam} being built.
 * @param <T> the concrete type of the builder subclass.
 */
public abstract class TeamBuilderAbstract<V extends AbstractTeam, T extends TeamBuilderAbstract<V, T>> implements ITeamBuilder<V, T> {

    /**
     * The instance of the team currently being constructed.
     */
    protected V team;

    /**
     * {@inheritDoc}
     */
    public abstract T reset();

    /**
     * {@inheritDoc}
     */
    @Override
    public T buildName(String name) {
        team.setName(name);
        return self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T buildDescription(String description) {
        team.setDescription(description);
        return self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T buildLeader(User leader) {
        team.setLeader(leader);
        return self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T buildMembers(List<User> members) {
        team.setMembers(members);
        return self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V getResult() {
        return team;
    }

    /**
     * Helper method to cast 'this' to the concrete builder type {@code T}.
     * This is required to maintain the fluent chain in subclass implementations.
     *
     * @return this instance cast to {@code T}.
     */
    @SuppressWarnings("unchecked")
    protected T self() {
        return (T) this;
    }
}
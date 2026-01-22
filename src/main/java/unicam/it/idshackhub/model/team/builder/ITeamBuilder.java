package unicam.it.idshackhub.model.team.builder;

import unicam.it.idshackhub.model.team.AbstractTeam;
import unicam.it.idshackhub.model.user.User;

import java.util.List;

/**
 * Defines the contract for a Team Builder.
 * <p>
 * This interface uses recursive generics to support a fluent API style across inheritance hierarchies.
 * </p>
 *
 * @param <V> the type of the product being built (extends {@link AbstractTeam}).
 * @param <T> the type of the builder implementation itself (used for method chaining).
 */
public interface ITeamBuilder<V extends AbstractTeam, T extends ITeamBuilder<V, T>>  {

    /**
     * Sets the name of the team.
     *
     * @param name the name to assign.
     * @return the builder instance.
     */
    T buildName(String name);

    /**
     * Sets the description of the team.
     *
     * @param description the description to assign.
     * @return the builder instance.
     */
    T buildDescription(String description);

    /**
     * Sets the leader of the team.
     *
     * @param leader the {@link User} who will lead the team.
     * @return the builder instance.
     */
    T buildLeader(User leader);

    /**
     * Sets the list of initial members.
     *
     * @param members the list of {@link User}s participating.
     * @return the builder instance.
     */
    T buildMembers(List<User> members);

    /**
     * Returns the constructed team object.
     *
     * @return the built instance of type {@code V}.
     */
    V getResult();

    /**
     * Resets the builder state to allow creating a new object.
     *
     * @return the builder instance.
     */
    T reset();
}
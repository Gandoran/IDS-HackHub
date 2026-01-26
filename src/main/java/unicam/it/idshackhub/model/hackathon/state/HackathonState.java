package unicam.it.idshackhub.model.hackathon.state;


import unicam.it.idshackhub.model.hackathon.Hackathon;
import unicam.it.idshackhub.model.user.role.permission.Permission;

/**
 * Defines the behavior of a hackathon lifecycle phase (State pattern).
 * <p>
 * Implementations decide which {@link Permission}s are allowed in the current phase and may
 * perform automatic transitions by updating the {@link HackathonStatus} of the provided context.
 * </p>
 */
public interface HackathonState {

    /**
     * Checks whether the given permission is allowed in the current phase.
     *
     * @param perm the permission to check
     * @return {@code true} if the action is allowed, {@code false} otherwise
     */

    boolean isActionAllowed(Permission perm);

    /**
     * Applies phase-specific automatic transitions, if any.
     *
     * @param context the hackathon being evaluated for transition
     * @throws RuntimeException if the transition preconditions are not satisfied
     */

    void updateState(Hackathon context);
}
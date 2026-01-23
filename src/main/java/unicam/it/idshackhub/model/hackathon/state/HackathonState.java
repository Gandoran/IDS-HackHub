package unicam.it.idshackhub.model.hackathon.state;


import unicam.it.idshackhub.model.hackathon.Hackathon;
import unicam.it.idshackhub.model.user.role.permission.Permission;

/**
 * Simple factory for Hackathon State singletons.
 * <p>
 * Keeps state instances reusable (no need to allocate new states each transition).
 * </p>
 */
public interface HackathonState {
    boolean isActionAllowed(Permission perm);
    void updateState(Hackathon context);
}
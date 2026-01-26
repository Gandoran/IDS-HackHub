package unicam.it.idshackhub.model.hackathon.state;

/**
 * Enumerates the lifecycle phases of a hackathon.
 * <p>
 * The current status determines which actions (permissions) are allowed and whether
 * an automatic transition can occur (see {@link HackathonState} and {@link HackathonStateFactory}).
 * </p>
 */

public enum HackathonStatus {
    REGISTRATION,
    IN_PROGRESS,
    EVALUATION,
    CONCLUSION,
}

package unicam.it.idshackhub.model.hackathon.state;

/**
 * Factory providing the {@link HackathonState} behavior associated with a {@link HackathonStatus}.
 * <p>
 * States are exposed as singletons to avoid re-allocating state objects on each transition.
 * </p>
 */

public class HackathonStateFactory {

    private static final HackathonState REGISTRATION = new Registration();
    private static final HackathonState IN_PROGRESS = new InProgress();
    private static final HackathonState EVALUATION = new Evaluation();
    private static final HackathonState CONCLUSION = new Conclusion();
    private static final HackathonState ARCHIVED = new Archived();

    /**
     * Returns the state behavior corresponding to the given status.
     *
     * @param status the hackathon status (may be {@code null})
     * @return the state behavior; defaults to {@link HackathonStatus#REGISTRATION} when {@code status} is {@code null}
     */

    public static HackathonState getBehavior(HackathonStatus status) {
        if (status == null) return REGISTRATION;

        return switch (status) {
            case REGISTRATION -> REGISTRATION;
            case IN_PROGRESS -> IN_PROGRESS;
            case EVALUATION -> EVALUATION;
            case CONCLUSION -> CONCLUSION;
            case ARCHIVED ->  ARCHIVED;
        };
    }
}

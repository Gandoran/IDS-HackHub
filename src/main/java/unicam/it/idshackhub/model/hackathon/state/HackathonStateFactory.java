package unicam.it.idshackhub.model.hackathon.state;

public class HackathonStateFactory {

    private static final HackathonState REGISTRATION = new Registration();
    private static final HackathonState IN_PROGRESS = new InProgress();
    private static final HackathonState EVALUATION = new Evaluation();
    private static final HackathonState CONCLUSION = new Conclusion();

    public static HackathonState getBehavior(HackathonStatus status) {
        if (status == null) return REGISTRATION;

        return switch (status) {
            case REGISTRATION -> REGISTRATION;
            case IN_PROGRESS  -> IN_PROGRESS;
            case EVALUATION -> EVALUATION;
            case CONCLUSION   -> CONCLUSION;
        };
    }
}

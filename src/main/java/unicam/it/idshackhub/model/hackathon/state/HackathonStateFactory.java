package unicam.it.idshackhub.model.hackathon.state;

public class HackathonStateFactory {

    // Cache delle istanze per evitare di fare "new" ogni volta (Singleton pattern per gli stati)
    private static final HackathonState REGISTRATION = new Registration();
    private static final HackathonState IN_PROGRESS = new InProgress();
    // ... altri ...

    public static HackathonState getBehavior(HackathonStatus status) {
        if (status == null) return REGISTRATION; // Default

        return switch (status) {
            case REGISTRATION -> REGISTRATION;
            case IN_PROGRESS  -> IN_PROGRESS;
            case EVALUATION -> new Evaluation(); // O usa la costante statica
            case CONCLUSION   -> new Conclusion();
        };
    }
}

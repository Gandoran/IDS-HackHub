package unicam.it.idshackhub.model.hackathon.state;

import unicam.it.idshackhub.model.hackathon.Hackathon;
import unicam.it.idshackhub.model.user.role.permission.Permission;

public class Evaluation implements HackathonState {

    @Override
    public boolean isActionAllowed(Permission perm) {
        return switch (perm) {
            case Can_Vote, Can_End_Evaluation_State -> true;
            default -> false;
        };
    }

    /**
     * Automatic transition:
     * Evaluation -> Conclusion when all submissions have a vote.
     */
    @Override
    public void updateState(Hackathon context) {
        boolean allVoted = context.getSubmissions().stream()
                .allMatch(s -> s.getVote() != null);

        if (!allVoted) return;

        context.setStatus(HackathonStatus.CONCLUSION);
    }
}

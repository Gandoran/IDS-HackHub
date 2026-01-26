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
     * Performs the automatic transition to {@link HackathonStatus#CONCLUSION} when all submissions have been voted.
     *
     * @param context the hackathon context
     * @throws RuntimeException if not all submissions have a vote
     */
    @Override
    public void updateState(Hackathon context) {
        boolean allVoted = context.getSubmissions().stream()
                .allMatch(s -> s.getVote() != null);
        if (!allVoted) throw new RuntimeException("Not all submissions are voted");
        context.setStatus(HackathonStatus.CONCLUSION);
    }
}

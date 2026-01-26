package unicam.it.idshackhub.model.hackathon.state;

import unicam.it.idshackhub.model.hackathon.Hackathon;
import unicam.it.idshackhub.model.hackathon.Schedule;
import unicam.it.idshackhub.model.user.role.permission.Permission;

import java.time.LocalDateTime;

/**
 * In-Progress phase:
 * - Teams can submit their project
 */
public class InProgress implements HackathonState {

    @Override
    public boolean isActionAllowed(Permission perm) {
        return switch (perm) {
            case Can_Submit -> true;
            default -> false;
        };
    }

    /**
     * Advances the hackathon to the {@link HackathonStatus#EVALUATION} phase once the submission deadline has passed.
     * <p>
     * This transition is time-driven: if the hackathon schedule (or its end date) is missing, or the current time is still
     * before the end date, the state is not changed.
     * <p>
     * When the deadline is reached, teams without a submission are removed. If no submitted teams remain after the cleanup,
     * the hackathon cannot enter evaluation and an exception is thrown.
     *
     * @param context the hackathon whose state may be updated
     * @throws IllegalStateException if the deadline has passed but no submissions were posted
     */

    @Override
    public void updateState(Hackathon context) {
        Schedule schedule = context.getSchedule();
        if (schedule == null || schedule.getEndDate() == null) return;

        if (LocalDateTime.now().isBefore(schedule.getEndDate())) return;

        if (context.getTeams() != null) {
            context.getTeams().removeIf(t -> t.getSubmission() == null);
        }

        if (context.getTeams() == null || context.getTeams().isEmpty()) {
            throw new IllegalStateException("Cannot move to evaluation: no submissions were posted");
        }

        context.setStatus(HackathonStatus.EVALUATION);
    }
}

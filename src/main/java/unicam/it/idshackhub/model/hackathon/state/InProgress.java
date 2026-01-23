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
     * Automatic transition:
     * InProgress -> Evaluation when now >= schedule.endDate
     *
     * Fail-fast: if transition time arrives but there are no submissions,
     * throws IllegalStateException.
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

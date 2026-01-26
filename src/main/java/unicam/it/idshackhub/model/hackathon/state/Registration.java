package unicam.it.idshackhub.model.hackathon.state;

import unicam.it.idshackhub.model.hackathon.Hackathon;
import unicam.it.idshackhub.model.hackathon.Schedule;
import unicam.it.idshackhub.model.user.role.permission.Permission;

import java.time.LocalDateTime;

/**
 * Registration phase:
 * - Organizer can invite a judge
 * - Team leaders can register a team
 */
public class Registration implements HackathonState {

    @Override
    public boolean isActionAllowed(Permission perm) {
        return switch (perm) {
            case Can_Invite_Judge -> true;
            case Can_Register_Team -> true;
            default -> false;
        };
    }

    /**
     * Advances the hackathon to the {@link HackathonStatus#IN_PROGRESS} phase once the start date has been reached.
     * <p>
     * This transition is time-driven: if the hackathon schedule (or its start date) is missing, or the current time is still
     * before the start date, the state is not changed.
     * <p>
     * When the start date is reached, the method verifies that the hackathon is ready to begin:
     * a judge must be assigned and the minimum number of teams required by the rules must be met.
     *
     * @param context the hackathon whose state may be updated
     * @throws IllegalStateException if the hackathon is due to start but a judge is missing
     * @throws IllegalStateException if the hackathon is due to start but the minimum number of teams is not reached
     */

    @Override
    public void updateState(Hackathon context) {
        Schedule schedule = context.getSchedule();
        if (schedule == null || schedule.getStartDate() == null) return;

        if (LocalDateTime.now().isBefore(schedule.getStartDate())) return;

        if (context.getStaff() == null || context.getStaff().getJudge() == null) {
            throw new IllegalStateException("Cannot start hackathon: missing judge");
        }
        if (context.getTeams() == null || context.getTeams().size() < context.getRules().getMinTeams()) {
            throw new IllegalStateException("Cannot start hackathon: minimum number of teams not reached");
        }
        context.setStatus(HackathonStatus.IN_PROGRESS);
    }
}
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
     * Automatic transition:
     * Registration -> Inprogress when now >= schedule.startDate
     *
     * Fail-fast (no Cancelled): if transition time arrives but prerequisites are missing,
     * throws IllegalStateException.
     */
    @Override
    public void updateState(Hackathon context) {
        Schedule schedule = context.getSchedule();
        if (schedule == null || schedule.getStartDate() == null) return;

        if (LocalDateTime.now().isBefore(schedule.getStartDate())) return;

        // prerequisites to start the hackathon
        if (context.getStaff() == null || context.getStaff().getJudge() == null) {
            throw new IllegalStateException("Cannot start hackathon: missing judge");
        }
        if (context.getTeams() == null || context.getTeams().size() < context.getRules().getMinTeams()) {
            throw new IllegalStateException("Cannot start hackathon: minimum number of teams not reached");
        }
        context.setStatus(HackathonStatus.IN_PROGRESS);
    }
}
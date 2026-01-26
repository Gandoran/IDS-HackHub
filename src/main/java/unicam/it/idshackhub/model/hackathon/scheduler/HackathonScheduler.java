package unicam.it.idshackhub.model.hackathon.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import unicam.it.idshackhub.service.HackathonService;

/**
 * Component responsible for orchestrating scheduled tasks related to Hackathon lifecycles.
 * <p>
 * This scheduler ensures that hackathon states (e.g., transitions from 'Open' to 'Closed')
 * are kept synchronized with the system clock without manual intervention.
 * </p>
 */
@Component
public class HackathonScheduler {

    private final HackathonService hackathonService;

    @Autowired
    public HackathonScheduler(HackathonService hackathonService) {
        this.hackathonService = hackathonService;
    }

    /**
     * Periodic task that triggers the update of all hackathon states.
     * <p>
     * This method runs automatically based on a cron expression (every hour at minute 0).
     * It invokes the service layer to evaluate deadlines and transition hackathons
     * to their appropriate status.
     * </p>
     *
     * @throws RuntimeException if an error occurs during the bulk update process.
     */
    @Scheduled(cron = "0 0 * * * *")
    public void checkHackathonDeadlines() {
        try {
            hackathonService.updateAllStates();
        } catch (Exception e) {
            throw new RuntimeException("Error while updating the state");
        }
    }
}
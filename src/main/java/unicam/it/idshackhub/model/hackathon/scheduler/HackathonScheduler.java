package unicam.it.idshackhub.model.hackathon.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import unicam.it.idshackhub.service.HackathonService;

@Component
public class HackathonScheduler {

    private final HackathonService hackathonService;

    @Autowired
    public HackathonScheduler(HackathonService hackathonService) {
        this.hackathonService = hackathonService;
    }

    @Scheduled(cron = "0 0 * * * *")
    public void checkHackathonDeadlines() {
        try {
            hackathonService.updateAllStates();
        } catch (Exception e) {
            throw new RuntimeException("Errore durante l'aggiornamento degli stati: ");
        }
    }
}
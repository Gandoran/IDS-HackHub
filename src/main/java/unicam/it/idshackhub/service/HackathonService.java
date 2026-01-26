package unicam.it.idshackhub.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import unicam.it.idshackhub.model.hackathon.Hackathon;
import unicam.it.idshackhub.model.hackathon.state.HackathonStatus;
import unicam.it.idshackhub.repository.HackathonRepository;
import unicam.it.idshackhub.repository.UserRepository;

import java.util.List;

/**
 * Provides operations related to {@link unicam.it.idshackhub.model.hackathon.Hackathon} lifecycle management.
 * <p>
 * This service is mainly responsible for periodic, system-driven tasks, such as synchronizing the state of
 * Hackathons whose status can advance automatically according to their schedule.
 * </p>
 */

@Service
public class HackathonService {

    private final HackathonRepository hackathonRepository;

    @Autowired
    public HackathonService(HackathonRepository hackathonRepository, UserRepository userRepository) {
        this.hackathonRepository = hackathonRepository;
    }

    /**
     * Updates the status of all Hackathons that are managed by the scheduler.
     * <p>
     * For each eligible Hackathon, the current status is compared with the status after invoking
     * {@link unicam.it.idshackhub.model.hackathon.Hackathon#updateState()}; the entity is persisted only when a
     * transition occurred.
     * </p>
     */

    @Transactional
    public void updateAllStates() {
        List<Hackathon> autoManagedHackathons = hackathonRepository.findHackathonsForScheduler();
        for (Hackathon h : autoManagedHackathons) {
            HackathonStatus oldState = h.getStatus();
            h.updateState();
            HackathonStatus newState = h.getStatus();
            if (oldState != newState) {
                hackathonRepository.save(h);
            }
        }
    }
}
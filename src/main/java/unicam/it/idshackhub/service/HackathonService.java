package unicam.it.idshackhub.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import unicam.it.idshackhub.model.hackathon.Hackathon;
import unicam.it.idshackhub.model.hackathon.state.HackathonStatus;
import unicam.it.idshackhub.repository.HackathonRepository;

import java.util.List;

import static unicam.it.idshackhub.service.utils.PermissionChecker.checkPermission;

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
    public HackathonService(HackathonRepository hackathonRepository) {
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
        List<Hackathon> autoManagedHackathons = hackathonRepository.findActiveHackathonsForScheduler();
        for (Hackathon h : autoManagedHackathons) {
            HackathonStatus oldState = h.getStatus();
            try {
                h.updateState();
                if (oldState != h.getStatus()) {
                    hackathonRepository.save(h);
                }
            } catch (IllegalStateException e) {
                h.setStatus(HackathonStatus.ARCHIVED);
                hackathonRepository.save(h);
            } catch (Exception e) {
                throw new RuntimeException("Error while updating the state");
            }
        }
    }
}
package unicam.it.idshackhub.service;

import jakarta.transaction.TransactionScoped;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import unicam.it.idshackhub.model.hackathon.Hackathon;
import unicam.it.idshackhub.model.hackathon.state.HackathonStatus;
import unicam.it.idshackhub.model.team.HackathonTeam;
import unicam.it.idshackhub.model.user.User;
import unicam.it.idshackhub.model.user.role.permission.Permission;
import unicam.it.idshackhub.repository.HackathonRepository;

import java.util.List;

import static unicam.it.idshackhub.service.PermissionChecker.checkPermission;

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
    private final PayPalService payPalService;

    @Autowired
    public HackathonService(HackathonRepository hackathonRepository,PayPalService payPalService) {
        this.payPalService = payPalService;
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

    @Transactional
    public void SendPrizeToWinner(User organzier, HackathonTeam winnerTeam){
        if(!checkPermission(organzier, Permission.Can_Proclamate_Winner)){
            throw new RuntimeException("You do not have permission to proclamate winner");
        }
        String winnerEmail = winnerTeam.getMainTeam().getPayPalAccount();
        Double prize = winnerTeam.getHackathonParticipation().getPrize();
        String approvalUrl = payPalService.initiatePayment(prize, winnerEmail);
    }
}
package unicam.it.idshackhub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unicam.it.idshackhub.dto.InviteStaffDTO;
import unicam.it.idshackhub.service.OrganizerService;

@RestController
@RequestMapping("/api/organizer")
public class OrganizerController {
    private final OrganizerService organizerService;

    @Autowired
    public OrganizerController(OrganizerService organizerService) {
        this.organizerService = organizerService;
    }

    @PostMapping("/invite-staff")
    public ResponseEntity<?> inviteStaff(@RequestBody InviteStaffDTO dto) {
        organizerService.inviteStaff(dto.organizerId(), dto.recipientId(), dto.hackathonId(), dto.role());
        return ResponseEntity.ok("Invitation sent.");
    }

    @PostMapping("{id}/proclaim-winner")
    public ResponseEntity<?> proclaimWinner(@PathVariable Long hackathonId, @RequestParam Long organizerId) {
        organizerService.proclaimWinner(organizerId, hackathonId);
        return ResponseEntity.ok("Winner proclaimed.");
    }
}
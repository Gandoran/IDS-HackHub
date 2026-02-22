package unicam.it.idshackhub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unicam.it.idshackhub.controller.dtoResponse.HackathonResponseDTO;
import unicam.it.idshackhub.controller.dtoResponse.HackathonTeamResponseDTO;
import unicam.it.idshackhub.controller.dtoResponse.Mapper.IMapper;
import unicam.it.idshackhub.controller.dtoResponse.Mapper.MapperDTO;
import unicam.it.idshackhub.controller.dtoResponse.MessageResponseDTO;
import unicam.it.idshackhub.dto.GetHackathonDTO;
import unicam.it.idshackhub.dto.HackathonDTO;
import unicam.it.idshackhub.dto.InviteStaffDTO;
import unicam.it.idshackhub.dto.WinnerDTO;
import unicam.it.idshackhub.service.OrganizerService;

@RestController
@RequestMapping("/api/organizer")
public class OrganizerController {
    private final OrganizerService organizerService;
    private final IMapper mapper;

    @Autowired
    public OrganizerController(OrganizerService organizerService, MapperDTO mapper) {
        this.mapper = mapper;
        this.organizerService = organizerService;
    }

    @PostMapping("/invite-staff")
    public ResponseEntity<MessageResponseDTO> inviteStaff(@RequestHeader Long organizerId, @RequestBody InviteStaffDTO dto) {
        return ResponseEntity.ok(mapper.toDto(
                organizerService.inviteStaff(organizerId, dto.recipientId(), dto.hackathonId(), dto.role())));
    }

    @PostMapping("/proclaim-winner")
    public ResponseEntity<HackathonTeamResponseDTO> proclaimWinner(@RequestHeader Long organizerId, @RequestBody WinnerDTO dto) {
        return ResponseEntity.ok(mapper.toDto(
                organizerService.proclaimWinner(organizerId, dto.hackathonId())));
    }

    @PostMapping("{hackathonId}/force-update")
    public ResponseEntity<?> forceUpdate(@PathVariable Long hackathonId) {
        organizerService.forceUpdate(hackathonId);
        return ResponseEntity.ok("Hackathon updated.");
    }
}
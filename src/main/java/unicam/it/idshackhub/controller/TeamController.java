package unicam.it.idshackhub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unicam.it.idshackhub.controller.dtoResponse.HackathonTeamResponseDTO;
import unicam.it.idshackhub.controller.dtoResponse.Mapper.IMapper;
import unicam.it.idshackhub.controller.dtoResponse.Mapper.MapperDTO;
import unicam.it.idshackhub.controller.dtoResponse.MessageResponseDTO;
import unicam.it.idshackhub.dto.InviteUserDTO;
import unicam.it.idshackhub.dto.RegisterTeamDTO;
import unicam.it.idshackhub.dto.ReplyDTO;
import unicam.it.idshackhub.model.team.HackathonTeam;
import unicam.it.idshackhub.service.TeamService;

@RestController
@RequestMapping("/api/team")
public class TeamController {

    private final TeamService teamService;
    private final IMapper mapper;

    @Autowired
    public TeamController(TeamService teamService, MapperDTO mapper) {
        this.mapper = mapper;
        this.teamService = teamService;
    }

    @PostMapping("/register")
    public ResponseEntity<HackathonTeamResponseDTO> registerTeamToHackathon(@RequestHeader Long leaderId, @RequestBody RegisterTeamDTO dto) {
        return ResponseEntity.ok(mapper.toDto(teamService.registerHackathonTeam(leaderId, dto.teamName(), dto.description(),
                                                        dto.teamLeaderId(), dto.memberIds(), dto.hackathonId())));
    }

    @PostMapping("/invite-user")
    public ResponseEntity<MessageResponseDTO> inviteUser(@RequestHeader Long leaderId,@RequestBody InviteUserDTO dto) {
        return ResponseEntity.ok(mapper.toDto(teamService.inviteUserToTeam(leaderId, dto.userId())));
    }

    @PostMapping("/join-request/process")
    public ResponseEntity<MessageResponseDTO> processJoinRequest(@RequestHeader Long leaderId, @RequestBody ReplyDTO dto) {
        return ResponseEntity.ok(mapper.toDto(teamService.manageJoinRequest(leaderId, dto.messageId(), dto.accepted())));
    }
}
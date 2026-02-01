package unicam.it.idshackhub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unicam.it.idshackhub.dto.InviteUserDTO;
import unicam.it.idshackhub.dto.RegisterTeamDTO;
import unicam.it.idshackhub.dto.ReplyDTO;
import unicam.it.idshackhub.model.team.HackathonTeam;
import unicam.it.idshackhub.service.TeamService;

@RestController
@RequestMapping("/api/team")
public class TeamController {

    private final TeamService teamService;

    @Autowired
    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerTeamToHackathon(@RequestBody RegisterTeamDTO dto) {
        HackathonTeam hTeam = teamService.registerHackathonTeam(dto.leaderId(), dto.teamName(), dto.description(),
                                                        dto.hackteamleaderId(), dto.memberIds(), dto.hackathonId());
        return ResponseEntity.ok("Team registered to Hackathon. HackathonTeam ID: " + hTeam.getId());
    }

    @PostMapping("/invite-user")
    public ResponseEntity<?> inviteUser(@RequestBody InviteUserDTO dto) {
        teamService.inviteUserToTeam(dto.userId(), dto.leaderId());
        return ResponseEntity.ok("Invitation sent to user.");
    }

    @PostMapping("/join-request/process")
    public ResponseEntity<?> processJoinRequest(@RequestParam Long leaderId, @RequestBody ReplyDTO dto) {
        teamService.manageJoinRequest(leaderId, dto.messageId(), dto.accepted());
        return ResponseEntity.ok("Join request processed.");
    }
}
package unicam.it.idshackhub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unicam.it.idshackhub.dto.HelpRequestDTO;
import unicam.it.idshackhub.dto.SubmissionDTO;
import unicam.it.idshackhub.model.user.User;
import unicam.it.idshackhub.service.HackathonTeamService;

import java.util.List;

@RestController
@RequestMapping("/api/hackteam")
public class HackathonTeamController {

    private final HackathonTeamService hackathonTeamService;

    @Autowired
    public HackathonTeamController(HackathonTeamService hackathonTeamService) {
        this.hackathonTeamService = hackathonTeamService;
    }

    @PostMapping("/submission")
    public ResponseEntity<?> postSubmission(@RequestBody SubmissionDTO dto) {
        hackathonTeamService.postSubmission(dto.hackathonLeaderId(), dto.description(), dto.hackathonTeamId(), dto.hackathonId());
        return ResponseEntity.ok("Submission posted successfully.");
    }

    @PostMapping("/help")
    public ResponseEntity<?> requestHelp(@RequestBody HelpRequestDTO dto) {
        hackathonTeamService.requestHelp(dto.userId(), dto.hackathonId(), dto.description());
        return ResponseEntity.ok("Help request sent to mentors.");
    }

    @GetMapping("/{id}/participants")
    public ResponseEntity<?> getHackathonTeamParticipants(@PathVariable Long id) {
        List<User> participants = hackathonTeamService.getTeamParticipants(id);
        return ResponseEntity.ok(participants);
    }
}
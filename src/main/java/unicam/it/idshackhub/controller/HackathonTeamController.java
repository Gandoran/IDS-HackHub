package unicam.it.idshackhub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import unicam.it.idshackhub.dto.HelpRequestDTO;
import unicam.it.idshackhub.dto.SubmissionDTO;
import unicam.it.idshackhub.service.HackathonTeamService;

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
}
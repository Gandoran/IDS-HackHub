package unicam.it.idshackhub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import unicam.it.idshackhub.dto.HelpResponseDTO;
import unicam.it.idshackhub.service.MentorService;

@RestController
@RequestMapping("/api/mentor")
public class MentorController {
    private final MentorService mentorService;

    @Autowired
    public MentorController(MentorService mentorService) {
        this.mentorService = mentorService;
    }

    @PostMapping("/requests/process")
    public ResponseEntity<?> processRequest(@RequestBody HelpResponseDTO dto) {
        mentorService.manageRequest(dto.mentorId(), dto.hackathonId(), dto.messageId(), dto.accepted());
        return ResponseEntity.ok("Request processed.");
    }
}

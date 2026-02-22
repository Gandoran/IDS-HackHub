package unicam.it.idshackhub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unicam.it.idshackhub.controller.dtoResponse.Mapper.IMapper;
import unicam.it.idshackhub.controller.dtoResponse.Mapper.MapperDTO;
import unicam.it.idshackhub.controller.dtoResponse.MessageResponseDTO;
import unicam.it.idshackhub.controller.dtoResponse.SubmissionResponseDTO;
import unicam.it.idshackhub.dto.HelpRequestDTO;
import unicam.it.idshackhub.dto.SubmissionDTO;
import unicam.it.idshackhub.model.user.User;
import unicam.it.idshackhub.service.HackathonTeamService;

import java.util.List;

@RestController
@RequestMapping("/api/hackteam")
public class HackathonTeamController {

    private final HackathonTeamService hackathonTeamService;
    private final IMapper mapper;

    @Autowired
    public HackathonTeamController(HackathonTeamService hackathonTeamService, MapperDTO mapper) {
        this.mapper = mapper;
        this.hackathonTeamService = hackathonTeamService;
    }

    @PostMapping("/submission")
    public ResponseEntity<SubmissionResponseDTO> postSubmission(@RequestHeader Long hackathonTeamLeaderId, @RequestBody SubmissionDTO dto) {
        return ResponseEntity.ok(mapper.toDto(
                hackathonTeamService.postSubmission(hackathonTeamLeaderId, dto.description(), dto.hackathonTeamId(), dto.hackathonId())));
    }

    @PostMapping("/help")
    public ResponseEntity<MessageResponseDTO> requestHelp(@RequestHeader Long memberId, @RequestBody HelpRequestDTO dto) {
        return ResponseEntity.ok(mapper.toDto(
                hackathonTeamService.requestHelp(memberId, dto.hackathonId(), dto.description())));

    }

    @GetMapping("/{id}/participants")
    public ResponseEntity<?> getHackathonTeamParticipants(@PathVariable Long id) {
        List<User> participants = hackathonTeamService.getTeamParticipants(id);
        return ResponseEntity.ok(participants);
    }
}
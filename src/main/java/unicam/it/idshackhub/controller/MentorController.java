package unicam.it.idshackhub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unicam.it.idshackhub.controller.dtoResponse.Mapper.IMapper;
import unicam.it.idshackhub.controller.dtoResponse.Mapper.MapperDTO;
import unicam.it.idshackhub.controller.dtoResponse.MessageResponseDTO;
import unicam.it.idshackhub.dto.EmailCallDTO;
import unicam.it.idshackhub.dto.HelpResponseDTO;
import unicam.it.idshackhub.service.MentorService;

@RestController
@RequestMapping("/api/mentor")
public class MentorController {
    private final MentorService mentorService;
    private final IMapper mapper;

    @Autowired
    public MentorController(MentorService mentorService, MapperDTO mapper) {
        this.mapper = mapper;
        this.mentorService = mentorService;
    }

    @PostMapping("/requests/process")
    public ResponseEntity<MessageResponseDTO> processRequest(@RequestHeader Long mentorId, @RequestBody HelpResponseDTO dto) {
        return ResponseEntity.ok(mapper.toDto(
                mentorService.manageRequest(mentorId, dto.hackathonId(), dto.messageId(), dto.accepted())));
    }

    @PostMapping("/request/inviteWithEmail")
    public ResponseEntity<?> inviteWithEmail(@RequestHeader Long mentorId,@RequestBody EmailCallDTO dto) {
        mentorService.sendCallEmail(mentorId,dto.title(),dto.description(),
                dto.virtualRoom(),dto.startTime(),dto.endTime(),dto.hackathonId(),dto.receiverId());
        return ResponseEntity.ok("Invite email processed.");
    }
}

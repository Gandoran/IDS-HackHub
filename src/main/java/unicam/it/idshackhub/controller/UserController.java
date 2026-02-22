package unicam.it.idshackhub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unicam.it.idshackhub.controller.dtoResponse.Mapper.IMapper;
import unicam.it.idshackhub.controller.dtoResponse.Mapper.MapperDTO;
import unicam.it.idshackhub.controller.dtoResponse.MessageResponseDTO;
import unicam.it.idshackhub.dto.JoinRequestDTO;
import unicam.it.idshackhub.dto.OrganizerDTO;
import unicam.it.idshackhub.dto.ReplyDTO;
import unicam.it.idshackhub.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final IMapper mapper;

    @Autowired
    public UserController(UserService userService, MapperDTO mapper) {
        this.mapper = mapper;
        this.userService = userService;
    }

    @PostMapping("/organizer-request")
    public ResponseEntity<MessageResponseDTO> sendOrganizerRequest(@RequestHeader Long userId, @RequestBody OrganizerDTO dt) {
        return ResponseEntity.ok(mapper.toDto(userService.sendVerifyRequest(userId, dt.content())));
    }

    @PostMapping("/join-team")
    public ResponseEntity<MessageResponseDTO> sendJoinTeamRequest(@RequestHeader Long userId, @RequestBody JoinRequestDTO dto) {
        return ResponseEntity.ok(mapper.toDto(userService.sendJoinRequest(userId, dto.teamId(), dto.content())));
    }

    @PostMapping("/manage-invite")
    public ResponseEntity<MessageResponseDTO> manageInvite(@RequestHeader Long userId, @RequestBody ReplyDTO dto) {
        return ResponseEntity.ok(mapper.toDto(userService.manageInvites(dto.messageId(), userId, dto.accepted())));
    }
}
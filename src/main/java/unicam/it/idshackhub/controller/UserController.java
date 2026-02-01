package unicam.it.idshackhub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unicam.it.idshackhub.dto.ReplyDTO;
import unicam.it.idshackhub.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/{userId}/verify-request")
    public ResponseEntity<?> sendVerifyRequest(@PathVariable Long userId, @RequestBody String content) {
        userService.sendVerifyRequest(userId, content);
        return ResponseEntity.ok("Verification request sent.");
    }

    @PostMapping("/{userId}/join-team/{teamId}")
    public ResponseEntity<?> sendJoinTeamRequest(@PathVariable Long userId, @PathVariable Long teamId, @RequestBody String content) {
        userService.sendJoinRequest(userId, teamId, content);
        return ResponseEntity.ok("Join request sent to team leader.");
    }

    @PostMapping("/{userId}/manage-invite")
    public ResponseEntity<?> manageInvite(@PathVariable Long userId, @RequestBody ReplyDTO dto) {
        userService.manageInvites(dto.messageId(), userId, dto.accepted());
        return ResponseEntity.ok("Invite processed.");
    }
}
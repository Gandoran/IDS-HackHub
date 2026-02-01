package unicam.it.idshackhub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unicam.it.idshackhub.dto.ReplyDTO;
import unicam.it.idshackhub.service.AdminService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/requests/process")
    public ResponseEntity<?> processRequest(@RequestParam Long adminId, @RequestBody ReplyDTO dto) {
        adminService.manageVerificationRequest(adminId, dto.messageId(), dto.accepted());
        return ResponseEntity.ok("Request processed successfully.");
    }
}
package unicam.it.idshackhub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unicam.it.idshackhub.controller.dtoResponse.Mapper.IMapper;
import unicam.it.idshackhub.controller.dtoResponse.Mapper.MapperDTO;
import unicam.it.idshackhub.controller.dtoResponse.MessageResponseDTO;
import unicam.it.idshackhub.dto.ReplyDTO;
import unicam.it.idshackhub.model.message.Message;
import unicam.it.idshackhub.service.AdminService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final IMapper mapper;

    @Autowired
    public AdminController(AdminService adminService, MapperDTO mapper) {
        this.mapper = mapper;
        this.adminService = adminService;
    }

    @PostMapping("/requests/process")
    public ResponseEntity<MessageResponseDTO> processRequest(@RequestHeader Long adminId, @RequestBody ReplyDTO dto) {
        return ResponseEntity.ok(mapper.toDto(
                adminService.manageOrganizerRequest(adminId, dto.messageId(), dto.accepted())));
    }
}
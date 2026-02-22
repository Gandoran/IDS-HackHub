package unicam.it.idshackhub.dto;

import unicam.it.idshackhub.model.user.role.ContextRole;

public record InviteStaffDTO(
        Long recipientId,
        Long hackathonId,
        ContextRole role
) {}
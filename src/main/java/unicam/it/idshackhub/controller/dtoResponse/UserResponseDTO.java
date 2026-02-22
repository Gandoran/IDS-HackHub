package unicam.it.idshackhub.controller.dtoResponse;

import unicam.it.idshackhub.model.user.role.GlobalRole;

public record UserResponseDTO(
        Long id,
        String username,
        String email,
        GlobalRole globalRole,
        Long userTeamId
) {}

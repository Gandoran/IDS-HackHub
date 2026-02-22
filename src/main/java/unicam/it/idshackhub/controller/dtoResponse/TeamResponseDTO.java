package unicam.it.idshackhub.controller.dtoResponse;

import java.util.List;

public record TeamResponseDTO(
        String name,
        String description,
        UserResponseDTO leader,
        List<UserResponseDTO> members,
        String payPalAccount
) {}

package unicam.it.idshackhub.controller.dtoResponse;

import java.util.List;

public record HackathonStaffResponseDTO(
        UserResponseDTO organizer,
        UserResponseDTO judge,
        List<UserResponseDTO> mentors
) {}

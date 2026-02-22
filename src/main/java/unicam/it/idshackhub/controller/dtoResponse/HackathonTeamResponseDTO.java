package unicam.it.idshackhub.controller.dtoResponse;

import java.util.List;

public record HackathonTeamResponseDTO(
        String name,
        String description,
        UserResponseDTO leader,
        List<UserResponseDTO> members,
        TeamResponseDTO mainTeam,
        Long hackathonId,
        SubmissionResponseDTO submission
) {}

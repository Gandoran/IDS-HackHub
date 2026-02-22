package unicam.it.idshackhub.dto;

import java.util.List;

public record RegisterTeamDTO(
        String teamName,
        String description,
        Long teamLeaderId,
        List<Long> memberIds,
        Long hackathonId
) {}
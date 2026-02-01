package unicam.it.idshackhub.dto;

import java.util.List;

public record RegisterTeamDTO(
        Long leaderId,
        String teamName,
        String description,
        Long hackteamleaderId,
        List<Long> memberIds,
        Long hackathonId
) {}
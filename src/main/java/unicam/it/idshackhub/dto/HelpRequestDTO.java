package unicam.it.idshackhub.dto;

public record HelpRequestDTO(
        Long userId,
        Long hackathonId,
        String description
) {}

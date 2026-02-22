package unicam.it.idshackhub.dto;

public record HelpResponseDTO(
        Long hackathonId,
        Long messageId,
        boolean accepted
) {}

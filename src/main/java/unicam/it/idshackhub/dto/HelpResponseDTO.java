package unicam.it.idshackhub.dto;

public record HelpResponseDTO(
        Long mentorId,
        Long hackathonId,
        Long messageId,
        boolean accepted
) {}

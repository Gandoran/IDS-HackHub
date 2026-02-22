package unicam.it.idshackhub.dto;

public record SubmissionDTO(
        Long hackathonTeamId,
        Long hackathonId,
        String description
) {}
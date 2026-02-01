package unicam.it.idshackhub.dto;

public record SubmissionDTO(
        Long hackathonLeaderId,
        Long hackathonTeamId,
        Long hackathonId,
        String description
) {}
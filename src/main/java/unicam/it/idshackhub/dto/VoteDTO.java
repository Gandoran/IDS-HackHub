package unicam.it.idshackhub.dto;

public record VoteDTO(
        Long submissionId,
        Long hackathonId,
        int vote
) {}
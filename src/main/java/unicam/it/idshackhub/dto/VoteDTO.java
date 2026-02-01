package unicam.it.idshackhub.dto;

public record VoteDTO(
        Long judgeId,
        Long submissionId,
        Long hackathonId,
        int vote
) {}
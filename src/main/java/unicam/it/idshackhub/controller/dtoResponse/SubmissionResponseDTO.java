package unicam.it.idshackhub.controller.dtoResponse;

import java.time.LocalDateTime;

public record SubmissionResponseDTO(
        Long id,
        String description,
        Integer vote,
        LocalDateTime submissionDate
) {}

package unicam.it.idshackhub.dto;

import unicam.it.idshackhub.service.email.icalendar.ICalendarDetails;

import java.time.LocalDateTime;

public record EmailCallDTO(
        Long receiverId,
        Long hackathonId,
        String title,
        String description,
        String virtualRoom,
        LocalDateTime startTime,
        LocalDateTime endTime
)
{ }

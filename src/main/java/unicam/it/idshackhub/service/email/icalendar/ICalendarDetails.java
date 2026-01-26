package unicam.it.idshackhub.service.email.icalendar;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter @Getter
public class ICalendarDetails {
    private String title;
    private String description;
    private String virtualRoom;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}

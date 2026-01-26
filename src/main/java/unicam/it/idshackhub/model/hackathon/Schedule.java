package unicam.it.idshackhub.model.hackathon;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Defines the logistical details of a Hackathon, including its timeline and venue.
 * <p>
 * This class encapsulates the start and end dates of the event, as well as the
 * deadline or start time for registrations. It ensures that the dates provided
 * follow a logical consistency upon instantiation.
 * </p>
 */
@Embeddable
@Getter @Setter
@NoArgsConstructor
public class Schedule {

    /**
     * The date and time when the registration phase begins.
     */
    private LocalDateTime startRegistrationDate;

    /**
     * The date and time when the actual Hackathon event begins.
     */
    private LocalDateTime startDate;

    /**
     * The date and time when the Hackathon event concludes.
     */
    private LocalDateTime endDate;

    /**
     * The physical or virtual location where the event takes place (e.g., "Online", "Room A1").
     */
    private String location;

    /**
     * Constructs a new Schedule with the specified dates and location.
     * <p>
     * This constructor performs a validation check to ensure that the dates are not null
     * and follow a chronological order (e.g., registration starts before the event starts).
     * If validation fails, the fields are not set.
     * </p>
     *
     * @param startRegistrationDate the start of the registration period.
     * @param startDate             the start of the event.
     * @param endDate               the end of the event.
     * @param location              the venue of the event.
     */
    public Schedule(LocalDateTime startRegistrationDate, LocalDateTime startDate, LocalDateTime endDate, String location) {
        if (startRegistrationDate != null && startDate != null && endDate != null && location != null) {
            if(startRegistrationDate.isBefore(startDate) && endDate.isAfter(startDate)) {
                this.startRegistrationDate = startRegistrationDate;
                this.startDate = startDate;
                this.endDate = endDate;
                this.location = location;
            }
        }
    }
}
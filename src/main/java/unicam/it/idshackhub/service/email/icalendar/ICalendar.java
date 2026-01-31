package unicam.it.idshackhub.service.email.icalendar;

/**
 * Service interface for generating standard iCalendar (.ics) files.
 * Provides the functionality to transform event details into a format compatible
 * with external calendar applications (Google Calendar, Outlook, etc.).
 */
public interface ICalendar {
    /**
     * Generates a byte array representing an .ics file for a specific event.
     * Includes logical validation of event details and formats the data according to RFC 5545.
     * * @param event The domain details of the event.
     * @param recipientEmail The email address of the attendee to be included in the invitation.
     * @return A byte array containing the iCalendar file data.
     */
    byte[] generateIcsFile(ICalendarDetails event, String recipientEmail) throws Exception;
}

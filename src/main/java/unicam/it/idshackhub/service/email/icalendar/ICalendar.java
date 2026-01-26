package unicam.it.idshackhub.service.email.icalendar;

public interface ICalendar {
    byte[] generateIcsFile(ICalendarDetails event, String recipientEmail) throws Exception;
}

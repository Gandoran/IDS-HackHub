package unicam.it.idshackhub.service.email.icalendar;

import io.github.cdimascio.dotenv.Dotenv;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class ICalendarGenerator implements ICalendar {

    private static final DateTimeFormatter ICS_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");
    private static final String NEWLINE = "\r\n";

    @Override
    public byte[] generateIcsFile(ICalendarDetails event, String recipientEmail) throws Exception {
        StringBuilder ics = new StringBuilder();
        Dotenv dotenv = Dotenv.load();
        ics.append("BEGIN:VCALENDAR").append(NEWLINE);
        ics.append("PRODID:-//MyCompany//MyProduct//EN").append(NEWLINE);
        ics.append("VERSION:2.0").append(NEWLINE);
        ics.append("METHOD:REQUEST").append(NEWLINE);
        ics.append("BEGIN:VEVENT").append(NEWLINE);
        addProperty(ics, "UID", UUID.randomUUID().toString());
        addProperty(ics, "DTSTAMP", format(LocalDateTime.now()));
        addProperty(ics, "DTSTART", format(event.getStartTime()));
        addProperty(ics, "DTEND",   format(event.getEndTime()));
        addProperty(ics, "SUMMARY",     event.getTitle());
        addProperty(ics, "DESCRIPTION", event.getDescription());
        addProperty(ics, "LOCATION",    event.getVirtualRoom());
        String attendeeKey = "ATTENDEE;ROLE=REQ-PARTICIPANT;PARTSTAT=NEEDS-ACTION;RSVP=TRUE;CN=Invitato";
        addProperty(ics, attendeeKey, "mailto:" + recipientEmail);
        ics.append("END:VEVENT").append(NEWLINE);
        ics.append("END:VCALENDAR").append(NEWLINE);
        return ics.toString().getBytes("UTF-8");
    }

    private void addProperty(StringBuilder sb, String key, String value) {
        if (value != null) {
            sb.append(key).append(":").append(value).append(NEWLINE);
        }
    }

    private String format(LocalDateTime date) {
        if (date == null) return "";
        return date.format(ICS_DATE_FORMATTER);
    }
}
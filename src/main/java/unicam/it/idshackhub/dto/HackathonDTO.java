package unicam.it.idshackhub.dto;

import java.time.LocalDateTime;

public record HackathonDTO(
        String title,
        String description,
        Double prize,
        // TeamRules
        int maxTeams,
        int minTeams,
        int maxPlayers,
        int minPlayers,
        // Schedule (Format: "2007-12-03T10:15:30")
        LocalDateTime startReg,
        LocalDateTime startEvent,
        LocalDateTime endEvent,
        String location
) {}
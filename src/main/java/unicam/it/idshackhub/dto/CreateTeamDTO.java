package unicam.it.idshackhub.dto;

public record CreateTeamDTO(
        Long leaderId,
        String name,
        String description,
        String payPalAccount
) {}
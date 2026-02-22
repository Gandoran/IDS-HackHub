package unicam.it.idshackhub.dto;

public record CreateTeamDTO(
        String name,
        String description,
        String payPalAccount
) {}
package unicam.it.idshackhub.dto;

public record CreateUserDTO(
        String username,
        String email,
        String password
) {}
package unicam.it.idshackhub.controller.dtoResponse;

import unicam.it.idshackhub.model.message.ActionStatus;
import unicam.it.idshackhub.model.message.MessageType;

public record MessageResponseDTO(
        Long id,
        UserResponseDTO sender,
        UserResponseDTO recipient,
        String content,
        MessageType type,
        ActionStatus actionStatus
) {}
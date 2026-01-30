package unicam.it.idshackhub.service;

import jakarta.transaction.Transactional;
import unicam.it.idshackhub.model.message.MessageType;
import unicam.it.idshackhub.model.user.User;
import unicam.it.idshackhub.model.user.role.permission.Permission;

import static unicam.it.idshackhub.service.PermissionChecker.checkPermission;

public class UserService {

    private final MessageService messageService;

    public UserService(MessageService messageService){
        this.messageService = messageService;
    }

    @Transactional
    public void sendVerifyRequest(User sender, String content) {
        validateUser(sender);
        messageService.sendMessage(sender, null, MessageType.VERIFY_USER_REQUEST, content, sender.getId());

    }


    private void validateUser(User user) {
        if(!PermissionChecker.checkPermission(user, Permission.Can_Create_Verified_Request)) {
            throw new RuntimeException("Permission denied: Cannot send verification request.");
        }
    }
}

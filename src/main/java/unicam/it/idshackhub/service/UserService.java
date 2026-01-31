package unicam.it.idshackhub.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import unicam.it.idshackhub.model.message.MessageType;
import unicam.it.idshackhub.model.user.User;
import unicam.it.idshackhub.model.user.role.permission.Permission;

import static unicam.it.idshackhub.service.PermissionChecker.checkPermission;

/**
 * Provides operations related to User lifecycle management.
 * <p>
 *     This service manages the creation and processing of User messages.
 * </p>
 */
@Service
public class UserService {

    private final MessageService messageService;

    public UserService(MessageService messageService){
        this.messageService = messageService;
    }

    /**
     * Sends a verification request to visible ONLY to System Admins.
     *
     */
    @Transactional
    public void sendVerifyRequest(User sender, String content) {
        validateUser(sender);
        messageService.sendMessage(sender, null, MessageType.VERIFY_USER_REQUEST, content, sender.getId());

    }

    private void validateUser(User user) {
        if(!checkPermission(user, Permission.Can_Create_Verified_Request)) {
            throw new RuntimeException("Permission denied: Cannot send verification request.");
        }
    }
}

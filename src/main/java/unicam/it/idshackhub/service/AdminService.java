package unicam.it.idshackhub.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import unicam.it.idshackhub.model.message.Message;
import unicam.it.idshackhub.model.user.User;
import unicam.it.idshackhub.model.user.role.permission.Permission;

import static unicam.it.idshackhub.service.PermissionChecker.checkPermission;

/**
 * Provides operations related to Admin-level use cases.
 */
@Service
public class AdminService {

    private final MessageService messageService;

    @Autowired
    public AdminService(MessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * Manages (Accepts/Rejects) a verification request.
     * The MessageService delegates the actual business logic to the appropriate strategy.
     * There are additional checks to ensure that the Admin is allowed to perform this action.
     */
    public Message manageVerificationRequest(User admin, Long messageId, boolean accept) {
        if (!checkPermission(admin, Permission.Can_Manage_Verified_Request)) {
            throw new RuntimeException("Permission denied: You are not an Admin.");
        }
        return messageService.processReply(messageId, accept, admin);
    }
}
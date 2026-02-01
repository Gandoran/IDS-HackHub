package unicam.it.idshackhub.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import unicam.it.idshackhub.exception.PermissionDeniedException;
import unicam.it.idshackhub.model.message.Message;
import unicam.it.idshackhub.model.user.User;
import unicam.it.idshackhub.model.user.role.permission.Permission;
import unicam.it.idshackhub.repository.UserRepository;

import static unicam.it.idshackhub.service.EntityUtils.getEntity;
import static unicam.it.idshackhub.service.PermissionChecker.checkPermission;

/**
 * Provides operations related to Admin-level use cases.
 */
@Service
public class AdminService {

    private final MessageService messageService;
    private final UserRepository userRepository;

    @Autowired
    public AdminService(MessageService messageService,
                        UserRepository userRepository) {
        this.messageService = messageService;
        this.userRepository = userRepository;
    }

    /**
     * Manages (Accepts/Rejects) a verification request.
     * The MessageService delegates the actual business logic to the appropriate strategy.
     * There are additional checks to ensure that the Admin is allowed to perform this action.
     */
    @Transactional
    public Message manageVerificationRequest(Long adminId, Long messageId, boolean accept) {
        User admin = getEntity(userRepository, adminId, "Admin");
        if (!checkPermission(admin, Permission.Can_Manage_Verified_Request)) {
            throw new PermissionDeniedException("You are not an Admin.");
        }
        return messageService.processReply(messageId, accept, admin,null);
    }
}
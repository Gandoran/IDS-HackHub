package unicam.it.idshackhub.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import unicam.it.idshackhub.model.message.ActionStatus;
import unicam.it.idshackhub.model.message.Message;
import unicam.it.idshackhub.model.message.MessageType;
import unicam.it.idshackhub.model.user.User;
import unicam.it.idshackhub.model.user.role.permission.Permission;
import unicam.it.idshackhub.repository.MessageRepository;

import java.util.List;

import static unicam.it.idshackhub.service.PermissionChecker.checkPermission;

@Service
public class AdminService {

    private final MessageRepository messageRepository;
    private final MessageService messageService;

    @Autowired
    public AdminService(MessageRepository messageRepository, MessageService messageService) {
        this.messageRepository = messageRepository;
        this.messageService = messageService;
    }

    /**
     * Returns all pending verification requests for an Admin.
     * Only an admin can see these requests.
     */
    public List<Message> getPendingVerificationRequests(User admin) {
        if (!checkPermission(admin, Permission.Can_Manage_Verified_Request)) {
            throw new RuntimeException("Permission denied: You are not an Admin.");
        }
        return messageRepository.findAllByTypeAndActionStatusAndRecipientIsNull(
                MessageType.VERIFY_USER_REQUEST,
                ActionStatus.PENDING
        );
    }

    /**
     * Manages (Accepts/Rejects) a verification request.
     * The MessageService delegates the actual business logic to the appropriate strategy.
     * There are additional checks to ensure that the Admin is allowed to perform this action.
     */
    public void manageVerificationRequest(User admin, Long messageId, boolean accept) {
        if (!checkPermission(admin, Permission.Can_Manage_Verified_Request)) {
            throw new RuntimeException("Permission denied: You are not an Admin.");
        }

        // Lascia la logica di business al MessageService
        messageService.processReply(messageId, accept, admin);
    }
}
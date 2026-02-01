package unicam.it.idshackhub.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import unicam.it.idshackhub.model.message.Message;
import unicam.it.idshackhub.model.message.MessageType;
import unicam.it.idshackhub.model.team.Team;
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
    public Message sendVerifyRequest(User sender, String content) {
        validateUser(sender);
        return messageService.sendMessage(sender, null, MessageType.VERIFY_USER_REQUEST, content, sender.getId());
    }

    /**
     * Lets the user accept or decline Invites.
     */
    public Message manageInvites(Long messageId, User sender, boolean accept) {
        return messageService.processReply(messageId, accept, sender);
    }

    /**
     * Sends a join request to a team leader.
     */
    @Transactional
    public Message sendJoinRequest(User sender, Team team, String content) {

        if (!checkPermission(sender, Permission.Can_Send_Join_Request)) {
            throw new RuntimeException("Permission denied: Cannot send join request.");
        }
        if (sender.getUserTeam() != null) {
            throw new RuntimeException("You are already in a team");
        }
        return messageService.sendMessage(
                sender,
                team.getLeader(),
                MessageType.JOIN_TEAM_REQUEST,
                content,
                team.getId()
        );
    }

    private void validateUser(User user) {
        if(!checkPermission(user, Permission.Can_Create_Verified_Request)) {
            throw new RuntimeException("Permission denied: Cannot send verification request.");
        }
    }
}

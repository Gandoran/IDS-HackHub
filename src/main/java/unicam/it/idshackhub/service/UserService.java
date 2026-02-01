package unicam.it.idshackhub.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import unicam.it.idshackhub.model.message.Message;
import unicam.it.idshackhub.model.message.MessageType;
import unicam.it.idshackhub.model.team.Team;
import unicam.it.idshackhub.model.user.User;
import unicam.it.idshackhub.model.user.role.permission.Permission;
import unicam.it.idshackhub.repository.TeamRepository;
import unicam.it.idshackhub.repository.UserRepository;

import static unicam.it.idshackhub.service.EntityUtils.getEntity;
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
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;

    @Autowired
    public UserService(MessageService messageService,
                       UserRepository userRepository,
                       TeamRepository teamRepository) {
        this.messageService = messageService;
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
    }

    /**
     * Sends a verification request to visible ONLY to System Admins.
     *
     */
    @Transactional
    public Message sendVerifyRequest(Long userId, String content) {
        User sender = getEntity(userRepository, userId, "User");
        validateUserForVerify(sender);
        return messageService.sendMessage(sender, null, MessageType.VERIFY_USER_REQUEST, content, sender.getId());
    }


    /**
     * Lets the user accept or decline Invites.
     */
    @Transactional
    public Message manageInvites(Long messageId, Long senderId, boolean accept) {
        User sender = getEntity(userRepository, senderId, "Sender");
        return messageService.processReply(messageId, accept, sender,null);
    }

    /**
     * Sends a join request to a team leader.
     */
    @Transactional
    public Message sendJoinRequest(Long userId, Long teamId, String content) {
        User sender = getEntity(userRepository, userId, "User");
        Team team = getEntity(teamRepository, teamId, "Team");

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

    private void validateUserForVerify(User user) {
        if (!checkPermission(user, Permission.Can_Create_Verified_Request)) {
            throw new RuntimeException("Permission denied: Cannot send verification request.");
        }
    }
}

package unicam.it.idshackhub.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import unicam.it.idshackhub.model.hackathon.Hackathon;
import unicam.it.idshackhub.model.message.MessageType;
import unicam.it.idshackhub.model.user.User;
import unicam.it.idshackhub.model.user.role.ContextRole;
import unicam.it.idshackhub.model.user.role.permission.Permission;

import static unicam.it.idshackhub.service.PermissionChecker.checkPermission;
@Service
public class OrganizerService {
    private final MessageService messageService;

    @Autowired
    public OrganizerService(MessageService messageService){
        this.messageService = messageService;
    }

    /*
    public void inviteStaff(User organizer, User recipient, Hackathon hackathon, ContextRole role) {
        if(!checkPermission(organizer, Permission.Can_Invite_Staff,hackathon)){
            throw new RuntimeException("Permission denied");
        }
        if(!hackathon.isActionAllowed(Permission.Can_Invite_Staff)) {
            throw new RuntimeException("Hackathon not in the registration phase");
        }
        if(!recipient.getRoleByContext(hackathon).isPresent()) {
            throw new RuntimeException("User already in the hackathon");
        }

        messageService.sendStaffInvite(organizer, recipient, MessageType.INVITE_STAFF_REQUEST,"Contenuto",  hackathon.getId(), role);
    }*/

    public void inviteStaff(User organizer, User recipient, Hackathon hackathon, ContextRole roleToAssign) {
        if (roleToAssign != ContextRole.H_Judge && roleToAssign != ContextRole.H_Mentor) {
            throw new IllegalArgumentException("Invalid role for invitation. Only Judge or Mentor allowed.");
        }

        if(!checkPermission(organizer, Permission.Can_Invite_Staff, hackathon)){
            throw new RuntimeException("Permission denied: You are not authorized to invite staff.");
        }

        if(!hackathon.isActionAllowed(Permission.Can_Invite_Staff)) {
            throw new RuntimeException("Action not allowed in the current Hackathon phase.");
        }

        if(recipient.getRoleByContext(hackathon).isPresent()) {
            throw new RuntimeException("The user " + recipient.getUsername() + " is already involved in this Hackathon.");
        }

        messageService.sendStaffInvite(
                organizer,
                recipient,
                MessageType.INVITE_STAFF_REQUEST,
                "You have been invited to join the staff as " + roleToAssign.name(),
                hackathon.getId(),
                roleToAssign
        );
    }


}

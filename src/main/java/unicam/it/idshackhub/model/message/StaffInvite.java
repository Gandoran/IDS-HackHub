package unicam.it.idshackhub.model.message;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import unicam.it.idshackhub.model.user.User;
import unicam.it.idshackhub.model.user.role.ContextRole;

@Getter @Setter
@NoArgsConstructor
@Entity
public class StaffInvite extends Message{
    private ContextRole role;

    public StaffInvite(User sender, User recipient, String content, MessageType type, ActionStatus actionStatus,
                       Long referenceId, ContextRole role){
        super(sender, recipient, content, type, actionStatus, referenceId);
        this.role = role;
    }
}

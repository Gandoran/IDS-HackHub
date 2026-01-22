package unicam.it.idshackhub.model.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import unicam.it.idshackhub.model.user.User;
import unicam.it.idshackhub.model.user.role.GlobalRole;

@Getter
@Setter
@AllArgsConstructor
public class Request {
    private long id;
    private User user;
    private String description;

    public void Manage(boolean manage) {
        if (manage) user.setGlobalRole(GlobalRole.G_VerifiedUser);
    }
}

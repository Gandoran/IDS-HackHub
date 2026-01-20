package unicam.it.idshackhub.model.utils;

import unicam.it.idshackhub.model.user.User;
import unicam.it.idshackhub.model.user.role.GlobalRole;

public class Request {
    private long id;
    private User user;
    private String description;

    public Request(User user, String description) {
        this.user = user;
        this.description = description;
    }

    public void Manage(boolean manage) {
        if (manage) user.setGlobalRole(GlobalRole.G_VerifiedUser);
    }
}

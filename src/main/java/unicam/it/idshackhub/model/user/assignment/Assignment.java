package unicam.it.idshackhub.model.user.assignment;

import unicam.it.idshackhub.model.user.role.Role;

public class Assignment {
    private BaseContext context;
    private Role role;

    public Role getRole() {
        return role;
    }

    public BaseContext getContext() {
        return context;
    }
}

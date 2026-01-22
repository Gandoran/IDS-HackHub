package unicam.it.idshackhub.model.user.role;

import unicam.it.idshackhub.model.user.role.permission.Permission;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public enum GlobalRole implements Role {
    G_VerifiedUser(EnumSet.of(Permission.Can_Create_Hackathon,Permission.Can_Create_Team)),
    G_SystemAdmin(EnumSet.of(Permission.Can_Manage_Request)),
    G_NormalUser(EnumSet.of(Permission.Can_Create_Request,Permission.Can_Create_Team));

    private final Set<Permission> permissions;

    GlobalRole(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    @Override
    public List<Permission> getListPermission() {
        return List.copyOf(this.permissions);
    }
    @Override
    public boolean hasPermission(Permission permission) {
        return this.permissions.contains(permission);
    }
}

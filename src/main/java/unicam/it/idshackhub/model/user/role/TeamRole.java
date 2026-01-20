package unicam.it.idshackhub.model.user.role;

import unicam.it.idshackhub.model.user.role.permission.Permission;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public enum TeamRole implements Role {
    T_TeamMember(EnumSet.noneOf(Permission.class)),
    T_TeamLeader(EnumSet.of(Permission.Can_Register_Team, Permission.Can_Create_HackathonTeam));

    private final Set<Permission> permissions;

    TeamRole(Set<Permission> permissions) {
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

package unicam.it.idshackhub.model.user.role;

import unicam.it.idshackhub.model.user.role.permission.Permission;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public enum HackathonRole implements Role {
    H_Judge(EnumSet.of(Permission.Can_Vote,Permission.Can_End_Evaluation_State)),
    H_Organizer(EnumSet.of(Permission.Can_Proclamate_Winner,Permission.Can_Invite_Judge)),
    H_HackathonTeamMember(EnumSet.noneOf(Permission.class)),
    H_HackathonTeamLeader(EnumSet.of(Permission.Can_Submit));

    private final Set<Permission> permissions;

    HackathonRole(Set<Permission> permissions) {
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

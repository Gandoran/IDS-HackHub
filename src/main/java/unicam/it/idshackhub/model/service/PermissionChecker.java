package unicam.it.idshackhub.model.service;

import unicam.it.idshackhub.model.user.User;
import unicam.it.idshackhub.model.user.assignment.Context;
import unicam.it.idshackhub.model.user.role.permission.Permission;

public final class PermissionChecker {
    public static boolean checkPermission(User user, Permission perm, Context context) {
        if (user == null || perm == null) return false;
        return user.getRoleByContext(context)
                .map(role -> role.hasPermission(perm))
                .orElse(false);
    }

    public static boolean checkPermission(User user, Permission perm) {
        if (user == null || perm == null) return false;
        return user.getGlobalRole().hasPermission(perm);
    }
}

package unicam.it.idshackhub.model.service;

import unicam.it.idshackhub.model.user.User;
import unicam.it.idshackhub.model.user.assignment.Context;
import unicam.it.idshackhub.model.user.role.permission.Permission;

/**
 * Utility class responsible for verifying User permissions within the system.
 * <p>
 * This class provides static methods to check if a user possesses a specific privilege,
 * either globally (based on their GlobalRole) or within a specific context
 * (based on their assignment in a Team or Hackathon).
 * </p>
 */
public final class PermissionChecker {

    /**
     * Checks if a user has a specific permission within a given context.
     * <p>
     * It retrieves the user's role associated with the provided context (e.g., a specific Team or Hackathon)
     * and verifies if that role includes the requested permission.
     * </p>
     *
     * @param user    the user whose permission is being checked.
     * @param perm    the permission to verify.
     * @param context the context (Hackathon or Team) in which the permission is required.
     * @return {@code true} if the user has the permission in the given context; {@code false} otherwise.
     */
    public static boolean checkPermission(User user, Permission perm, Context context) {
        if (user == null || perm == null) return false;
        return user.getRoleByContext(context)
                .map(role -> role.hasPermission(perm))
                .orElse(false);
    }

    /**
     * Checks if a user has a specific global permission.
     * <p>
     * It validates the permission against the user's {@link unicam.it.idshackhub.model.user.role.GlobalRole}.
     * </p>
     *
     * @param user the user whose permission is being checked.
     * @param perm the permission to verify.
     * @return {@code true} if the user has the global permission; {@code false} otherwise.
     */
    public static boolean checkPermission(User user, Permission perm) {
        if (user == null || perm == null) return false;
        return user.getGlobalRole().hasPermission(perm);
    }
}
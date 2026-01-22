package unicam.it.idshackhub.model.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import unicam.it.idshackhub.model.user.User;
import unicam.it.idshackhub.model.user.role.GlobalRole;

/**
 * Represents a formal administrative request submitted by a User.
 * <p>
 * Typically, this class is used when a {@link User} (usually a {@code G_NormalUser})
 * requests an account upgrade to become a {@code G_VerifiedUser}. The request includes
 * a justification description and waits for administrator approval via the {@link #Manage(boolean)} method.
 * </p>
 */
@Getter
@Setter
@AllArgsConstructor
public class Request {

    /**
     * The unique identifier for this specific request.
     */
    private long id;

    /**
     * The user who submitted the request.
     */
    private User user;

    /**
     * A brief explanation or justification provided by the user regarding why the request should be approved.
     */
    private String description;

    /**
     * Processes the outcome of the request.
     * <p>
     * This method is typically called by a System Administrator via the
     * {@link unicam.it.idshackhub.model.service.SystemService}. If the request is approved
     * ({@code manage} is true), the requesting User is automatically promoted to the
     * {@link GlobalRole#G_VerifiedUser} role.
     * </p>
     *
     * @param manage {@code true} to approve the request and upgrade the user; {@code false} to deny (no state change).
     */
    public void Manage(boolean manage) {
        if (manage) user.setGlobalRole(GlobalRole.G_VerifiedUser);
    }
}
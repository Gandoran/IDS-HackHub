package unicam.it.idshackhub.model.utils;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import unicam.it.idshackhub.model.user.User;
import unicam.it.idshackhub.model.user.role.GlobalRole;
import unicam.it.idshackhub.service.SystemService;

/**
 * Represents a formal administrative request submitted by a User.
 * <p>
 * Typically, this class is used when a {@link User} (usually a {@code G_NormalUser})
 * requests an account upgrade to become a {@code G_VerifiedUser}. The request includes
 * a justification description and waits for administrator approval via the {@link #Manage(boolean)} method.
 * </p>
 */
@Entity
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class Request {

    /**
     * The unique identifier for this specific request.
     */
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * The user who submitted the request.
     */
    @ManyToOne @JoinColumn(name = "user_id")
    private User user;

    /**
     * A brief explanation or justification provided by the user regarding why the request should be approved.
     */
    private String description;

    public Request(User user, String description) {
        this.user = user;
        this.description = description;
    }

    /**
     * Processes the outcome of the request.
     * <p>
     * This method is typically called by a System Administrator via the
     * {@link SystemService}. If the request is approved
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
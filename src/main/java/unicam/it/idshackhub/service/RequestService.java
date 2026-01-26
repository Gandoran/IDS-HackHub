package unicam.it.idshackhub.service;

import org.springframework.stereotype.Service;
import unicam.it.idshackhub.model.user.User;
import unicam.it.idshackhub.model.user.role.permission.Permission;
import unicam.it.idshackhub.model.utils.Request;
import unicam.it.idshackhub.repository.RequestRepository;
import unicam.it.idshackhub.repository.UserRepository;

import static unicam.it.idshackhub.service.PermissionChecker.checkPermission;

/**
 * Manages user requests within the system.
 * <p>
 * Requests can be created by authorized users and managed by administrators. Managing a request typically
 * results in a decision (accepted/rejected) and updates the related user accordingly.
 * </p>
 */

@Service
public class RequestService {

    private final UserRepository userRepository;
    private final RequestRepository requestRepository;

    /**
     * Creates the service.
     *
     * @param userRepository repository used to persist user updates triggered by request management.
     * @param requestRepository repository used to persist and delete requests.
     */

    public RequestService(UserRepository userRepository, RequestRepository requestRepository) {
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
    }

    /**
     * Creates a new request for the given user.
     *
     * @param user the user creating the request.
     * @param description the request description.
     * @return the persisted request.
     * @throws RuntimeException if the user does not have {@link unicam.it.idshackhub.model.user.role.permission.Permission#Can_Create_Request}.
     */

    public Request createRequest(User user, String description) {
        if (!checkPermission(user, Permission.Can_Create_Request)) {
            throw new RuntimeException("Permission denied");
        }
        return requestRepository.save(new Request(user, description));
    }

    /**
     * Manages (accepts or rejects) a request.
     * <p>
     * After being managed, the request is removed and the related user is persisted with the updated state.
     * </p>
     *
     * @param admin the administrator managing the request.
     * @param request the request to manage.
     * @param manage {@code true} to accept the request; {@code false} to reject it.
     * @return the decision applied ({@code true} if accepted; {@code false} otherwise).
     * @throws RuntimeException if the administrator does not have {@link unicam.it.idshackhub.model.user.role.permission.Permission#Can_Manage_Request}.
     */

    public boolean manageRequest(User admin, Request request, boolean manage) {
        if (!checkPermission(admin, Permission.Can_Manage_Request)) {
            throw new RuntimeException("Permission denied");
        }
        request.Manage(manage);
        requestRepository.delete(request);
        userRepository.save(request.getUser());
        return manage;
    }

}

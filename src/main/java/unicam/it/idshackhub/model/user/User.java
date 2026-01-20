package unicam.it.idshackhub.model.user;

import unicam.it.idshackhub.model.user.assignment.Assignment;
import unicam.it.idshackhub.model.user.assignment.Context;
import unicam.it.idshackhub.model.user.role.GlobalRole;
import unicam.it.idshackhub.model.user.role.Role;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class User {
    private long id;
    private String username;
    private String email;
    private String passwordHash;
    private GlobalRole globalRole;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public GlobalRole getGlobalRole() {
        return globalRole;
    }

    public void setGlobalRole(GlobalRole globalRole) {
        this.globalRole = globalRole;
    }

    public List<Assignment> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<Assignment> assignments) {
        this.assignments = assignments;
    }

    private List<Assignment> assignments = new ArrayList<>();

    public Optional<Role> getRoleByContext(Context context) {
        return assignments.stream()
                .filter(a -> context.equals(a.getContext()))
                .map(a -> (Role) a.getRole())
                .findFirst();
    }

    public Optional<Context> getContextByRole(Role role) {
        return assignments.stream()
                .filter(a -> role.equals(a.getRole()))
                .map(Assignment::getContext)
                .map(Context.class::cast)
                .findFirst();
    }
}

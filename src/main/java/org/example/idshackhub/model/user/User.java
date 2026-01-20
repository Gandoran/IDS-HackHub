package org.example.idshackhub.model.user;

import org.example.idshackhub.model.user.assignment.Assignment;
import org.example.idshackhub.model.user.assignment.Context;
import org.example.idshackhub.model.user.role.GlobalRole;
import org.example.idshackhub.model.user.role.Role;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class User {
    private long id;
    private String username;
    private String email;
    private String passwordHash;
    private GlobalRole globalRole;
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

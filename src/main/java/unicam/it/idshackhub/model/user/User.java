package unicam.it.idshackhub.model.user;

import lombok.Getter;
import lombok.Setter;
import unicam.it.idshackhub.model.team.Team;
import unicam.it.idshackhub.model.user.assignment.Assignment;
import unicam.it.idshackhub.model.user.assignment.Context;
import unicam.it.idshackhub.model.user.role.GlobalRole;
import unicam.it.idshackhub.model.user.role.Role;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
public class User {
    private long id;
    private String username;
    private String email;
    private String passwordHash;
    private GlobalRole globalRole;
    private List<Assignment> assignments;
    private Team userTeam;

    public User(long id,String username,String email,String passwordHash){
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.globalRole = GlobalRole.G_NormalUser;
        this.assignments = new ArrayList<>();
        this.userTeam = null;
    }

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

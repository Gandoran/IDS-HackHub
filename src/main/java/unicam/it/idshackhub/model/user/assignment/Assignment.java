package unicam.it.idshackhub.model.user.assignment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import unicam.it.idshackhub.model.user.role.Role;

@Getter
@Setter
@AllArgsConstructor
public class Assignment {
    private BaseContext context;
    private Role role;
}

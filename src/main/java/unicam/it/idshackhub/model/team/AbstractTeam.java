package unicam.it.idshackhub.model.team;

import lombok.Getter;
import lombok.Setter;
import unicam.it.idshackhub.model.user.User;
import unicam.it.idshackhub.model.user.assignment.BaseContext;
import unicam.it.idshackhub.model.user.assignment.Context;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public abstract class AbstractTeam extends BaseContext {
    private String name;
    private String description;
    private User leader;
    private List<User> members = new ArrayList<>();
}

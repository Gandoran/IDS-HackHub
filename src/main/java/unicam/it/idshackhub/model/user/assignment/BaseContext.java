package unicam.it.idshackhub.model.user.assignment;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class BaseContext implements Context {
    private long id;
}

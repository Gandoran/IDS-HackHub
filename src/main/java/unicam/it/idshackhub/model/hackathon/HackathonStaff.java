package unicam.it.idshackhub.model.hackathon;

import lombok.Getter;
import lombok.Setter;
import unicam.it.idshackhub.model.user.User;
import unicam.it.idshackhub.model.user.role.HackathonRole;

@Setter
@Getter
public class HackathonStaff {
    private User organizer;
    private User judge;

    public HackathonStaff(User organizer) {
        this.organizer = organizer;
    }

}

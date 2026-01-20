package unicam.it.idshackhub.model.hackathon;

import unicam.it.idshackhub.model.user.User;
import unicam.it.idshackhub.model.user.role.HackathonRole;

public class HackathonStaff {
    private User organizer;
    private User judge;

    public HackathonStaff(){};

    public void setOrganizer(User organizer) {
        this.organizer = organizer;
    }

    public void setJudge(User judge) {
        this.judge = judge;
    }

    public User getOrganizer() {
        return organizer;
    }

    public User getJudge() {
        return judge;
    }
}

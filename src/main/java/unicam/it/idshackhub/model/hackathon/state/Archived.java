package unicam.it.idshackhub.model.hackathon.state;

import unicam.it.idshackhub.model.hackathon.Hackathon;
import unicam.it.idshackhub.model.user.role.permission.Permission;

public class Archived implements HackathonState{

    @Override
    public boolean isActionAllowed(Permission perm) {
        return false;
    }

    @Override
    public void updateState(Hackathon context) {
        //final state no more updates
    }
}

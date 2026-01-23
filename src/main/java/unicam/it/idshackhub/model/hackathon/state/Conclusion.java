package unicam.it.idshackhub.model.hackathon.state;

import unicam.it.idshackhub.model.hackathon.Hackathon;
import unicam.it.idshackhub.model.user.role.permission.Permission;

public class Conclusion implements HackathonState {

    @Override
    public boolean isActionAllowed(Permission perm) {
        return switch (perm) {
            case Can_Proclamate_Winner -> true;
            default -> false;
        };
    }

    @Override
    public void updateState(Hackathon context) {
        // final state: no automatic transitions
    }
}
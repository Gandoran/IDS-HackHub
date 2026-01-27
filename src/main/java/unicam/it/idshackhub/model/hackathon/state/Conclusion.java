package unicam.it.idshackhub.model.hackathon.state;

import unicam.it.idshackhub.model.hackathon.Hackathon;
import unicam.it.idshackhub.model.user.role.permission.Permission;

/**
 * Final phase of a hackathon lifecycle.
 * <p>
 * In this phase the winner can be proclaimed and no automatic transitions occur.
 * </p>
 */

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
        //TODO CONTROLLO SE SONO STATI INVIATI SOLDI
        context.setStatus(HackathonStatus.ARCHIVED);
    }
}
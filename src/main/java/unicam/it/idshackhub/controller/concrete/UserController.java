package unicam.it.idshackhub.controller.concrete;

import unicam.it.idshackhub.controller.AbstractController;
import unicam.it.idshackhub.model.user.User;

public class UserController extends AbstractController<User> {
    public UserController() {super(User.class);}
}

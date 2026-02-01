package unicam.it.idshackhub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import unicam.it.idshackhub.model.user.User;
import unicam.it.idshackhub.service.UserService;

import java.util.Map;

@RestController
public class HomeController {

    @Autowired
    private UserService userService;

    @GetMapping("/user-info")
    public Map<String, Object> getUserInfo(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) return Map.of("error", "Not logged in");

        String githubId = String.valueOf(principal.getAttributes().get("id"));
        String login = principal.getAttribute("login");
        String email = principal.getAttribute("email");
        String avatar = principal.getAttribute("avatar_url");

        User dbUser = userService.processOAuthPostLogin(githubId, login, email);


        return Map.of(
                "name", dbUser.getUsername(),
                "db_id", dbUser.getId(),
                "role", dbUser.getGlobalRole(),
                "avatar", avatar != null ? avatar : ""
        );
    }
}